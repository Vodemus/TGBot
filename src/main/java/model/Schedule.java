package model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int schedule_id;
    private int teacher_id;
    private int weekday_id;
    private int weekparity_id;
    private int pararoom_id;
    private int squad_id;
    private int subsquad_id;
    private int discipline_id;
    private int paratime_id;
    private int paratype_id;
    private LocalDate start_date;
    private LocalDate end_date;

    public Schedule(int teacher_id, int weekday_id, int weekparity_id, int pararoom_id, int squad_id, int subsquad_id, int discipline_id, int paratime_id, int paratype_id, LocalDate start_date, LocalDate end_date) {
        this.teacher_id = teacher_id;
        this.weekday_id = weekday_id;
        this.weekparity_id = weekparity_id;
        this.pararoom_id = pararoom_id;
        this.squad_id = squad_id;
        this.subsquad_id = subsquad_id;
        this.discipline_id = discipline_id;
        this.paratime_id = paratime_id;
        this.paratype_id = paratype_id;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Schedule() {}

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public int getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(int weekday_id) {
        this.weekday_id = weekday_id;
    }

    public int getWeekparity_id() {
        return weekparity_id;
    }

    public void setWeekparity_id(int weekparity_id) {
        this.weekparity_id = weekparity_id;
    }

    public int getPararoom_id() {
        return pararoom_id;
    }

    public void setPararoom_id(int pararoom_id) {
        this.pararoom_id = pararoom_id;
    }

    public int getSquad_id() {
        return squad_id;
    }

    public void setSquad_id(int squad_id) {
        this.squad_id = squad_id;
    }

    public int getSubsquad_id() {
        return subsquad_id;
    }

    public void setSubsquad_id(int subsquad_id) {
        this.subsquad_id = subsquad_id;
    }

    public int getDiscipline_id() {
        return discipline_id;
    }

    public void setDiscipline_id(int discipline_id) {
        this.discipline_id = discipline_id;
    }

    public int getParatime_id() {
        return paratime_id;
    }

    public void setParatime_id(int paratime_id) {
        this.paratime_id = paratime_id;
    }

    public int getParatype_id() {
        return paratype_id;
    }

    public void setParatype_id(int paratype_id) {
        this.paratype_id = paratype_id;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
}
