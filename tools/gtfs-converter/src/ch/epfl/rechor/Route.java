package ch.epfl.rechor;

import java.util.Objects;

public record Route(Kind kind, String longName) {
    public enum Kind {TRAM, METRO, TRAIN, BUS, FERRY, AERIAL_LIFT, FUNICULAR}

    public Route {
        Objects.requireNonNull(kind);
        longName = longName.intern();
    }
}
