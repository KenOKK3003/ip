package chatterbox;

import java.time.LocalDateTime;

/**
 * Represents a task with a deadline.
 */
class Deadline extends Task {
    /** Deadline date and time. */
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
