package weeklyColection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.tuple.Pair;
import scheduleManager.Event;
import scheduleManager.Room;
import scheduleManager.Schedule;
import scheduleManager.ScheduleManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;

import java.util.*;


public class ScheduleManagerImp extends ScheduleManager {

    /**
     * Metoda koja kreira događaj na osnovu podataka iz fajla
     * @paramDate Datum događaja
     * @paramDateTo Datum do kog traje događaj
     * @paramRoom Sala u kojoj se događaj održava
     * @paramDayOfWeek Dan u nedelji kada se događaj održava
     * @paramStartTime Vreme početka događaja
     * @paramEndTime Vreme kraja događaja
     * @paramAdditionalData Dodatni podaci o događaju
     * @return Kreirani događaj
     */

    @Override
    protected boolean loadScheduleFromJSONFile() {
        schedule = initializeSchedule();
        Scanner scanner = new Scanner(System.in);
        Event event = null;
        String jsonFile = scanner.nextLine();
        System.out.println("Unesite datume od kad do kad vazi raspored i zatim izuzete dane");
        getDatesAndExceptedDays();
        Map<String, String> additionalData = new HashMap<>();

        try {
            //Učionica;Datum od;Datum do;Dan;Termin;Predmet;Tip;Nastavnik;Grupe
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFile));
            ArrayNode eventsArray = (ArrayNode) rootNode;
            List<String> fieldsNames = new ArrayList<>();

            for (Iterator<String> it = eventsArray.get(0).fieldNames(); it.hasNext(); ) {
                String x = it.next();
                fieldsNames.add(x);
            }

