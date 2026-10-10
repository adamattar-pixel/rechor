package ch.epfl.rechor;

public record Transfer(int depStationId, int arrStationId, int minutes) {
    public Transfer {
        Preconditions.checkArgument((depStationId & 0xFFFF) == depStationId);
        Preconditions.checkArgument((arrStationId & 0xFFFF) == arrStationId);
        Preconditions.checkArgument((minutes & 0xFF) == minutes);
    }
}
