package ch.epfl.rechor;

import de.siegmar.fastcsv.reader.CsvReader;

import java.io.IOException;
import java.nio.file.Path;
import java.text.Collator;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class GtfsReader {
    private static final DateTimeFormatter GTFS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Path gtfsDir;

    public GtfsReader(Path gtfsDir) {
        this.gtfsDir = gtfsDir;
    }

    public Map<LocalDate, Set<String>> activeServices() throws IOException {
        var activeServices = new TreeMap<LocalDate, Set<String>>();

        var calendarPath = gtfsDir.resolve("calendar.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(calendarPath)) {
            for (var r : reader) {
                var serviceId = r.getField("service_id");
                var weekDays = EnumSet.noneOf(DayOfWeek.class);
                for (var dayOfWeek : DayOfWeek.values()) {
                    if (r.getField(dayOfWeek.name().toLowerCase()).equals("1"))
                        weekDays.add(dayOfWeek);
                }

                var startDate = LocalDate.parse(r.getField("start_date"), GTFS_DATE_FORMAT);
                var endDate = LocalDate.parse(r.getField("end_date"), GTFS_DATE_FORMAT);
                startDate.datesUntil(endDate.plusDays(1))
                        .filter(d -> weekDays.contains(d.getDayOfWeek()))
                        .forEach(d ->
                                activeServices
                                        .computeIfAbsent(d, k -> new TreeSet<>())
                                        .add(serviceId));
            }
        }

        var calendarDatesPath = gtfsDir.resolve("calendar_dates.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(calendarDatesPath)) {
            for (var r : reader) {
                var serviceId = r.getField("service_id");
                var date = LocalDate.parse(r.getField("date"), GTFS_DATE_FORMAT);
                var exceptionType = r.getField("exception_type");
                switch (exceptionType) {
                    case "1" -> {
                        activeServices
                                .computeIfAbsent(date, k -> new TreeSet<>())
                                .add(serviceId);
                    }
                    case "2" -> {
                        activeServices
                                .computeIfAbsent(date, k -> new TreeSet<>())
                                .remove(serviceId);
                    }
                    default ->
                            throw new IllegalArgumentException("Invalid exception type: " + exceptionType);
                }
            }
        }
        return Map.copyOf(activeServices);
    }

    public IdValues<Station> readStations() throws IOException {
        var stopsPath = gtfsDir.resolve("stops.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(stopsPath)) {
            var stationsB = new IdValues.Builder<Station>();
            for (var r : reader) {
                if (!r.getField("parent_station").isBlank()) continue;
                var name = r.getField("stop_name");
                var lon = convertLonLat(r.getField("stop_lon"));
                var lat = convertLonLat(r.getField("stop_lat"));
                var aliases = IntStream.range(1, 5)
                        .mapToObj(i -> "ch_station_synonym" + i)
                        .map(r::getField)
                        .filter(a -> !a.isBlank())
                        .toList();
                stationsB.add(r.getField("stop_id"), new Station(name, aliases, lon, lat));
            }
            stationsB.sort(Comparator.comparing(Station::name, Collator.getInstance(Locale.FRENCH)));
            return stationsB.build();
        }
    }

    private static final double ONE_OVER_ONE_HUNDRED_EIGHTY = 1d / 180d;

    private static int convertLonLat(String angleS) {
        var angleDeg = Double.parseDouble(angleS);
        assert -180d <= angleDeg && angleDeg < 180d;
        var angleI = Math.scalb(angleDeg * ONE_OVER_ONE_HUNDRED_EIGHTY, 31);
        return (int) angleI;
    }

    public IdValues<Platform> readPlatforms(IdValues<Station> stops) throws IOException {
        var stopsPath = gtfsDir.resolve("stops.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(stopsPath)) {
            var platformsB = new IdValues.Builder<Platform>();
            for (var r : reader) {
                var parentStation = r.getField("parent_station");
                if (parentStation.isBlank()) continue;
                var station = stops.get(stops.indexOf(parentStation));
                var platformCode = r.getField("platform_code");
                platformsB.add(r.getField("stop_id"), new Platform(station, platformCode));
            }
            return platformsB.build();
        }
    }

    public IdValues<Route> readRoutes() throws IOException {
        var routesPath = gtfsDir.resolve("routes.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(routesPath)) {
            var routesB = new IdValues.Builder<Route>();
            for (var r : reader) {
                var id = r.getField("route_id");
                var kind = switch (r.getField("route_type")) {
                    case "0" -> Route.Kind.TRAM;
                    case "1" -> Route.Kind.METRO;
                    case "2" -> Route.Kind.TRAIN;
                    case "3" -> Route.Kind.BUS;
                    case "4" -> Route.Kind.FERRY;
                    case "6" -> Route.Kind.AERIAL_LIFT;
                    case "7" -> Route.Kind.FUNICULAR;
                    default -> throw new Error("unknown route type: " + r.getField("route_type"));
                };
                var longName = r.getField("route_long_name");
                routesB.add(id, new Route(kind, longName));
            }

            routesB.deduplicate();
            routesB.sort(Comparator.comparing(Route::longName));
            return routesB.build();
        }
    }

    public Set<String> readTripHeadSigns() throws IOException {
        var tripsPath = gtfsDir.resolve("trips.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(tripsPath)) {
            return reader.stream()
                    .map(r -> r.getField("trip_headsign"))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    public IdValues<Trip> readTrips(IdValues<Route> routes, Set<String> activeServices)
            throws IOException {
        var tripsPath = gtfsDir.resolve("trips.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(tripsPath)) {
            var tripsB = new IdValues.Builder<Trip>();
            reader.stream()
                    .filter(r -> activeServices.contains(r.getField("service_id")))
                    .forEach(r -> {
                        var id = r.getField("trip_id");
                        var routeIndex = routes.indexOf(r.getField("route_id"));
                        var headSign = r.getField("trip_headsign");
                        var attributesCh = Set.of(r.getField("attributes_ch").split(";"));
                        tripsB.add(id, new Trip((short) routeIndex, headSign, attributesCh));
                    });
            return tripsB.build();
        }
    }

    private static short convertTime(String time) {
        var hms = time.split(Pattern.quote(":"));
        return (short) (Integer.parseInt(hms[0]) * 60 + Integer.parseInt(hms[1]));
    }

    public List<Connection> readConnections(IdValues<Station> stations,
                                            IdValues<Platform> platforms,
                                            IdValues<Trip> trips)
            throws IOException {
        var stopTimesPath = gtfsDir.resolve("stop_times.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(stopTimesPath)) {
            var connections = new ArrayList<Connection>();

            var prevStopId = 0;
            var prevDepMinutes = (short) 0;
            var prevTripId = 0;
            var connectionTripPos = -1;
            for (var r : reader) {
                var tripId = trips.maybeIndexOf(r.getField("trip_id"));
                if (tripId == -1) continue;

                var departureTime = r.getField("departure_time");
                if (departureTime.isBlank()) {
                    assert r.getField("arrival_time").isBlank()
                           && !r.getField("stop_sequence").equals("0");
                    continue;
                }

                var stopIdS = r.getField("stop_id");
                var stopId = stations.containsId(stopIdS)
                        ? stations.indexOf(stopIdS)
                        : stations.size() + platforms.indexOf(stopIdS);
                assert (stopId & 0xFFFF) == stopId;
                var stopSequence = Integer.parseInt(r.getField("stop_sequence"));
                var depMinutes = convertTime(departureTime);
                if (stopSequence != 0) {
                    assert tripId == prevTripId;
                    var arrMinutes = convertTime(r.getField("arrival_time"));
                    var connection = new Connection(
                            prevStopId,
                            prevDepMinutes,
                            stopId,
                            arrMinutes,
                            tripId,
                            (byte) connectionTripPos++);
                    connections.add(connection);
                } else connectionTripPos = 0;
                prevStopId = stopId;
                prevDepMinutes = depMinutes;
                prevTripId = tripId;
            }

            connections.sort(Comparator
                    .comparingInt(Connection::depMinutes)
                    .thenComparingInt(Connection::tripPos)
                    .reversed());

            return connections;
        }
    }

    public List<Transfer> readTransfers(IdValues<Station> stations) throws IOException {
        var transfersPath = gtfsDir.resolve("transfers.txt");
        try (var reader = CsvReader.builder().ofNamedCsvRecord(transfersPath)) {
            record StationPair(int fromStationId, int toStationId) { }

            var map = reader.stream()
                    .filter(r -> r.getField("transfer_type").equals("2"))
                    .map(r -> {
                        var fromStationId = stations.indexOf(r.getField("from_stop_id"));
                        var toStationId = stations.indexOf(r.getField("to_stop_id"));
                        var minTransferMinutes = (Integer.parseInt(r.getField("min_transfer_time")) / 60);
                        return Map.entry(new StationPair(fromStationId, toStationId), minTransferMinutes);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Math::max));

            return map.entrySet().stream()
                    .map(e -> new Transfer(e.getKey().fromStationId, e.getKey().toStationId, e.getValue()))
                    .sorted(Comparator.comparingInt(Transfer::arrStationId).thenComparingInt(Transfer::depStationId))
                    .toList();
        }
    }
}
