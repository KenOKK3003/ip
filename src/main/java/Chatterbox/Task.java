package chatterbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a generic task in the Chatterbox application.
 * Serves as the base class for specific task types such as ToDo, Deadline, and Event.
 */
abstract class Task {
    /**
     * Represents the type of a task.
     */
    enum TaskType {
        /** Represents a todo task. */
        TODO("T"),
        /** Represents a deadline task. */
        DEADLINE("D"),
        /** Represents an event task. */
        EVENT("E");

        /** Icon representing the task type. */
        private final String icon;

        TaskType(String icon) {
            this.icon = icon;
        }

        /**
         * Returns the icon for this task type.
         *
         * @return Single-character icon.
         */
        public String getIcon() {
            return icon;
        }
        
        /**
         * Converts a string representation to a TaskType.
         *
         * @param type String representation ("T", "D", or "E").
         * @return Corresponding TaskType.
         * @throws IllegalArgumentException If the type string is not recognized.
         */
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
    
    /** Description of the task. */
    protected String description;
    /** Completion status of the task. */
    protected boolean isDone;
    /** Type of the task. */
    protected TaskType type;
    /** Formatter for displaying dates to users. */
    protected static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    /** Formatter for storing dates in files. */
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

    /**
     * Returns a string representation of the task for display purposes.
     *
     * @return Display string for the task.
     */
    @Override
    public String toString() {
        return getTypeIcon() + getStatusIcon() + " " + description;
    }
}
