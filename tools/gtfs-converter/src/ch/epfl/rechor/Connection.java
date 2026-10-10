package ch.epfl.rechor;

public record Connection(int depStopId,
                         short depMinutes,
                         int arrStopId,
                         short arrMinutes,
                         int tripId,
                         byte tripPos) {
    public Connection {
        Preconditions.checkArgument((depStopId & 0xFFFF) == depStopId);
        Preconditions.checkArgument((arrStopId & 0xFFFF) == arrStopId);
        Preconditions.checkArgument(Minutes.isValid(depMinutes));
        Preconditions.checkArgument(Minutes.isValid(arrMinutes));
        // Ideally, we would like to have `arrMinutes > depMinutes`, but in the actual data,
        // some connections are shown as instantaneous (same arrival as departure time).
        Preconditions.checkArgument(arrMinutes >= depMinutes);
        Preconditions.checkArgument((tripId & 0xFF_FF_FF) == tripId);
        Preconditions.checkArgument((tripPos & 0xFF) == tripPos);
    }
}
