package DatabaseHelper;

public class Sensor {

    public String value, time;


//
    public Sensor(String value, String Time) {
        this.value = value;
       this.time = Time;
    }
    public Sensor() {}

    public String getvalue() {
        return value;
    }

    public String getTime() {
        return time;
    }
}
