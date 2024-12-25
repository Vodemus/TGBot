package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Time;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "paratime")
public class Paratime {
    @Id
    private int id;
    private int number;
    private Time starttime;
    private Time endtime;
    public int getId() {
        return id;
    }

    public void setId(int commissionid) {
        this.id = commissionid;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStringStarttime() {
        String timeString = starttime.toString();
        String formattedTime = timeString.substring(0, 5);
        return formattedTime;
    }
    public Time getStarttime() {
        return starttime;
    }

    public void setStarttime(Time starttime) {
        this.starttime = starttime;
    }

    public Time getEndtime() {
        return endtime;
    }
    public String getStringEndtime() {
        String timeString = endtime.toString();
        String formattedTime = timeString.substring(0, 5);
        return formattedTime;
    }

    public void setEndtime(Time endtime) {
        this.endtime = endtime;
    }
}
