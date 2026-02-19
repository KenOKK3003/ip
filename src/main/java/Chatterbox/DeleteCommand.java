package chatterbox;

/**
 * Represents a command to delete a task.
 */
class DeleteCommand extends Command {
    /** Index of the task to delete. */
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
