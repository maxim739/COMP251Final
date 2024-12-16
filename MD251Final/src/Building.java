public record Building(BuildingID id, int occupants) implements Comparable<Building> {
    @Override
    public int compareTo(Building o) {
        return id.compareTo(o.id);
    }
    public int retId() { return this.id.getIdNum();}
}