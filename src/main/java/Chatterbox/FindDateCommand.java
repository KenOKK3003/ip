package chatterbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Represents a command to find tasks on a specific date.
 */
class FindDateCommand extends Command {
    /** Date to search for tasks. */
    private LocalDateTime date;
    /** Formatter for displaying dates. */
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public FindDateCommand(LocalDateTime date) {
        this.date = date;
    }
    
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> foundTasks = tasks.findTasksOnDate(date);
        ui.showTasksOnDate(foundTasks, tasks, date.format(DATE_ONLY_FORMATTER));
    }
}
