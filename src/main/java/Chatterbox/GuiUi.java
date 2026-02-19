package chatterbox;

import java.util.ArrayList;

/**
 * Handles user interface interactions for the GUI version of Chatterbox.
 */
class GuiUi extends Ui {
    /** System-specific line separator. */
    private static final String NEW_LINE = System.lineSeparator();
    /** Buffer for accumulating output. */
    private final StringBuilder output = new StringBuilder();

    private void appendLine(String line) {
        output.append(line).append(NEW_LINE);
    }

    /**
     * Clears the output buffer.
     */
    public void clear() {
        output.setLength(0);
    }

    /**
     * Returns the accumulated output and clears the buffer.
     *
     * @return Accumulated output string.
     */
    public String consumeOutput() {
        String result = output.toString().stripTrailing();
        clear();
        return result;
    }

    @Override
    public void showWelcome() {
        appendLine(" Hello! I'm Chatterbox");
        appendLine(" What can I do for you?");
    }

    @Override
    public void showGoodbye() {
        appendLine(" Bye! Hope to see you again soon!");
    }

    @Override
    public void showLine() {
        // No-op for GUI output
    }

    @Override
    public void showError(String message) {
        appendLine(" OOPS!!! " + message);
    }

    @Override
    public void showLoadingError(String message) {
        appendLine("Error loading tasks: " + message);
    }

    @Override
    public void showTaskAdded(Task task, int totalTasks) {
        appendLine(" Got it. I've added this task:");
        appendLine("   " + task);
        appendLine(" Now you have " + totalTasks + " tasks in the list.");
    }
    
    @Override
    public void showScheduleClash(ArrayList<Task> clashingTasks) {
        appendLine(" Warning: This event clashes with:");
        for (Task task : clashingTasks) {
            appendLine("   - " + task);
        }
    }

    @Override
    public void showTaskRemoved(Task task, int totalTasks) {
        appendLine(" Noted. I've removed this task:");
        appendLine("   " + task);
        appendLine(" Now you have " + totalTasks + " tasks in the list.");
    }

    @Override
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            appendLine(" Nice! Congrats on finishing this task!");
        } else {
            appendLine(" OK, I've forgotten about it already!");
        }
        appendLine("   " + task);
    }

    @Override
    public void showTaskList(ArrayList<Task> tasks) {
        appendLine(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    @Override
    public void showTasksOnDate(ArrayList<Task> foundTasks, TaskList fullTaskList, String date) {
        appendLine(" Tasks on " + date + ":");
        if (foundTasks.isEmpty()) {
            appendLine(" No tasks found for this date.");
        } else {
            ArrayList<Task> allTasks = fullTaskList.getAllTasks();
            for (Task foundTask : foundTasks) {
                int originalIndex = -1;
                for (int i = 0; i < allTasks.size(); i++) {
                    if (allTasks.get(i) == foundTask) {
                        originalIndex = i;
                        break;
                    }
                }
                if (originalIndex != -1) {
                    appendLine(" " + (originalIndex + 1) + "." + foundTask);
                } else {
                    appendLine(" ?." + foundTask);
                }
            }
        }
    }

    @Override
    public void showMatchingTasks(ArrayList<Task> foundTasks, TaskList fullTaskList, String keyword) {
        appendLine(" Here are the matching tasks in your list:");
        if (foundTasks.isEmpty()) {
            appendLine(" No matching tasks found.");
        } else {
            ArrayList<Task> allTasks = fullTaskList.getAllTasks();
            for (Task foundTask : foundTasks) {
                int originalIndex = -1;
                for (int i = 0; i < allTasks.size(); i++) {
                    if (allTasks.get(i) == foundTask) {
                        originalIndex = i;
                        break;
                    }
                }
                if (originalIndex != -1) {
                    appendLine(" " + (originalIndex + 1) + "." + foundTask);
                } else {
                    appendLine(" ?." + foundTask);
                }
            }
        }
    }
}
