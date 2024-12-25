package model;

import javax.persistence.*;

@Entity
@Table(name = "paratype")
public class Paratype {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;

    public Paratype(String name) {
        this.type = type;
    }

    public Paratype() {
    }

    @Override
    public String toString() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String gettype() {
        return type;
    }

    public void settype(String type) {
        this.type = type;
    }
}
