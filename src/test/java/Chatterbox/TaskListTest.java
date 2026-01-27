package Chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_and_remove_and_mark() throws Exception {
        TaskList list = new TaskList();
        ToDo t = new ToDo("a");
        list.addTask(t);
        assertEquals(1, list.size());

        list.markTask(0, true);
        assertTrue(list.getTask(0).toString().contains("[X]"));

        Task removed = list.removeTask(0);
        assertEquals(t, removed);
        assertEquals(0, list.size());
    }

    @Test
    public void remove_invalid_throws() {
        TaskList list = new TaskList();
        Exception ex = assertThrows(ChatterboxException.class, () -> list.removeTask(0));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    public void findTasksOnDate_finds_deadline_and_event() {
        TaskList list = new TaskList();
        LocalDateTime ddate = LocalDateTime.of(2024, 1, 27, 12, 0);
        Deadline d = new Deadline("due", ddate);
        Event e = new Event("meet", ddate.minusHours(1), ddate.plusHours(2));
        list.addTask(d);
        list.addTask(e);

        ArrayList<Task> found = list.findTasksOnDate(ddate);
        assertEquals(2, found.size());
    }
}
