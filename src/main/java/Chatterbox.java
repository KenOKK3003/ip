import java.util.Scanner;

public class Chatterbox {
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
        String[] memory = new String[100];
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
                for (int i = 0; i < memory.length; i++) {
                    if (memory[i] != null) {
                        System.out.println(i + 1 + ". " + memory[i]);
                    }
                }
                System.out.println(LINE);
                continue;
            }
            // Stores input in memory
            memory[memoryIndex] = input;
            System.out.println(LINE);
            System.out.print("added " + input + "\n");
            System.out.println(LINE);
            memoryIndex++;
        }
        scanner.close();
    }
}