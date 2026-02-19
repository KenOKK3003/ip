package chatterbox;

/**
 * Represents a command to mark or unmark a task as done.
 */
class MarkCommand extends Command {
    /** Index of the task to mark. */
    private int taskIndex;
    /** Whether to mark as done or not done. */
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
