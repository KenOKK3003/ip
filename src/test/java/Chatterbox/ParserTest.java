package Chatterbox;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

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
    }

    @Test
    public void parse_invalid_throws() {
        Parser p = new Parser();
        assertThrows(ChatterboxException.class, () -> p.parseCommand(""));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("deadline"));
        assertThrows(ChatterboxException.class, () -> p.parseCommand("event something"));
    }
}