            for (JsonNode eventNode : eventsArray) {
                additionalData.clear();


                String ucionica = eventNode.get(fieldsNames.get(0)).asText();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date dateOd;
                Date dateTo;
                try {
                    dateOd = format.parse(eventNode.get(fieldsNames.get(1)).asText());
                    dateTo = format.parse(eventNode.get(fieldsNames.get(2)).asText());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String dan = eventNode.get(fieldsNames.get(3)).asText();
                String termin = eventNode.get(fieldsNames.get(4)).asText();

                for (int i = 4; i < fieldsNames.size(); i++) {
                    additionalData.put(fieldsNames.get(i), eventNode.get(fieldsNames.get(i)).asText());
                }
                event = createEventFromFile(dateOd, dateTo, ucionica, dan, termin, additionalData);
                schedule.getSchedule().add(event);

            }
            return true;
        } catch (IOException e) {
            System.out.println("fajl nije ucitan\n");
            return false;
        }
    }

    @Override
    protected boolean loadScheduleFromCSVFile() {
        schedule = initializeSchedule();
        Scanner scanner = new Scanner(System.in);
        String csvFile = scanner.nextLine();
        System.out.println("Unesite datume od kad do kad vazi raspored i zatim izuzete dane");
        getDatesAndExceptedDays();

        List<Event> events = new ArrayList<>();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).build()) {
            Map<String, String> additionalData = new HashMap<>();
            List<String[]> records = csvReader.readAll();
            String[] head = new String[0];
            boolean csv = false;
            Event event = null;

            for (String[] record : records) {
                additionalData.clear();
                String line = Arrays.toString(record);
                line = line.replace("[", "");
                line = line.replace("]", "");
                String[] parts = line.split(";");
                if (!csv) {
                    head = parts;
                    csv = true;
                    continue;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date dateOd;
                Date dateTo;
                String ucionica = parts[0];
                try {
                    dateOd = format.parse(parts[1]);
                    dateTo = format.parse(parts[2]);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                String dan = parts[3];

                StringBuilder result = new StringBuilder();

                for (char c : dan.toCharArray()) {
                    if (c < 128) {
                        result.append(c);
                    }
                }
                dan = result.toString();
                dan = dan.trim();

                String termin = parts[4];
                for (int i = 4; i < parts.length; i++) {

                    additionalData.put(head[i], parts[i]);
                }

                event = createEventFromFile(dateOd, dateTo, ucionica, dan, termin, additionalData);
                schedule.getSchedule().add(event);
            }
            return true;
        } catch (IOException | CsvException e) {
            System.out.println("fajl nije ucitan\n");
            return false;
        }
    }

    private List<Event> getNesto(List<Event> listaEventa) {
        List<Event> events = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<Event> eventsForDay = schedule.getEventsByDay(day, listaEventa);
            Map<String, List<Event>> eventsPerRoom = sortEventsByRoom(eventsForDay);

            for (String room : eventsPerRoom.keySet()) {
                List<Event> sortedList = new ArrayList<>(eventsPerRoom.get(room));
                sortedList.sort(Comparator.comparing(Event::getStartTime));

                for (Event event : sortedList) {
                    // Iteriraj kroz svako pojavljivanje događaja
                    Calendar currentEventDate = Calendar.getInstance();
                    currentEventDate.setTime(event.getDate());

                    Calendar eventEndDate = Calendar.getInstance();
                    eventEndDate.setTime(event.getDateTo());

                    while (currentEventDate.before(eventEndDate) && !currentEventDate.after(schedule.getStartDate()) && currentEventDate.before(schedule.getEndDate())) {
                        currentEventDate.add(Calendar.DAY_OF_MONTH, 7);
                    }
                    while (currentEventDate.after(schedule.getStartDate()) && currentEventDate.before(schedule.getEndDate()) && currentEventDate.before(eventEndDate)) {
                        if (!event.getAdditionalData().isEmpty())
                            events.add(new Event(currentEventDate.getTime(), event.getRoom(), event.getStartTime(), event.getEndTime(), event.getDayOfWeek(), event.getAdditionalData()));
                        else
                            events.add(new Event(currentEventDate.getTime(), event.getRoom(), event.getStartTime(), event.getEndTime(), event.getDayOfWeek()));
                        currentEventDate.add(Calendar.DAY_OF_MONTH, 7);
                    }
                }
            }
        }

        events.sort(
                Comparator.comparing(Event::getRoom, Comparator.comparing(Room::getName)) // Prvo po imenu sobe
                        .thenComparing(Event::getDate)  // Zatim po datumima
        );

        return events;
    }

    @Override
    public Map<Pair<String, String>, List<String>> findAvailableTime(List<Event> listaEventa) {
        Map<Pair<String, String>, List<String>> availableTimes = new HashMap<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        List<Event> sortedList = new ArrayList<>(getNesto(listaEventa));

        StringBuilder timeSlots = new StringBuilder();

        Time lastEndTime = Time.valueOf("00:00:00");
        if(sortedList.isEmpty())
            return availableTimes;
        Date lastDate = sortedList.get(0).getDate();
        Event lastEvent = null;

        for (Event event : sortedList) {
            if (!event.getDate().equals(lastDate)) {
                if (timeSlots.length() > 0) {
                    timeSlots.append(", ");
                }
                timeSlots.append(lastEndTime).append("-").append("23:59:59");
                lastEndTime = Time.valueOf("00:00:00");
                lastDate = event.getDate();

                String formattedDate = dateFormatter.format(lastEvent.getDate());
                Pair<String, String> roomDatePair = Pair.of(lastEvent.getRoom().getName(), formattedDate);

                availableTimes.computeIfAbsent(roomDatePair, k -> new ArrayList<>());
                availableTimes.get(roomDatePair).add(timeSlots.toString());

                timeSlots.setLength(0);
            }
            if (!event.getStartTime().equals(lastEndTime)) {
                if (timeSlots.length() > 0) {
                    timeSlots.append(", ");
                }
                timeSlots.append(lastEndTime).append("-").append(event.getStartTime());
                lastEndTime = event.getEndTime();
                lastDate = event.getDate();
            }
            lastEvent = event;

        }

        if (!lastEndTime.equals(Time.valueOf("23:59:59"))) {
            if (timeSlots.length() > 0) {
                timeSlots.append(", ");
            }
            timeSlots.append(lastEndTime).append("-").append("23:59:59");
        }

        String formattedDate = dateFormatter.format(lastEvent.getDate());
        Pair<String, String> roomDatePair = Pair.of(lastEvent.getRoom().getName(), formattedDate);

        availableTimes.computeIfAbsent(roomDatePair, k -> new ArrayList<>());
        availableTimes.get(roomDatePair).add(timeSlots.toString());


        return availableTimes;
    }

}

