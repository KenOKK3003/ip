package Chatterbox;
// chatterbox_single_file.java
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

class ChatterboxException extends Exception {
    public ChatterboxException(String message) {
        super(message);
    }
}

// ==================== Task Classes ====================
/**
 * Represents a generic task in the Chatterbox application.
 * Serves as the base class for specific task types such as ToDo, Deadline, and Event.
 */
abstract class Task {
    enum TaskType {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String icon;

        TaskType(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
        
        public static TaskType fromString(String type) {
            switch (type) {
            case "T":
                return TODO;
            case "D":
                return DEADLINE;
            case "E":
                return EVENT;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
            }
        }
    }
    
    protected String description;
    protected boolean isDone;
    protected TaskType type;
    protected static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    protected static final DateTimeFormatter FILE_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Constructs a new Task with the specified description and type.
     * The task is initially marked as not done.
     *
     * @param description Description of the task.
     * @param type Type of the task (TODO, DEADLINE, EVENT).
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Constructs a new Task with the specified description, type, and completion status.
     *
     * @param description Description of the task.
     * @param type Type of the task (TODO, DEADLINE, EVENT).
     * @param isDone Completion status of the task.
     */
    public Task(String description, TaskType type, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
        this.type = type;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the status icon representing whether the task is done.
     *
     * @return Status icon string ("[X]" if done, "[ ]" if not done).
     */
    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the icon representing the type of the task.
     *
     * @return Type icon string (e.g., "[T]", "[D]", "[E]").
     */
    public String getTypeIcon() {
        return "[" + type.getIcon() + "]";
    }
    
    /**
     * Returns a string representation of the task suitable for file storage.
     *
     * @return File format string for the task.
     */
    public abstract String toFileFormat();
    
    /**
     * Returns the date and time associated with the task, if any.
     * For base tasks, returns null.
     *
     * @return Date and time of the task, or null if not applicable.
     */
    public LocalDateTime getDateTime() {
        return null; // Base task has no date/time
    }

    @Override
    /**
     * Returns a string representation of the task for display purposes.
     *
     * @return Display string for the task.
     */
    public String toString() {
        return getTypeIcon() + getStatusIcon() + " " + description;
    }
}

class ToDo extends Task {
    public ToDo(String description) {
        super(description, TaskType.TODO);
    }
    
    public ToDo(String description, boolean isDone) {
        super(description, TaskType.TODO, isDone);
    }
    
    @Override
    public String toFileFormat() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }
}

class Deadline extends Task {
    protected LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }
    
    public Deadline(String description, LocalDateTime by, boolean isDone) {
        super(description, TaskType.DEADLINE, isDone);
        this.by = by;
    }
    
    public LocalDateTime getBy() {
        return by;
    }
    
    @Override
    public LocalDateTime getDateTime() {
        return by;
    }

    @Override
    public String toString() {
        return getTypeIcon() + getStatusIcon() + " " + description + 
               " (by: " + by.format(DISPLAY_DATE_FORMATTER) + ")";
    }
    
    @Override
    public String toFileFormat() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description + 
               " | " + by.format(FILE_DATE_FORMATTER);
    }
}

class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }
    
    public Event(String description, LocalDateTime from, LocalDateTime to, boolean isDone) {
        super(description, TaskType.EVENT, isDone);
        this.from = from;
        this.to = to;
    }
    
    public LocalDateTime getFrom() {
        return from;
    }
    
    public LocalDateTime getTo() {
        return to;
    }
    
    @Override
    public LocalDateTime getDateTime() {
        return from; // Return start time for event
    }

    @Override
    public String toString() {
        return getTypeIcon() + getStatusIcon() + " " + description + 
               " (from: " + from.format(DISPLAY_DATE_FORMATTER) + 
               " to: " + to.format(DISPLAY_DATE_FORMATTER) + ")";
    }
    
    @Override
    public String toFileFormat() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description + 
               " | " + from.format(FILE_DATE_FORMATTER) + 
               " | " + to.format(FILE_DATE_FORMATTER);
    }
}

// ==================== TaskList ====================
/**
 * Represents a list of tasks in the Chatterbox application.
 * Provides methods to add, remove, retrieve, and search for tasks.
 */
