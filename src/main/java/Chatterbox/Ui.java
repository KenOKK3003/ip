package chatterbox;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles user interface interactions for the Chatterbox application.
 */
class Ui {
    /** Separator line for CLI output. */
    private static final String LINE = "________________________________";
    /** Scanner for reading user input. */
    private Scanner scanner;
    
    public Ui() {
        scanner = new Scanner(System.in);
    }
    
    /**
     * Displays the welcome message.
     */
    public void showWelcome() {
        showLine();
        System.out.println(" Hello! I'm Chatterbox");
        System.out.println(" What can I do for you?");
        showLine();
    }
    
    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(" Bye! Hope to see you again soon!");
    }
    
    /**
     * Displays a separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }
    
    /**
     * Reads a command from the user.
     *
     * @return User input string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }
    
    /**
     * Displays an error message.
     */
    public void showError(String message) {
        System.out.println(" OOPS!!! " + message);
    }
    
    /**
     * Displays a loading error message.
     */
    public void showLoadingError(String message) {
        System.err.println("Error loading tasks: " + message);
    }
    
    /**
     * Displays a confirmation message for a newly added task.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + totalTasks + " tasks in the list.");
    }
    
    /**
     * Displays a warning about schedule clashes.
     */
    public void showScheduleClash(ArrayList<Task> clashingTasks) {
        System.out.println(" Warning: This event clashes with:");
        for (Task task : clashingTasks) {
            System.out.println("   - " + task);
        }
    }
    
    /**
     * Displays a confirmation message for a removed task.
     */
    public void showTaskRemoved(Task task, int totalTasks) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + totalTasks + " tasks in the list.");
    }
    
    /**
     * Displays a confirmation message for marking or unmarking a task.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            System.out.println(" Nice! Congrats on finishing this task!");
        } else {
            System.out.println(" OK, I've forgotten about it already!");
        }
        System.out.println("   " + task);
    }
    
    /**
     * Displays the list of all tasks.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }
    
    /**
     * Displays tasks occurring on a specific date.
     */
    public void showTasksOnDate(ArrayList<Task> foundTasks, TaskList fullTaskList, String date) {
        System.out.println(" Tasks on " + date + ":");
        if (foundTasks.isEmpty()) {
            System.out.println(" No tasks found for this date.");
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
                    System.out.println(" " + (originalIndex + 1) + "." + foundTask);
                } else {
                    System.out.println(" ?." + foundTask);
                }
            }
        }
    }
    
    /**
     * Displays the list of tasks that match a keyword search.
     */
    public void showMatchingTasks(ArrayList<Task> foundTasks, TaskList fullTaskList, String keyword) {
            System.out.println(" Here are the matching tasks in your list:");
            if (foundTasks.isEmpty()) {
                System.out.println(" No matching tasks found.");
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
                        System.out.println(" " + (originalIndex + 1) + "." + foundTask);
                    } else {
                        System.out.println(" ?." + foundTask); // fallback if not found
                    }
                }
            }
        }
    
    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }
}
