package chatterbox;

/**
 * Represents an executable command in the Chatterbox application.
 * Serves as the base class for all user commands.
 */
abstract class Command {
    /**
     * Executes the command using the provided task list, UI, and storage.
     *
     * @param tasks The task list to operate on.
     * @param ui The user interface for displaying output.
     * @param storage The storage handler for persisting changes.
     * @throws ChatterboxException If an error occurs during execution.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChatterboxException;
    /**
     * Returns whether this command will cause the application to exit.
     *
     * @return True if the command signals exit, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
