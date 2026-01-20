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
                try {
                    int taskNum = Integer.parseInt(input.substring(5));
                    if (taskNum > 0 && taskNum <= memoryIndex && memory[taskNum - 1] != null) {
                        memory[taskNum - 1].markAsDone();
                        System.out.println(LINE);
                        System.out.println(" Nice! Congrats on finishing this task!");
                        System.out.println("   " + memory[taskNum - 1]);
                        System.out.println(LINE);
                    } else {
                        System.out.println(LINE);
                        System.out.println(" Invalid task number!");
                        System.out.println(LINE);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(LINE);
                    System.out.println(" Please provide a valid task number!");
                    System.out.println(LINE);
                }
                continue;
            }
            // Marks task as not done
            if (input.startsWith("unmark ")) {
                try {
                    int taskNum = Integer.parseInt(input.substring(7));
                    if (taskNum > 0 && taskNum <= memoryIndex && memory[taskNum - 1] != null) {
                        memory[taskNum - 1].markAsNotDone();
                        System.out.println(LINE);
                        System.out.println(" OK, I've forgotten about it already!");
                        System.out.println("   " + memory[taskNum - 1]);
                        System.out.println(LINE);
                    } else {
                        System.out.println(LINE);
                        System.out.println(" Invalid task number!");
                        System.out.println(LINE);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(LINE);
                    System.out.println(" Please provide a valid task number!");
                    System.out.println(LINE);
                }
                continue;
            }
            // Adds a todo task
            if (input.startsWith("todo ")) {
                String description = input.substring(5);
                memory[memoryIndex] = new ToDo(description);
                System.out.println(LINE);
                System.out.println(" Got it. I'll make sure you won't forget this task!");
                System.out.println("   " + memory[memoryIndex]);
                memoryIndex++;
                System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                System.out.println(LINE);
                continue;
            }
            // Adds a deadline task
            if (input.startsWith("deadline ")) {
                String rest = input.substring(9);
                int byIndex = rest.indexOf("/by ");
                if (byIndex != -1) {
                    String description = rest.substring(0, byIndex).trim();
                    String by = rest.substring(byIndex + 4).trim();
                    memory[memoryIndex] = new Deadline(description, by);
                    System.out.println(LINE);
                    System.out.println(" Got it. Remember to complete this task on time!");
                    System.out.println("   " + memory[memoryIndex]);
                    memoryIndex++;
                    System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                    System.out.println(LINE);
                } else {
                    System.out.println(LINE);
                    System.out.println(" Please specify the deadline with /by");
                    System.out.println(LINE);
                }
                continue;
            }
            // Adds an event task
            if (input.startsWith("event ")) {
                String rest = input.substring(6);
                int fromIndex = rest.indexOf("/from ");
                int toIndex = rest.indexOf("/to ");
                if (fromIndex != -1 && toIndex != -1) {
                    String description = rest.substring(0, fromIndex).trim();
                    String from = rest.substring(fromIndex + 6, toIndex).trim();
                    String to = rest.substring(toIndex + 4).trim();
                    memory[memoryIndex] = new Event(description, from, to);
                    System.out.println(LINE);
                    System.out.println(" Got it. Make sure to attend this event!");
                    System.out.println("   " + memory[memoryIndex]);
                    memoryIndex++;
                    System.out.println(" Now you have " + memoryIndex + " tasks in the list.");
                    System.out.println(LINE);
                } else {
                    System.out.println(LINE);
                    System.out.println(" Please specify the event time with /from and /to");
                    System.out.println(LINE);
                }
                continue;
            }
            // Stores input in memory
            memory[memoryIndex] = new Task(input);
            System.out.println(LINE);
            System.out.print("added " + input + "\n");
            System.out.println(LINE);
            memoryIndex++;
        }
        scanner.close();
    }
}