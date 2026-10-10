package ch.epfl.rechor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.printf("Usage: java %s <gtfs_dir> <out_dir> <date/range>...\n", Main.class.getName());
            System.exit(1);
        }

        var gtfsDir = Path.of(args[0]);
        var outDir = Path.of(args[1]);
        var allDates = Arrays.stream(args, 2, args.length)
                .flatMap(d -> {
                    var ds = d.split(Pattern.quote("…"), -1);
                    return switch (ds.length) {
                        case 1 -> Stream.of(LocalDate.parse(ds[0]));
                        case 2 -> LocalDate.parse(ds[0]).datesUntil(LocalDate.parse(ds[1]));
                        default -> throw new Error("invalid date or date range: " + d);
                    };
                })
                .distinct()
                .toList();

        var nsStart = System.nanoTime();

        Files.createDirectories(outDir);

        var gtfsReader = new GtfsReader(gtfsDir);
        var activeServices = gtfsReader.activeServices();
        var stations = gtfsReader.readStations();
        var platforms = gtfsReader.readPlatforms(stations);
        var transfers = gtfsReader.readTransfers(stations);
        var routes = gtfsReader.readRoutes();

        var stationsL = stations.values();
        var platformsL = platforms.values();
        var routesL = routes.values();

        var stringPoolB = new StringPool.Builder();
        for (var s : stationsL) {
            stringPoolB.add(s.name());
            s.aliases().forEach(stringPoolB::add);
        }
        for (var p : platformsL) stringPoolB.add(p.code());
        for (var r : routesL) stringPoolB.add(r.longName());
        stringPoolB.addAll(gtfsReader.readTripHeadSigns());
        var stringPool = stringPoolB.build();
        System.err.printf("INFO: string table contains %,d entries\n", stringPool.size());

        var binWriter = new BinWriter(outDir);

        // Strings
        binWriter.writeStringPool(stringPool, StandardCharsets.ISO_8859_1);

        // Routes
        binWriter.writeRoutes(routesL, stringPool);
        System.err.printf("INFO: %,d routes\n", routesL.size());

        // Stops and platforms
        binWriter.writeStations(stationsL, stringPool);
        binWriter.writeStationAliases(stationsL, stringPool);
        binWriter.writePlatforms(platformsL, stringPool, stationsL);
        System.err.printf("INFO: %,d stations, %,d platforms\n", stationsL.size(), platformsL.size());

        // Transfers
        binWriter.writeTransfers(transfers);
        System.err.printf("INFO: %,d transfers\n", transfers.size());

        // Trips and connections (for every day in the given range)
        allDates.parallelStream()
                .forEach(d -> {
                    try {
                        var trips = gtfsReader.readTrips(routes, activeServices.get(d));
                        var connections = gtfsReader.readConnections(stations, platforms, trips);
                        System.err.printf("INFO: %s => %,d trips, %,d connections\n", d, trips.size(), connections.size());
                        binWriter.writeTrips(d, trips.values(), stringPool);
                        binWriter.writeConnections(d, connections);
                        binWriter.writeConnectionsSucc(d, connections);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });

        var nsEnd = System.nanoTime();
        System.err.printf("INFO: done in %.1f s\n", (nsEnd - nsStart) * 1e-9);
    }
}
