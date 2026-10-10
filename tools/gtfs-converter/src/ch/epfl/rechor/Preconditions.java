package ch.epfl.rechor;

public final class Preconditions {
    private Preconditions() {}

    public static final boolean CHECK_PRECONDITIONS = true;

    public static void checkArgument(boolean mustBeTrue) {
        if (CHECK_PRECONDITIONS && !mustBeTrue)
            throw new IllegalArgumentException();
    }
}
