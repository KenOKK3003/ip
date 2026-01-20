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
        
        while (true) {
            input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(LINE);
                System.out.println(BYE_MSG);
                System.out.println(LINE);
                break;
            }
            System.out.println(LINE);
            System.out.println("Chatterbox echoes: " + input);
            System.out.println(LINE);
        }
        
        scanner.close();
    }
}