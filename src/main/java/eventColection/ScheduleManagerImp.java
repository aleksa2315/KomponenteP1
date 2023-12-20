package eventColection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import scheduleManager.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

@Getter
public class ScheduleManagerImp extends ScheduleManager {
    @Override
    protected boolean loadScheduleFromJSONFile() {
        schedule = new Schedule();
        Scanner scanner = new Scanner(System.in);
        Event event = null;

        String jsonFile = scanner.nextLine();
        Map<String, String> additionalData = new HashMap<>();

        try {
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
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String ucionica = eventNode.get(fieldsNames.get(0)).asText();
                Date date = format.parse(eventNode.get(fieldsNames.get(1)).asText());

                String dan = eventNode.get(fieldsNames.get(2)).asText();
                String termin = eventNode.get(fieldsNames.get(3)).asText();

                for (int i = 4; i < fieldsNames.size(); i++) {
                    additionalData.put(fieldsNames.get(i), eventNode.get(fieldsNames.get(i)).asText());
                }
                event = createEventFromFile(date, null, ucionica, dan, termin, additionalData);
                schedule.getSchedule().add(event);

            }
            return true;
        } catch (IOException e) {
            System.out.println("fajl nije ucitan\n");
            return false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected boolean loadScheduleFromCSVFile(){
        schedule = new Schedule();
        Scanner scanner = new Scanner(System.in);
        String csvFile = scanner.nextLine();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFile)).build()) {
            Map<String, String> additionalData = new HashMap<>();
            List<String[]> records = csvReader.readAll();
            String [] head = new String[0];
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
                Date date;
                String ucionica = parts[0];
                try {
                    date = format.parse(parts[1]);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                String dan = parts[2];

                StringBuilder result = new StringBuilder();

                for (char c : dan.toCharArray()) {
                    if (c < 128) {
                        result.append(c);
                    }
                }
                dan = result.toString();
                dan = dan.trim();

                String termin = parts[3];
                for (int i = 4; i < parts.length; i++) {

                    additionalData.put(head[i], parts[i]);
                }

                event = createEventFromFile(date, null, ucionica, dan, termin, additionalData);
                schedule.getSchedule().add(event);
            }
            return true;
        } catch (IOException | CsvException e) {
            System.out.println("fajl nije ucitan\n");
            return false;
        }

    }

    @Override
    public Map<Pair<String, String>, List<String>> findAvailableTime(List<Event> listaEventa) { //IMPLE1
        Map<Pair<String, String>, List<String>> availableTimes = new HashMap<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        for (DayOfWeek day : DayOfWeek.values()) {
            List<Event> eventsForDay = schedule.getEventsByDay(day, listaEventa);
            Map<String, List<Event>> eventsPerRoom = sortEventsByRoom(eventsForDay);

            for (String room : eventsPerRoom.keySet()) {
                StringBuilder timeSlots = new StringBuilder();
                List<Event> sortedList = new ArrayList<>(eventsPerRoom.get(room));
                sortedList.sort(
                        Comparator.comparing(Event::getDate)
                                .thenComparing(Event::getStartTime)
                );

                if(sortedList.isEmpty())
                    return availableTimes;

                Time lastEndTime = Time.valueOf("00:00:00");
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
                        Pair<String, String> roomDatePair = Pair.of(room, formattedDate);

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
                Pair<String, String> roomDatePair = Pair.of(room, formattedDate);

                availableTimes.computeIfAbsent(roomDatePair, k -> new ArrayList<>());
                availableTimes.get(roomDatePair).add(timeSlots.toString());

            }
        }
        return availableTimes;
    }
}
