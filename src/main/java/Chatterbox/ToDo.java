package chatterbox;

/**
 * Represents a todo task without any date/time attached.
 */
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
