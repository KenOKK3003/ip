import java.util.Scanner;

public class Chatterbox {
    public static void main(String[] args) {
        String GREET_MSG = "Hello! I'm Chatterbox\n" +
                            "What can I do for you?";
        String BYE_MSG = "Bye! Hope to see you again soon!";
        
        System.out.println(GREET_MSG);

        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(BYE_MSG);
                break;
            }
            System.out.println("Chatterbox replies: " + input);
        }
        
        scanner.close();
    }
}