class TaskList {
    private ArrayList<Task> tasks;
    
    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }
    
    /**
     * Constructs a TaskList with the specified list of tasks.
     *
     * @param tasks List of tasks to initialise the TaskList with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    
    /**
     * Adds a task to the task list.
     *
     * @param task Task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }
    
    /**
     * Removes the task at the specified index from the task list.
     *
     * @param index Index of the task to remove (0-based).
     * @return The removed task.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public Task removeTask(int index) throws ChatterboxException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChatterboxException("Task number " + (index + 1) + " does not exist.");
        }
        return tasks.remove(index);
    }
    
    /**
     * Returns the task at the specified index.
     *
     * @param index Index of the task to retrieve (0-based).
     * @return The task at the specified index.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public Task getTask(int index) throws ChatterboxException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChatterboxException("Task number " + (index + 1) + " does not exist.");
        }
        return tasks.get(index);
    }
    
    /**
     * Marks the task at the specified index as done or not done.
     *
     * @param index Index of the task to mark (0-based).
     * @param isDone True to mark as done, false to mark as not done.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public void markTask(int index, boolean isDone) throws ChatterboxException {
        Task task = getTask(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
    
    /**
     * Returns the list of all tasks in the task list.
     *
     * @return List of all tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }
    
    /**
     * Returns the number of tasks in the task list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }
    
    /**
     * Returns a list of tasks that occur on the specified date.
     *
     * @param date Date to search for tasks.
     * @return List of tasks occurring on the given date.
     */
    public ArrayList<Task> findTasksOnDate(LocalDateTime date) {
        ArrayList<Task> result = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.getBy().toLocalDate().equals(date.toLocalDate())) {
                    result.add(task);
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                // Check if the event occurs on the given date
                if (!event.getFrom().toLocalDate().isAfter(date.toLocalDate()) && 
                    !event.getTo().toLocalDate().isBefore(date.toLocalDate())) {
                    result.add(task);
                }
            }
        }
        
        return result;
    }
}

class Ui {
    private static final String LINE = "________________________________";
    private Scanner scanner;
    
    public Ui() {
        scanner = new Scanner(System.in);
    }
    
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Chatterbox");
        System.out.println(" What can I do for you?");
        showLine();
    }
    
    public void showGoodbye() {
        System.out.println(" Bye! Hope to see you again soon!");
    }
    
    public void showLine() {
        System.out.println(LINE);
    }
    
    public String readCommand() {
        return scanner.nextLine();
    }
    
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }
    
    public void showLoadingError(String message) {
        System.err.println("Error loading tasks: " + message);
    }
    
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + totalTasks + " tasks in the list.");
    }
    
    public void showTaskRemoved(Task task, int totalTasks) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + totalTasks + " tasks in the list.");
    }
    
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            System.out.println(" Nice! Congrats on finishing this task!");
        } else {
            System.out.println(" OK, I've forgotten about it already!");
        }
        System.out.println("   " + task);
    }
    
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }
    
    public void showTasksOnDate(ArrayList<Task> tasks, String date) {
        System.out.println(" Tasks on " + date + ":");
        if (tasks.isEmpty()) {
            System.out.println(" No tasks found for this date.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
    }
    
    public void close() {
        scanner.close();
    }
}

// ==================== Storage ====================
/**
 * Handles loading and saving of tasks to and from a file for the Chatterbox application.
 * Provides methods to persist and retrieve the task list.
 */
class Storage {
    private String filePath;
    private static final DateTimeFormatter FILE_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    
    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Loads tasks from the data file.
     * Creates the file and its parent directory if they do not exist.
     *
     * @return List of tasks loaded from the file.
     * @throws ChatterboxException If an error occurs while loading tasks.
     */
    public ArrayList<Task> load() throws ChatterboxException {
        ArrayList<Task> tasks = new ArrayList<>();
        
        try {
            Path dataFilePath = Paths.get(filePath);
            Path dataDirPath = dataFilePath.getParent();
            
            // Create data directory if it doesn't exist
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            // Create data file if it doesn't exist
            if (!Files.exists(dataFilePath)) {
                Files.createFile(dataFilePath);
                return tasks; // Empty list for new file
            }
            
            // Read all lines from file
            java.util.List<String> lines = Files.readAllLines(dataFilePath);
            
            for (String line : lines) {
                try {
                    Task task = parseTaskFromFile(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping corrupted line: " + line);
                }
            }
            
        } catch (IOException e) {
            throw new ChatterboxException("Error loading tasks: " + e.getMessage());
        }
        
        return tasks;
    }
    
    private Task parseTaskFromFile(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(" \\| ");
        
        // Minimum 3 parts: type | status | description
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid format: " + line);
        }
        
        String typeStr = parts[0].trim();
        String statusStr = parts[1].trim();
        String description = parts[2].trim();
        
        boolean isDone = statusStr.equals("1");
        
        try {
            Task.TaskType type = Task.TaskType.fromString(typeStr);
            
            switch (type) {
            case TODO:
                return new ToDo(description, isDone);
                
            case DEADLINE:
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Deadline missing 'by' field: " + line);
                }
                String byStr = parts[3].trim();
                LocalDateTime by = LocalDateTime.parse(byStr, FILE_DATE_FORMATTER);
                return new Deadline(description, by, isDone);
                
            case EVENT:
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Event missing 'from' or 'to' field: " + line);
                }
                String fromStr = parts[3].trim();
                String toStr = parts[4].trim();
                LocalDateTime from = LocalDateTime.parse(fromStr, FILE_DATE_FORMATTER);
                LocalDateTime to = LocalDateTime.parse(toStr, FILE_DATE_FORMATTER);
                return new Event(description, from, to, isDone);
                
            default:
                throw new IllegalArgumentException("Unknown task type: " + typeStr);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in: " + line, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse task: " + line, e);
        }
    }
    
    /**
     * Saves the given list of tasks to the data file.
     * Creates the file and its parent directory if they do not exist.
     *
     * @param tasks List of tasks to save.
     * @throws ChatterboxException If an error occurs while saving tasks.
     */
    public void save(ArrayList<Task> tasks) throws ChatterboxException {
        try {
            Path dataFilePath = Paths.get(filePath);
            Path dataDirPath = dataFilePath.getParent();
            
            // Ensure data directory exists
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            FileWriter writer = new FileWriter(filePath);
            
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
            
            writer.close();
            
        } catch (IOException e) {
            throw new ChatterboxException("Error saving tasks: " + e.getMessage());
        }
    }
}

