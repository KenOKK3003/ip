package chatterbox;

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
    public void getTask_invalidIndices_throw() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("a"));

        assertThrows(ChatterboxException.class, () -> list.getTask(-1));
        assertThrows(ChatterboxException.class, () -> list.getTask(1));
    }

    @Test
    public void markTask_unmarkAfterMark_changesStatusBack() throws Exception {
        TaskList list = new TaskList();
        list.addTask(new ToDo("a"));

        list.markTask(0, true);
        assertTrue(list.getTask(0).toString().contains("[X]"));

        list.markTask(0, false);
        assertTrue(list.getTask(0).toString().contains("[ ]"));
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

    @Test
    public void findTasksOnDate_spanningEvent_includesAllDatesInRange() {
        TaskList list = new TaskList();
        Event spanning = new Event(
                "overnight hackathon",
                LocalDateTime.of(2024, 1, 27, 23, 0),
                LocalDateTime.of(2024, 1, 29, 1, 0)
        );
        list.addTask(spanning);
        list.addTask(new ToDo("not date-based"));

        assertEquals(1, list.findTasksOnDate(LocalDateTime.of(2024, 1, 27, 0, 0)).size());
        assertEquals(1, list.findTasksOnDate(LocalDateTime.of(2024, 1, 28, 0, 0)).size());
        assertEquals(1, list.findTasksOnDate(LocalDateTime.of(2024, 1, 29, 0, 0)).size());
        assertEquals(0, list.findTasksOnDate(LocalDateTime.of(2024, 1, 30, 0, 0)).size());
    }

    @Test
    public void findTasksByKeyword_caseInsensitive_and_emptyKeyword() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("Read Book"));
        list.addTask(new ToDo("write notes"));

        ArrayList<Task> foundCaseInsensitive = list.findTasksByKeyword("rEaD");
        assertEquals(1, foundCaseInsensitive.size());
        assertTrue(foundCaseInsensitive.get(0).getDescription().contains("Read"));

        ArrayList<Task> foundEmpty = list.findTasksByKeyword("");
        assertEquals(2, foundEmpty.size());
    }
}
