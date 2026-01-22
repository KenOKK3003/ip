import java.util.Scanner;

public class Chatterbox {
    static class Task {
        protected String description;
        protected boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
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
            return "[T]";
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description;
        }
    }

    static class ToDo extends Task {
        public ToDo(String description) {
            super(description);
        }

        @Override
        public String getTypeIcon() {
            return "[T]";
        }
    }

    static class Deadline extends Task {
        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String getTypeIcon() {
            return "[D]";
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description + " (by: " + by + ")";
        }
    }

    static class Event extends Task {
        protected String from;
        protected String to;

        public Event(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String getTypeIcon() {
            return "[E]";
        }

        @Override
        public String toString() {
            return getTypeIcon() + getStatusIcon() + " " + description + " (from: " + from + " to: " + to + ")";
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
        Task[] memory = new Task[100];
        int memoryIndex = 0;
        
        while (true) {
            input = scanner.nextLine();
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
                for (int i = 0; i < memory.length; i++) {
                    if (memory[i] != null) {
                        System.out.println(" " + (i + 1) + "." + memory[i]);
                    }
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
                    if (taskNum > 0 && taskNum <= memoryIndex && memory[taskNum - 1] != null) {
                        memory[taskNum - 1].markAsDone();
                        System.out.println(LINE);
                        System.out.println(" Nice! Congrats on finishing this task!");
                        System.out.println("   " + memory[taskNum - 1]);
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
                continue;
            }
            // Handle just "mark" without number
            if (input.equals("mark")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please specify which task to mark.");
                System.out.println(LINE);
                continue;
            }
            // Marks task as not done
            if (input.startsWith("unmark ")) {
                if (input.substring(7).trim().isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! Please specify which task to unmark.");
                    System.out.println(LINE);
                    continue;
                }
                try {
                    int taskNum = Integer.parseInt(input.substring(7).trim());
                    if (taskNum > 0 && taskNum <= memoryIndex && memory[taskNum - 1] != null) {
                        memory[taskNum - 1].markAsNotDone();
                        System.out.println(LINE);
                        System.out.println(" OK, I've forgotten about it already!");
                        System.out.println("   " + memory[taskNum - 1]);
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
                continue;
            }
            // Handle just "unmark" without number
            if (input.equals("unmark")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! Please specify which task to unmark.");
                System.out.println(LINE);
                continue;
            }
            // Adds a todo task
            if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                if (description.isEmpty()) {
                    System.out.println(LINE);
                    System.out.println(" OOPS!!! The description of a todo cannot be empty.");
                    System.out.println(LINE);
                    continue;
                }
                memory[memoryIndex] = new ToDo(description);
                System.out.println(LINE);
                System.out.println(" Got it. I'll make sure you won't forget this task!");
                System.out.println("   " + memory[memoryIndex]);
                memoryIndex++;
                System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }
            // Handle just "todo" without description
            if (input.equals("todo")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of a todo cannot be empty.");
                System.out.println(LINE);
                continue;
            }
            // Adds a deadline task
            if (input.startsWith("deadline ")) {
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
                memory[memoryIndex] = new Deadline(description, by);
                System.out.println(LINE);
                System.out.println(" Got it. Remember to complete this task on time!");
                System.out.println("   " + memory[memoryIndex]);
                memoryIndex++;
                System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }
            // Handle just "deadline" without description
            if (input.equals("deadline")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of a deadline cannot be empty.");
                System.out.println(LINE);
                continue;
            }
            // Adds an event task
            if (input.startsWith("event ")) {
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
                memory[memoryIndex] = new Event(description, from, to);
                System.out.println(LINE);
                System.out.println(" Got it. Make sure to attend this event!");
                System.out.println("   " + memory[memoryIndex]);
                memoryIndex++;
                System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }
            // Handle just "event" without description
            if (input.equals("event")) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! The description of an event cannot be empty.");
                System.out.println(LINE);
                continue;
            }
            // Unknown command - show error
            System.out.println(LINE);
            System.out.println(" Hmm, I don't recognize that command! Try 'todo', 'deadline', 'event', or 'list'!");
            System.out.println(LINE);
        }
        scanner.close();
    }
}