package scheduleManager;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter

public class Schedule {
    private List<Event> schedule;
    private Calendar startDate;
    private Calendar endDate;
    private List<Date> exceptions;


    public Schedule() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        schedule = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }
    

    public List<Event> sortByDate(){
        List<Event> events = new ArrayList<>(schedule);
        if(events.get(0).getDateTo() != null)
            events.sort(Comparator.comparing(Event::getDate).thenComparing(Event::getDateTo));
        else
            events.sort(Comparator.comparing(Event::getDate));
        return events;
    }

    public List<Event> sortByDayOfWeekDay(){
        //Sortiranje prvo do dana u nedelji, pa po datumu ako datum nije null
        List<Event> events = new ArrayList<>(schedule);
        if(events.get(0).getDate() == null)
            events.sort(Comparator.comparing(Event::getDayOfWeek));
        else
            events.sort(Comparator.comparing(Event::getDayOfWeek).thenComparing(Event::getDate));
        return events;
    }

    public List<Event> scheduleFromDateToDate(Date pocetak, Date kraj) {
        List<Event> events = new ArrayList<>();
        if(pocetak.after(kraj))
            throw new IllegalArgumentException("Pocetak ne moze biti posle kraja");
        if(schedule.get(0).getDate() != null)
            for (Event e : schedule) {
                if (e.getDate().after(pocetak) && e.getDate().before(kraj))
                    events.add(e);
            }
        return events;
    }

    public List<Event> getEventsByDay(DayOfWeek day, List<Event> listaEvent){
        List<Event> events = new ArrayList<>();
        for (Event event : listaEvent){
            if (event.getDayOfWeek() == day){
                events.add(event);
            }
        }
        events.sort(Comparator.comparing(Event::getDate));
        return events;
    }
}
