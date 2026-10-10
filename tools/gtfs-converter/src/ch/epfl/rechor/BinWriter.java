package ch.epfl.rechor;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class BinWriter {
    private final Path dir;

    public BinWriter(Path dir) {
        this.dir = dir;
    }

    private static DataOutputStream newBufferedDataOutputStream(Path stopsPath) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(stopsPath)));
    }

    private Path createdDateDir(LocalDate date) throws IOException {
        var dateDir = dir.resolve(date.toString());
        Files.createDirectories(dateDir);
        return dateDir;
    }

    public void writeStringPool(StringPool stringPool, Charset charset) throws IOException {
        try (var s = Files.newBufferedWriter(dir.resolve("strings.txt"), charset)) {
            var stringsIt = stringPool.strings().iterator();
            while (stringsIt.hasNext()) {
                s.write(stringsIt.next());
                s.write('\n');
            }
        }
    }

    public void writeStations(List<Station> stations, StringPool stringPool) throws IOException {
        try (var s = newBufferedDataOutputStream(dir.resolve("stations.bin"))) {
            for (var station : stations) {
                s.writeShort(stringPool.idOf(station.name()));
                s.writeInt(station.lon());
                s.writeInt(station.lat());
            }
        }
    }

    public void writeStationAliases(List<Station> stations, StringPool stringPool) throws IOException {
        try (var s = newBufferedDataOutputStream(dir.resolve("station-aliases.bin"))) {
            for (var station : stations) {
                for (var alias : station.aliases()) {
                    s.writeShort(stringPool.idOf(alias));
                    s.writeShort(stringPool.idOf(station.name()));
                }
            }
        }
    }

    public void writePlatforms(List<Platform> platforms, StringPool stringPool, List<Station> stations) throws IOException {
        var stationsIndex = IntStream.range(0, stations.size())
                .boxed()
                .collect(Collectors.toMap(stations::get, Function.identity(), (s1, s2) -> s1));
        try (var s = newBufferedDataOutputStream(dir.resolve("platforms.bin"))) {
            for (var platform : platforms) {
                s.writeShort(stringPool.idOf(platform.code()));
                s.writeShort(stationsIndex.get(platform.parent()));
            }
        }
    }

    public void writeTrips(LocalDate date, List<Trip> trips, StringPool stringPool) throws IOException {
        var dateDir = createdDateDir(date);
        try (var s = newBufferedDataOutputStream(dateDir.resolve("trips.bin"))) {
            for (var trip : trips) {
                // TODO write all data (and write routes, so the given id is useful).
                s.writeShort(trip.routeId());
                s.writeShort(stringPool.idOf(trip.headSign()));
            }
        }
    }

    public void writeRoutes(List<Route> routes, StringPool stringPool) throws IOException {
        try (var s = newBufferedDataOutputStream(dir.resolve("routes.bin"))) {
            for (var route : routes) {
                s.writeShort(stringPool.idOf(route.longName()));
                s.writeByte(route.kind().ordinal());
            }
        }
    }

    public void writeTransfers(List<Transfer> transfers) throws IOException {
        try (var s = newBufferedDataOutputStream(dir.resolve("transfers.bin"))) {
            for (var transfer : transfers) {
                s.writeShort(transfer.depStationId());
                s.writeShort(transfer.arrStationId());
                s.writeByte(transfer.minutes());
            }
        }
    }

    public void writeConnections(LocalDate date, List<Connection> connections) throws IOException {
        var dateDir = createdDateDir(date);
        try (var s = newBufferedDataOutputStream(dateDir.resolve("connections.bin"))) {
            for (var connection : connections) {
                s.writeShort(connection.depStopId());
                s.writeShort(connection.depMinutes());
                s.writeShort(connection.arrStopId());
                s.writeShort(connection.arrMinutes());
                var tripPosId = (connection.tripId() << 8) | connection.tripPos();
                s.writeInt(tripPosId);
            }
        }
    }

    private static int tripAndPos(int tripId, int tripPos) {
        return (tripId << 8) | tripPos;
    }

    private static int tripAndPos(Connection connection) {
        return tripAndPos(connection.tripId(), connection.tripPos());
    }

    public void writeConnectionsSucc(LocalDate date, List<Connection> connections) throws IOException {
        var index = IntStream.range(0, connections.size())
                .boxed()
                .collect(Collectors.toMap(i -> tripAndPos(connections.get(i)), Function.identity()));

        var dateDir = createdDateDir(date);
        try (var s = newBufferedDataOutputStream(dateDir.resolve("connections-succ.bin"))) {
            for (var c : connections) {
                var succIndex = index.get(tripAndPos(c.tripId(), c.tripPos() + 1));
                if (succIndex == null) succIndex = index.get(tripAndPos(c.tripId(), 0));
                s.writeInt(succIndex);
            }
        }
    }
}
