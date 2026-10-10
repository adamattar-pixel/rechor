package ch.epfl.rechor;

import java.util.Objects;

public record Platform(Station parent, String code) {
    public Platform {
        Objects.requireNonNull(parent);
        code = code.intern();
    }
}
