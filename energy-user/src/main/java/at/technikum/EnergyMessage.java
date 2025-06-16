package at.technikum;

/**
 * Data Transfer Object (DTO) for energy messages exchanged via RabbitMQ.
 * <p>
 * Contains raw energy data produced or consumed by either a user or producer
 * at a given timestamp. Used for serialization/deserialization of JSON payloads.
 */
public class EnergyMessage {
    public String type;
    public String association;
    public double kwh;
    public String datetime;

    public EnergyMessage(String type, String association, double kwh, String datetime) {
        this.type = type;
        this.association = association;
        this.kwh = kwh;
        this.datetime = datetime;
    }
}
