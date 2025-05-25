package at.technikum;

import java.time.LocalDateTime;

public class EnergyMessage {
    public String type;
    public String association;
    public double kwh;
    public String datetime;

    @Override
    public String toString() {
        return "EnergyMessage{" +
                "type='" + type + '\'' +
                ", association='" + association + '\'' +
                ", kwh=" + kwh +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}