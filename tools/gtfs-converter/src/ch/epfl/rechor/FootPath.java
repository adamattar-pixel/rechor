package ch.epfl.rechor;

public record FootPath(short depStopId,
                       short arrStopId,
                       short durationMinutes) {
    public FootPath {
        Preconditions.checkArgument(Minutes.isValid(durationMinutes));
    }
}
