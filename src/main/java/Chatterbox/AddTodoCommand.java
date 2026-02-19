package chatterbox;

/**
 * Represents a command to add a todo task.
 */
class AddTodoCommand extends Command {
    /** Description of the todo task. */
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
