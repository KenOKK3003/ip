package chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void load_nonexistentFile_createsAndReturnsEmpty() throws Exception {
        Path dir = Files.createTempDirectory("chatterbox-storage-load");
        Path file = dir.resolve("tasks.txt");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> loaded = storage.load();

        assertTrue(Files.exists(file));
        assertEquals(0, loaded.size());

        Files.deleteIfExists(file);
        Files.deleteIfExists(dir);
    }

    @Test
    public void load_corruptedLines_skipsInvalidAndLoadsValid() throws Exception {
        Path tmp = Files.createTempFile("chatterbox-corrupt", ".txt");
        List<String> lines = List.of(
                "T | 1 | valid todo",
                "X | 0 | invalid type",
                "D | 0 | invalid date | not-a-date",
                "E | 1 | valid event | 2024-03-01 0900 | 2024-03-01 1100",
                "badly formatted line"
        );
        Files.write(tmp, lines);

        Storage storage = new Storage(tmp.toString());
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertTrue(loaded.get(0) instanceof ToDo);
        assertTrue(loaded.get(1) instanceof Event);

        Files.deleteIfExists(tmp);
    }

    @Test
    public void save_nestedDirectoryPath_createsDirectoriesAndFile() throws Exception {
        Path root = Files.createTempDirectory("chatterbox-storage-save");
        Path nestedFile = root.resolve(Paths.get("nested", "data", "tasks.txt"));

        Storage storage = new Storage(nestedFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo("t1"));

        storage.save(tasks);

        assertTrue(Files.exists(nestedFile));
        List<String> written = Files.readAllLines(nestedFile);
        assertEquals(1, written.size());
        assertTrue(written.get(0).startsWith("T | 0 | t1"));

        Files.deleteIfExists(nestedFile);
        Files.deleteIfExists(nestedFile.getParent());
        Files.deleteIfExists(nestedFile.getParent().getParent());
        Files.deleteIfExists(root);
    }
}
