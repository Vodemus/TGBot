package model;

import javax.persistence.*;

@Entity
@Table(name = "weekday")
public class Weekday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Weekday(String name) {
        this.name = name;
    }

    public Weekday() {
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
