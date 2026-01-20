import java.util.Scanner;

public class Chatterbox {
    static class Task {
        private String description;
        private boolean isDone;

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

        @Override
        public String toString() {
            return getStatusIcon() + " " + description;
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
                        System.out.println(" Nice! I've marked this task as done:");
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
                        System.out.println(" OK, I've marked this task as not done yet:");
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