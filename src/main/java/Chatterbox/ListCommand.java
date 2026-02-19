package chatterbox;

/**
 * Represents a command to list all tasks.
 */
class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException {
        if (!storage.fileExists()) {
            ui.showError("Hey, my memory file seems to not exist! Let me create one and you can re-enter your command!");
            storage.createFile();
            return;
        }
        ui.showTaskList(tasks.getAllTasks());
    }
}
