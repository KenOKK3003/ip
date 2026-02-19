package chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void parse_todo_and_deadline_and_event_and_mark() throws Exception {
        Parser p = new Parser();

        Command c1 = p.parseCommand("todo read book");
        assertEquals("AddTodoCommand", c1.getClass().getSimpleName());

        Command c2 = p.parseCommand("deadline return book /by 2023-06-06 1800");
        assertEquals("AddDeadlineCommand", c2.getClass().getSimpleName());

        Command c3 = p.parseCommand("event project /from 2023-08-06 1400 /to 2023-08-06 1600");
        assertEquals("AddEventCommand", c3.getClass().getSimpleName());

        Command c4 = p.parseCommand("mark 1");
        assertEquals("MarkCommand", c4.getClass().getSimpleName());

        Command c5 = p.parseCommand("find project");
        assertEquals("FindCommand", c5.getClass().getSimpleName());

        Command c6 = p.parseCommand("finddate 2024-01-27");
        assertEquals("FindDateCommand", c6.getClass().getSimpleName());
    }

    @Test
    public void parse_invalid_throws() {
        Parser p = new Parser();
        assertThrows(ChatterboxException.class, () -> p.parseCommand(""));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("deadline"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("event something"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("unknowncmd test"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("find"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("finddate"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("finddate 27-01-2024"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("mark"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("mark abc"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("delete"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("delete abc"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("todo   "));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("deadline read /by"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("deadline /by 2024-01-27"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("deadline read"));
        assertThrows(ChatterboxException.class,
                () -> p.parseCommand("event proj /from 2024-01-28 1300 /to 2024-01-28 1200"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("event /from 2024-01-28 /to 2024-01-29"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("event proj /from /to 2024-01-29"));
    }

    @Test
    public void parse_accepts_case_insensitive_and_flexible_date_format() throws Exception {
        Parser p = new Parser();

        Command c1 = p.parseCommand("ToDo read book");
        assertEquals("AddTodoCommand", c1.getClass().getSimpleName());

        Command c2 = p.parseCommand("deadline return book /by 2024-02-20");
        assertEquals("AddDeadlineCommand", c2.getClass().getSimpleName());

        Command c3 = p.parseCommand("event retreat /from 2024-02-20 /to 2024-02-21");
        assertEquals("AddEventCommand", c3.getClass().getSimpleName());
    }
}
