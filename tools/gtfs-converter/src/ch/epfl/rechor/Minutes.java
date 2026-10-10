package ch.epfl.rechor;

public final class Minutes {
    public static final short MAX_MINUTES = 2 * 24 * 60;

    public static boolean isValid(short minutes) {
        return 0 <= minutes && minutes < MAX_MINUTES;
    }
}
