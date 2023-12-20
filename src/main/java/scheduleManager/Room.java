package scheduleManager;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
@Getter
@Setter

public class Room {
    private String name;
    private int capacity;
    private Map<String, String> additionalData;

    public Room(String name, int capacity, Map<String, String> additionalData) {
        this.name = name;
        this.capacity = capacity;
        this.additionalData = new HashMap<>(additionalData);
    }

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.additionalData = new HashMap<>();
    }

    public Room() {
        this.additionalData = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", capacity=" + capacity +
                ", additionalData=" + additionalData +
                '}';
    }
}
