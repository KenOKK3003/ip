package chatterbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Parses user input and creates corresponding Command objects for the Chatterbox application.
 * Handles command recognition and argument extraction.
 */
class Parser {
    /** Formatter for parsing date-time input. */
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    /** Formatter for parsing date-only input. */
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Parses the full user input and returns the corresponding Command object.
     *
     * @param fullCommand The complete user input string.
     * @return The Command object representing the user's command.
     * @throws ChatterboxException If the input is invalid or unrecognised.
     */
    public Command parseCommand(String fullCommand) throws ChatterboxException {
        assert fullCommand != null : "Command input must not be null";
        if (fullCommand.trim().isEmpty()) {
            throw new ChatterboxException("Please enter a command.");
        }
        
        String[] parts = fullCommand.trim().split(" ", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";
        
        switch (commandWord) {
        case "bye":
            return new ExitCommand();
        case "list":
            return new ListCommand();
        case "mark":
            return parseMarkCommand(arguments, true);
        case "unmark":
            return parseMarkCommand(arguments, false);
        case "delete":
            return parseDeleteCommand(arguments);
        case "todo":
            return parseTodoCommand(arguments);
        case "deadline":
            return parseDeadlineCommand(arguments);
        case "event":
            return parseEventCommand(arguments);
        case "finddate":
            return parseFindDateCommand(arguments);
        case "find":
            return parseFindCommand(arguments);
        default:
            throw new ChatterboxException(
                "Hmm, I don't recognize that command! " +
                "Try 'todo', 'deadline', 'event', 'list', 'find', or 'finddate'!");
        }

    }

    private Command parseFindCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Find arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("Please specify a keyword to search for.");
        }
        return new FindCommand(arguments.trim());
    }
    
    private Command parseMarkCommand(String arguments, boolean isDone) throws ChatterboxException {
        assert arguments != null : "Mark arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("Please specify which task to " + 
                (isDone ? "mark" : "unmark") + ".");
        }
        
        try {
            int taskNum = Integer.parseInt(arguments.trim()) - 1;
            return new MarkCommand(taskNum, isDone);
        } catch (NumberFormatException e) {
            throw new ChatterboxException("Please provide a valid task number.");
        }
    }
    
    private Command parseDeleteCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Delete arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("Please specify which task to delete.");
        }
        
        try {
            int taskNum = Integer.parseInt(arguments.trim()) - 1;
            return new DeleteCommand(taskNum);
        } catch (NumberFormatException e) {
            throw new ChatterboxException("Please provide a valid task number.");
        }
    }
    
    private Command parseTodoCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Todo arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("The description of a todo cannot be empty.");
        }
        
        return new AddTodoCommand(arguments.trim());
    }
    
    private Command parseDeadlineCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Deadline arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("The description of a deadline cannot be empty.");
        }
        
        String[] parts = arguments.split("/by ", 2);
        if (parts.length < 2) {
            throw new ChatterboxException("Please specify the deadline with /by");
        }
        
        String description = parts[0].trim();
        String byStr = parts[1].trim();
        
        if (description.isEmpty()) {
            throw new ChatterboxException("The description of a deadline cannot be empty.");
        }
        
        if (byStr.isEmpty()) {
            throw new ChatterboxException("The deadline date/time cannot be empty.");
        }
        
        try {
            LocalDateTime by = parseFlexibleDateTime(byStr);
            return new AddDeadlineCommand(description, by);
        } catch (DateTimeParseException e) {
            throw new ChatterboxException(
                "Invalid date format. Please use yyyy-MM-dd HHmm (e.g., 2019-12-02 1800) " +
                "or yyyy-MM-dd (e.g., 2019-12-02)");
        }
    }
    
    private Command parseEventCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Event arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("The description of an event cannot be empty.");
        }
        
        String[] fromParts = arguments.split("/from ", 2);
        if (fromParts.length < 2) {
            throw new ChatterboxException("Please specify the event time with /from and /to");
        }
        
        String[] toParts = fromParts[1].split("/to ", 2);
        if (toParts.length < 2) {
            throw new ChatterboxException("Please specify the event time with /from and /to");
        }
        
        String description = fromParts[0].trim();
        String fromStr = toParts[0].trim();
        String toStr = toParts[1].trim();
        
        if (description.isEmpty()) {
            throw new ChatterboxException("The description of an event cannot be empty.");
        }
        
        if (fromStr.isEmpty() || toStr.isEmpty()) {
            throw new ChatterboxException("The event time cannot be empty.");
        }
        
        try {
            LocalDateTime from = parseFlexibleDateTime(fromStr);
            LocalDateTime to = parseFlexibleDateTime(toStr);
            
            if (to.isBefore(from)) {
                throw new ChatterboxException("The 'to' time must be after the 'from' time.");
            }
            
            return new AddEventCommand(description, from, to);
        } catch (DateTimeParseException e) {
            throw new ChatterboxException(
                "Invalid date format. Please use yyyy-MM-dd HHmm (e.g., 2019-12-02 1800) " +
                "or yyyy-MM-dd (e.g., 2019-12-02)");
        }
    }
    
    private Command parseFindDateCommand(String arguments) throws ChatterboxException {
        assert arguments != null : "Find date arguments must not be null";
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("Please specify a date (yyyy-MM-dd).");
        }
        
        try {
            LocalDateTime date = parseDate(arguments.trim());
            return new FindDateCommand(date);
        } catch (DateTimeParseException e) {
            throw new ChatterboxException("Invalid date format. Please use yyyy-MM-dd (e.g., 2019-12-02)");
        }
    }
    
    private LocalDateTime parseDate(String dateStr) throws DateTimeParseException {
        return LocalDateTime.parse(dateStr + " 0000", INPUT_DATE_FORMATTER);
    }
    
    private LocalDateTime parseFlexibleDateTime(String dateTimeStr) throws DateTimeParseException {
        // Try full date-time format first
        try {
            return LocalDateTime.parse(dateTimeStr, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException e1) {
            // Try date-only format
            return parseDate(dateTimeStr);
        }
    }
}