// ==================== Command Classes ====================
/**
 * Represents an executable command in the Chatterbox application.
 * Serves as the base class for all user commands.
 */
abstract class Command {
    /**
     * Executes the command using the provided task list, UI, and storage.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface for displaying output.
     * @param storage The storage handler for persisting changes.
     * @throws ChatterboxException If an error occurs during execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException;
    /**
     * Returns whether this command will cause the application to exit.
     *
     * @return True if the command signals exit, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}

class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }
    
    @Override
    public boolean isExit() {
        return true;
    }
}

class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getAllTasks());
    }
}

class MarkCommand extends Command {
    private int taskIndex;
    private boolean isDone;
    
    public MarkCommand(int taskIndex, boolean isDone) {
        this.taskIndex = taskIndex;
        this.isDone = isDone;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        tasks.markTask(taskIndex, isDone);
        Task task = tasks.getTask(taskIndex);
        ui.showTaskMarked(task, isDone);
        storage.save(tasks.getAllTasks());
    }
}

class DeleteCommand extends Command {
    private int taskIndex;
    
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        Task removedTask = tasks.removeTask(taskIndex);
        ui.showTaskRemoved(removedTask, tasks.size());
        storage.save(tasks.getAllTasks());
    }
}

class AddTodoCommand extends Command {
    private String description;
    
    public AddTodoCommand(String description) {
        this.description = description;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        Task newTask = new ToDo(description);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks.getAllTasks());
    }
}

class AddDeadlineCommand extends Command {
    private String description;
    private LocalDateTime by;
    
    public AddDeadlineCommand(String description, LocalDateTime by) {
        this.description = description;
        this.by = by;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        Task newTask = new Deadline(description, by);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks.getAllTasks());
    }
}

class AddEventCommand extends Command {
    private String description;
    private LocalDateTime from;
    private LocalDateTime to;
    
    public AddEventCommand(String description, LocalDateTime from, LocalDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        Task newTask = new Event(description, from, to);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.save(tasks.getAllTasks());
    }
}

class FindDateCommand extends Command {
    private LocalDateTime date;
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public FindDateCommand(LocalDateTime date) {
        this.date = date;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> foundTasks = tasks.findTasksOnDate(date);
        ui.showTasksOnDate(foundTasks, date.format(DATE_ONLY_FORMATTER));
    }
}

// ==================== Parser ====================
/**
 * Parses user input and creates corresponding Command objects for the Chatterbox application.
 * Handles command recognition and argument extraction.
 */
class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
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
            
        default:
            throw new ChatterboxException(
                "Hmm, I don't recognize that command! " +
                "Try 'todo', 'deadline', 'event', 'list', or 'finddate'!");
        }
    }
    
    private Command parseMarkCommand(String arguments, boolean isDone) throws ChatterboxException {
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
        if (arguments.trim().isEmpty()) {
            throw new ChatterboxException("The description of a todo cannot be empty.");
        }
        
        return new AddTodoCommand(arguments.trim());
    }
    
    private Command parseDeadlineCommand(String arguments) throws ChatterboxException {
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

// ==================== Main Chatterbox Class ====================
/**
 * Represents the main entry point for the Chatterbox application.
 * Handles initialisation, command processing loop, and program execution.
 */
public class Chatterbox {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private Parser parser;

    /**
     * Constructs a Chatterbox instance, loading tasks from the specified file path.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Chatterbox(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        
        try {
            tasks = new TaskList(storage.load());
        } catch (ChatterboxException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main command loop for the Chatterbox application.
     * Handles user input, command execution, and program termination.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = parser.parseCommand(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ChatterboxException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        
        ui.close();
    }

    /**
     * The main method to launch the Chatterbox application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Chatterbox("./data/chatterbox.txt").run();
    }
}