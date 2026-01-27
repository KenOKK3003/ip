package Chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class StorageTest {

    @Test
    public void load_and_save_roundtrip() throws Exception {
        Path tmp = Files.createTempFile("chatterbox-test", ".txt");
        String path = tmp.toString();

        // Prepare storage with some tasks and save
        Storage storage = new Storage(path);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("join sports club", false));
        LocalDateTime dt = LocalDateTime.of(2025, 12, 25, 10, 30);
        tasks.add(new Deadline("present", dt, true));
        storage.save(tasks);

        // Load back
        Storage storage2 = new Storage(path);
        ArrayList<Task> loaded = storage2.load();
        assertEquals(2, loaded.size());
        assertTrue(loaded.get(0) instanceof ToDo);
        assertTrue(loaded.get(1) instanceof Deadline);

        // Cleanup
        Files.deleteIfExists(tmp);
    }
}
