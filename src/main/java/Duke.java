public class Duke {
    private String commandType;

    public static void main(String[] args) {
        System.out.println("Hello!");
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        commandType = "default";
        return "Duke heard: " + input;
    }
    
    public String getCommandType() {
        return commandType;
    }
}
