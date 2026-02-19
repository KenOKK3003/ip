package chatterbox;

import java.time.LocalDateTime;

/**
 * Represents a command to add a deadline task.
 */
class AddDeadlineCommand extends Command {
    /** Description of the deadline task. */
    private String description;
    /** Deadline date and time. */
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
