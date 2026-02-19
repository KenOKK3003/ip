package chatterbox;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents a command to add an event task.
 */
class AddEventCommand extends Command {
    /** Description of the event. */
    private String description;
    /** Start date and time of the event. */
    private LocalDateTime from;
    /** End date and time of the event. */
    private LocalDateTime to;
    
    public AddEventCommand(String description, LocalDateTime from, LocalDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        ArrayList<Task> clashes = tasks.findClashingEvents(from, to);
        Task newTask = new Event(description, from, to);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        if (!clashes.isEmpty()) {
            ui.showScheduleClash(clashes);
        }
        storage.save(tasks.getAllTasks());
    }
}
