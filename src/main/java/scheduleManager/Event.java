package scheduleManager;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter

public class Event {
    private Date date;
    private Room room;
    private Date dateTo;
    private Time startTime;
    private Time endTime;
    private DayOfWeek dayOfWeek;
    private Map<String, String > additionalData;


    public Event(Date date, Room room, Time startTime, Time endTime, DayOfWeek dayOfWeek, Map<String, String > additionalData) {
        this.additionalData = new HashMap<>();
        this.date = date;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.additionalData = additionalData;
    }

    public Event(Date date, Room room, Time startTime, Time endTime, DayOfWeek dayOfWeek) {
        this.additionalData = new HashMap<>();
        this.date = date;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
    }

    public Event(Date date, Date dateTo, Room room, Time startTime, Time endTime, DayOfWeek dayOfWeek, Map<String, String > additionalData) {
        this(date, room, startTime, endTime, dayOfWeek, additionalData);
        this.dateTo = dateTo;
    }

    public Event(Date date, Date dateTo, Room room, Time startTime, Time endTime, DayOfWeek dayOfWeek) {
        this(date, room, startTime, endTime, dayOfWeek);
        this.dateTo = dateTo;
    }
    public Event(){

    }

    @Override
    public String toString() {
        if(dateTo == null)
            return "Event{" +
                "datum=" + date +
                ", ucionica=" + room.getName() +
                ", vreme pocetka=" + startTime +
                ", vreme kraja=" + endTime +
                ", dan u nedelji=" + dayOfWeek +
                 additionalData +
                '}';
        else
            return "Event{" +
                "datum od=" + date +
                ", datum do=" + dateTo +
                ", ucionica=" + room.getName() +
                ", vreme pocetka=" + startTime +
                ", vreme kraja=" + endTime +
                ", dan u nedelji=" + dayOfWeek +
                 additionalData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(date, event.date) && Objects.equals(room, event.room) && Objects.equals(startTime, event.startTime) && Objects.equals(endTime, event.endTime) && dayOfWeek == event.dayOfWeek && Objects.equals(additionalData, event.additionalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, room, startTime, endTime, dayOfWeek, additionalData);
    }
}
