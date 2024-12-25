package model;

import javax.persistence.*;

@Entity
@Table(name = "pararoom")
public class Pararoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Pararoom(String name) {
        this.name = name;
    }

    public Pararoom() {
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
