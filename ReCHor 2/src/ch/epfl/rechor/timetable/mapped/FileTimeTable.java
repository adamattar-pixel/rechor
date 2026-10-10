package ch.epfl.rechor.timetable.mapped;

import ch.epfl.rechor.timetable.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

/**
 * Représente un emploi du temps (TimeTable) basé sur des fichiers.
 * Cette classe lit les différents fichiers binaires et textuels situés dans un répertoire
 * afin de construire l'ensemble des données nécessaires à la représentation d'un emploi du temps.
 * Les données lues incluent la table de chaînes, les stations, les alias de stations,
 * les plateformes, les trajets, les correspondances et les voyages.
 */
public record FileTimeTable(Path directory,
                            List<String> stringTable,
                            Stations stations,
                            StationAliases stationAliases,
                            Platforms platforms,
                            Routes routes,
                            Transfers transfers) implements TimeTable {

    /**
     * Crée un emploi du temps à partir d'un répertoire contenant les fichiers de données.
     * Cette méthode lit le fichier "strings.txt" pour construire la table de chaînes,
     * et ouvre les différents fichiers binaires (stations, alias de stations, plateformes,
     * trajets et correspondances) pour construire les instances correspondantes.
     *
     * @param directory : le répertoire contenant les fichiers de données
     * @return un emploi du temps construit à partir des données du répertoire
     * @throws IOException si une erreur survient lors de la lecture des fichiers
     */
    public static TimeTable in(Path directory) throws IOException {
        List<String> stringTable = List.copyOf(Files.readAllLines(directory
                .resolve("strings.txt"), StandardCharsets.ISO_8859_1));

        try(FileChannel stationsChannel = FileChannel.open(directory
                .resolve("stations.bin"));
            FileChannel stationAliasesChannel = FileChannel.open(directory
                    .resolve("station-aliases.bin"));
            FileChannel platformsChannel = FileChannel.open(directory
                    .resolve("platforms.bin"));
            FileChannel routesChannel = FileChannel.open(directory
                    .resolve("routes.bin"));
            FileChannel transfersChannel = FileChannel.open(directory
                    .resolve("transfers.bin"));)
        {
            Stations stations = new BufferedStations(stringTable
                    , getMapped(stationsChannel));
            StationAliases stationAliases = new BufferedStationAliases(stringTable
                    , getMapped(stationAliasesChannel));
            Platforms platforms = new BufferedPlatforms(stringTable
                    , getMapped(platformsChannel));
            Routes routes = new BufferedRoutes(stringTable
                    , getMapped(routesChannel));
            Transfers transfers  = new BufferedTransfers(
                    getMapped(transfersChannel));
            return new FileTimeTable(directory, stringTable, stations, stationAliases
                    , platforms, routes, transfers);
        }
    }

    //mess check 55
    private static MappedByteBuffer getMapped(FileChannel file) throws IOException {
        return file.map(READ_ONLY, 0, file.size());
    }

    /**
     * Retourne les voyages (trips) pour une date donnée.
     * Cette méthode ouvre le fichier "trips.bin" correspondant à la date spécifiée dans un sous-répertoire,
     * puis construit une instance de BufferedTrips pour représenter les voyages de cette date.
     *
     * @param date : la date pour laquelle obtenir les voyages
     * @return les voyages correspondants à la date donnée
     * @throws UncheckedIOException si une erreur d'E/S survient lors de la lecture du fichier
     */
    @Override
    public Trips tripsFor(LocalDate date) {
        try (FileChannel tripsChannel = FileChannel.open(directory()
                .resolve(date.toString() + "/trips.bin"));) {
            return new BufferedTrips(List.copyOf(stringTable())
                    , getMapped(tripsChannel));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Retourne les connexions pour une date donnée.
     * Cette méthode ouvre les fichiers "connections.bin" et "connections-succ.bin" correspondant à la date spécifiée
     * dans un sous-répertoire, puis construit une instance de BufferedConnections pour représenter les connexions de cette date.
     *
     * @param date : la date pour laquelle obtenir les connexions
     * @return les connexions correspondantes à la date donnée
     * @throws UncheckedIOException si une erreur d'E/S survient lors de la lecture des fichiers
     */
    @Override
    public Connections connectionsFor(LocalDate date) {
        try(FileChannel connectionsChannel = FileChannel.open(directory()
                .resolve(date.toString() + "/connections.bin"));
            FileChannel connectionsSuccChannel = FileChannel.open(directory()
                    .resolve(date.toString() + "/connections-succ.bin"));)
        {
            return new BufferedConnections(getMapped(connectionsChannel),
                    getMapped(connectionsSuccChannel));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
