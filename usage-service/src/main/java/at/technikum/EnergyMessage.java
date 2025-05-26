package at.technikum;
public class EnergyMessage {
    public String type;
    public String association;
    public double kwh;
    public String datetime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

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