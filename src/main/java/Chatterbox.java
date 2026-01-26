import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Chatterbox {
    // File paths
    private static final String DATA_DIR = "./data";
    private static final String DATA_FILE = DATA_DIR + "/chatterbox.txt";
    
    enum TaskType {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String icon;

        TaskType(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
        
        public static TaskType fromString(String type) {
            switch (type) {
            case "T":
                return TODO;
            case "D":
                return DEADLINE;
            case "E":
                return EVENT;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
            }
        }
    }

    static class Task {
        protected String description;
        protected boolean isDone;
        protected TaskType type;

        public Task(String description, TaskType type) {
            this.description = description;
            this.isDone = false;
            this.type = type;
        }

        public Task(String description, TaskType type, boolean isDone) {
            this.description = description;
            this.isDone = isDone;
            this.type = type;
        }

        public void markAsDone() {
            this.isDone = true;
        }

        public void markAsNotDone() {
            this.isDone = false;
        }

        public String getStatusIcon() {
            return (isDone ? "[X]" : "[ ]");
        }

        public String getDescription() {
            return description;
        }

        public String getTypeIcon() {
            return "[" + type.getIcon() + "]";
        }
        
        public String toFileFormat() {
            return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description;
        }
    }

    static class ToDo extends Task {
        public ToDo(String description) {
            super(description, TaskType.TODO);
        }
        
        public ToDo(String description, boolean isDone) {
            super(description, TaskType.TODO, isDone);
        }
        
        @Override
        public String toFileFormat() {
            return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
        }
    }

    static class Deadline extends Task {
        protected String by;

        public Deadline(String description, String by) {
            super(description, TaskType.DEADLINE);
            this.by = by;
        }
        
        public Deadline(String description, String by, boolean isDone) {
            super(description, TaskType.DEADLINE, isDone);
            this.by = by;
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description + " (by: " + by + ")";
        }
        
        @Override
        public String toFileFormat() {
            return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description + " | " + by;
        }
    }

    static class Event extends Task {
        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description, TaskType.EVENT);
            this.from = from;
            this.to = to;
        }
        
        public Event(String description, String from, String to, boolean isDone) {
            super(description, TaskType.EVENT, isDone);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description + " (from: " + from + " to: " + to + ")";
        }
        
        @Override
        public String toFileFormat() {
            return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description + " | " + from + " | " + to;
        }
    }
    
    /**
     * Loads tasks from the data file.
     * Creates data directory and file if they don't exist.
     * 
     * @return List of loaded tasks
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        
        try {
            Path dataDirPath = Paths.get(DATA_DIR);
            Path dataFilePath = Paths.get(DATA_FILE);
            
            // Create data directory if it doesn't exist
            if (!Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            // Create data file if it doesn't exist
            if (!Files.exists(dataFilePath)) {
                Files.createFile(dataFilePath);
                return tasks; // Empty list for new file
            }
            
            // Read all lines from file
            java.util.List<String> lines = Files.readAllLines(dataFilePath);
            
            for (String line : lines) {
                try {
                    Task task = parseTaskFromFile(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Skipping corrupted line: " + line);
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * Parses a single line from the data file into a Task object.
     * 
     * @param line The line from the data file
     * @return Task object, or null if line is empty
     * @throws IllegalArgumentException if line format is invalid
     */
    private static Task parseTaskFromFile(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(" \\| ");
        
        // Minimum 3 parts: type | status | description
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid format: " + line);
        }
        
        String typeStr = parts[0].trim();
        String statusStr = parts[1].trim();
        String description = parts[2].trim();
        
        boolean isDone = statusStr.equals("1");
        
        try {
            TaskType type = TaskType.fromString(typeStr);
            
            switch (type) {
            case TODO:
                return new ToDo(description, isDone);
                
            case DEADLINE:
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Deadline missing 'by' field: " + line);
                }
                String by = parts[3].trim();
                return new Deadline(description, by, isDone);
                
            case EVENT:
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Event missing 'from' or 'to' field: " + line);
                }
                String from = parts[3].trim();
                String to = parts[4].trim();
                return new Event(description, from, to, isDone);
                
            default:
                throw new IllegalArgumentException("Unknown task type: " + typeStr);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse task: " + line, e);
        }
    }
    
    /**
     * Saves all tasks to the data file.
     * 
     * @param tasks The list of tasks to save
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            // Ensure data directory exists
            Path dataDirPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            FileWriter writer = new FileWriter(DATA_FILE);
            
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
            
            writer.close();
            
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String LINE = "________________________________";
        String GREET_MSG = " Hello! I'm Chatterbox\n" +
                            " What can I do for you?";
        String BYE_MSG = " Bye! Hope to see you again soon!";
        
        System.out.println(LINE);
        System.out.println(GREET_MSG);
        System.out.println(LINE);

        Scanner scanner = new Scanner(System.in);
        String input;
        
        // Load tasks from file
        ArrayList<Task> tasks = loadTasks();
        System.out.println(" Loaded " + tasks.size() + " tasks from memory.");
        System.out.println(LINE);
        
        while (true) {
            input = scanner.nextLine();
            boolean tasksChanged = false;
            
            // Exits if user types "bye"
            if (input.equals("bye")) {
                System.out.println(LINE);
                System.out.println(BYE_MSG);
                System.out.println(LINE);
                break;
            }
            
            // Handle empty input
            if (input.trim().isEmpty()) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please enter a command.");
                System.out.println(LINE);
                continue;
            }
            
            // Prints out memory if user types "list"
            if (input.equals("list")) {
                System.out.println(LINE);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println(" " + (i + 1) + "." + tasks.get(i));
                }
                System.out.println(LINE);
                continue;
            }
            
            // Marks task as done
            if (input.startsWith("mark ")) {
                if (input.substring(5).trim().isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify which task to mark.");
                    System.out.println(LINE);
                    continue;
                }
                try {
                    int taskNum = Integer.parseInt(input.substring(5).trim());
                    if (taskNum > 0 && taskNum <= tasks.size()) {
                        tasks.get(taskNum - 1).markAsDone();
                        tasksChanged = true;
                        System.out.println(LINE);
                        System.out.println(" Nice! Congrats on finishing this task!");
                        System.out.println("   " + tasks.get(taskNum - 1));
                        System.out.println(LINE);
                    } else {
                        System.out.println(LINE);
                        System.out.println(" OOPS!!! Task number " + taskNum + " does not exist.");
                        System.out.println(LINE);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please provide a valid task number.");
                    System.out.println(LINE);
                }
            }
            // Handle just "mark" without number
            else if (input.equals("mark")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please specify which task to mark.");
                System.out.println(LINE);
            }
            // Marks task as not done
            else if (input.startsWith("unmark ")) {
                if (input.substring(7).trim().isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify which task to unmark.");
                    System.out.println(LINE);
                    continue;
                }
                try {
                    int taskNum = Integer.parseInt(input.substring(7).trim());
                    if (taskNum > 0 && taskNum <= tasks.size()) {
                        tasks.get(taskNum - 1).markAsNotDone();
                        tasksChanged = true;
                        System.out.println(LINE);
                        System.out.println(" OK, I've forgotten about it already!");
                        System.out.println("   " + tasks.get(taskNum - 1));
                        System.out.println(LINE);
                    } else {
                        System.out.println(LINE);
                        System.out.println(" OOPS!!! Task number " + taskNum + " does not exist.");
                        System.out.println(LINE);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please provide a valid task number.");
                    System.out.println(LINE);
                }
            }
            // Handle just "unmark" without number
            else if (input.equals("unmark")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please specify which task to unmark.");
                System.out.println(LINE);
            }
            // Deletes a task
            else if (input.startsWith("delete ")) {
                if (input.substring(7).trim().isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify which task to delete.");
                    System.out.println(LINE);
                    continue;
                }
                try {
                    int taskNum = Integer.parseInt(input.substring(7).trim());
                    if (taskNum > 0 && taskNum <= tasks.size()) {
                        Task removedTask = tasks.remove(taskNum - 1);
                        tasksChanged = true;
                        System.out.println(LINE);
                        System.out.println(" Noted. I've removed this task:");
                        System.out.println("   " + removedTask);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(LINE);
                    } else {
                        System.out.println(LINE);
                        System.out.println(" OOPS!!! Task number " + taskNum + " does not exist.");
                        System.out.println(LINE);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please provide a valid task number.");
                    System.out.println(LINE);
                }
            }
            // Handle just "delete" without number
            else if (input.equals("delete")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please specify which task to delete.");
                System.out.println(LINE);
            }
            // Adds a todo task
            else if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                if (description.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of a todo cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                Task newTask = new ToDo(description);
                tasks.add(newTask);
                tasksChanged = true;
                System.out.println(LINE);
                System.out.println(" Got it. I'll make sure you won't forget this task!");
                System.out.println("   " + newTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(LINE);
            }
            // Handle just "todo" without description
            else if (input.equals("todo")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of a todo cannot be empty.");
                System.out.println(LINE);
            }
            // Adds a deadline task
            else if (input.startsWith("deadline ")) {
                String rest = input.substring(9).trim();
                if (rest.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of a deadline cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                int byIndex = rest.indexOf("/by ");
                if (byIndex == -1) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify the deadline with /by");
                    System.out.println(LINE);
                    continue;
                }
                String description = rest.substring(0, byIndex).trim();
                String by = rest.substring(byIndex + 4).trim();
                if (description.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of a deadline cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                if (by.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The deadline date/time cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                Task newTask = new Deadline(description, by);
                tasks.add(newTask);
                tasksChanged = true;
                System.out.println(LINE);
                System.out.println(" Got it. Remember to complete this task on time!");
                System.out.println("   " + newTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(LINE);
            }
            // Handle just "deadline" without description
            else if (input.equals("deadline")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of a deadline cannot be empty.");
                System.out.println(LINE);
            }
            // Adds an event task
            else if (input.startsWith("event ")) {
                String rest = input.substring(6).trim();
                if (rest.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of an event cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                int fromIndex = rest.indexOf("/from ");
                int toIndex = rest.indexOf("/to ");
                if (fromIndex == -1 || toIndex == -1) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify the event time with /from and /to");
                    System.out.println(LINE);
                    continue;
                }
                String description = rest.substring(0, fromIndex).trim();
                String from = rest.substring(fromIndex + 6, toIndex).trim();
                String to = rest.substring(toIndex + 4).trim();
                if (description.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of an event cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                if (from.isEmpty() || to.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The event time cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                Task newTask = new Event(description, from, to);
                tasks.add(newTask);
                tasksChanged = true;
                System.out.println(LINE);
                System.out.println(" Got it. Make sure to attend this event!");
                System.out.println("   " + newTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(LINE);
            }
            // Handle just "event" without description
            else if (input.equals("event")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of an event cannot be empty.");
                System.out.println(LINE);
            }
            // Unknown command - show error
            else {
                System.out.println(LINE);
                System.out.println(" Hmm, I don't recognize that command! Try 'todo', 'deadline', 'event', or 'list'!");
                System.out.println(LINE);
            }
            
            // Save tasks if they were modified
            if (tasksChanged) {
                saveTasks(tasks);
            }
        }
        scanner.close();
    }
}