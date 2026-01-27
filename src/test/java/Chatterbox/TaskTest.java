package Chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void todo_toFileFormat_and_toString() {
        ToDo t = new ToDo("read book", false);
        assertTrue(t.toFileFormat().startsWith("T | 0 | read book"));
        assertTrue(t.toString().contains("read book"));
    }

    @Test
    public void deadline_toFileFormat_and_toString() {
        LocalDateTime by = LocalDateTime.of(2023, 6, 6, 18, 0);
        Deadline d = new Deadline("return book", by, true);
        String file = d.toFileFormat();
        assertTrue(file.startsWith("D | 1 | return book | "));
        assertTrue(d.toString().contains("return book"));
        assertEquals(by, d.getBy());
    }

    @Test
    public void event_toFileFormat_and_toString() {
        LocalDateTime from = LocalDateTime.of(2023, 8, 6, 14, 0);
        LocalDateTime to = LocalDateTime.of(2023, 8, 6, 16, 0);
        Event e = new Event("project meeting", from, to, false);
        String file = e.toFileFormat();
        assertTrue(file.startsWith("E | 0 | project meeting | "));
        assertTrue(e.toString().contains("project meeting"));
        assertEquals(from, e.getFrom());
        assertEquals(to, e.getTo());
    }
}
