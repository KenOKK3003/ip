package chatterbox;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents a list of tasks in the Chatterbox application.
 * Provides methods to add, remove, retrieve, and search for tasks.
 */
class TaskList {
    /** List of tasks. */
    private ArrayList<Task> tasks;
    
    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }
    
    /**
     * Constructs a TaskList with the specified list of tasks.
     *
     * @param tasks List of tasks to initialise the TaskList with.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must not be null";
        this.tasks = tasks;
    }
    
    /**
     * Adds a task to the task list.
     *
     * @param task Task to be added.
     */
    public void addTask(Task task) {
        assert task != null : "Task must not be null";
        tasks.add(task);
    }
    
    /**
     * Removes the task at the specified index from the task list.
     *
     * @param index Index of the task to remove (0-based).
     * @return The removed task.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public Task removeTask(int index) throws ChatterboxException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChatterboxException("Task number " + (index + 1) + " does not exist.");
        }
        return tasks.remove(index);
    }
    
    /**
     * Returns the task at the specified index.
     *
     * @param index Index of the task to retrieve (0-based).
     * @return The task at the specified index.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public Task getTask(int index) throws ChatterboxException {
        if (index < 0 || index >= tasks.size()) {
            throw new ChatterboxException("Task number " + (index + 1) + " does not exist.");
        }
        return tasks.get(index);
    }
    
    /**
     * Marks the task at the specified index as done or not done.
     *
     * @param index Index of the task to mark (0-based).
     * @param isDone True to mark as done, false to mark as not done.
     * @throws ChatterboxException If the index is out of bounds.
     */
    public void markTask(int index, boolean isDone) throws ChatterboxException {
        Task task = getTask(index);
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }
    
    /**
     * Returns the list of all tasks in the task list.
     *
     * @return List of all tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return tasks;
    }
    
    /**
     * Returns the number of tasks in the task list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }
    
    /**
     * Checks if a new event clashes with any existing events.
     *
     * @param newFrom Start time of the new event.
     * @param newTo End time of the new event.
     * @return List of clashing events, empty if no clashes.
     */
    public ArrayList<Task> findClashingEvents(LocalDateTime newFrom, LocalDateTime newTo) {
        ArrayList<Task> clashes = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Event) {
                Event event = (Event) task;
                // Check if time ranges overlap
                if (newFrom.isBefore(event.getTo()) && newTo.isAfter(event.getFrom())) {
                    clashes.add(task);
                }
            }
        }
        return clashes;
    }
    
    /**
     * Returns a list of tasks that occur on the specified date.
     *
     * @param date Date to search for tasks.
     * @return List of tasks occurring on the given date.
     */
    public ArrayList<Task> findTasksOnDate(LocalDateTime date) {
        ArrayList<Task> result = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.getBy().toLocalDate().equals(date.toLocalDate())) {
                    result.add(task);
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                // Check if the event occurs on the given date
                if (!event.getFrom().toLocalDate().isAfter(date.toLocalDate()) && 
                    !event.getTo().toLocalDate().isBefore(date.toLocalDate())) {
                    result.add(task);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Finds tasks whose description contains the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return List of matching tasks.
     */
    public ArrayList<Task> findTasksByKeyword(String keyword) {
        ArrayList<Task> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                result.add(task);
            }
        }
        return result;
    }
}
