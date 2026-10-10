package ch.epfl.rechor;

import java.util.List;
import java.util.Objects;

public record Station(String name, List<String> aliases, int lon, int lat) {
    public Station {
        Objects.requireNonNull(name);
        Objects.requireNonNull(aliases);
    }
}
