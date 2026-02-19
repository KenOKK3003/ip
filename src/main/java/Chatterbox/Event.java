package chatterbox;

import java.time.LocalDateTime;

/**
 * Represents an event task with a start and end time.
 */
class Event extends Task {
    /** Start date and time of the event. */
    protected LocalDateTime from;
    /** End date and time of the event. */
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
