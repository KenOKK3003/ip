package chatterbox;

import java.util.ArrayList;

/**
 * Represents a command to find tasks by keyword.
 */
class FindCommand extends Command {
    /** Keyword to search for. */
    private String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> foundTasks = tasks.findTasksByKeyword(keyword);
        ui.showMatchingTasks(foundTasks, tasks, keyword);
    }
}
