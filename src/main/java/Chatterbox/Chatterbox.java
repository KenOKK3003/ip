package chatterbox;

// ==================== Main Chatterbox Class ====================
/**
 * Represents the main entry point for the Chatterbox application.
 * Handles initialisation, command processing loop, and program execution.
 */
public class Chatterbox {
    /** Storage handler for persisting tasks. */
    private Storage storage;
    /** List of tasks. */
    private TaskList tasks;
    /** User interface for CLI. */
    private Ui ui;
    /** Parser for interpreting user commands. */
    private Parser parser;
    /** User interface for GUI. */
    private GuiUi guiUi;
    /** Type of the last executed command. */
    private String lastCommandType = "default";
    /** Whether the last command was an exit command. */
    private boolean lastIsExit = false;

    /**
     * Constructs a Chatterbox instance, loading tasks from the specified file path.
     * If loading fails, starts with an empty task list.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Chatterbox(String filePath) {
        assert filePath != null && !filePath.isBlank() : "File path must not be blank";
        ui = new Ui();
        guiUi = new GuiUi();
        storage = new Storage(filePath);
        parser = new Parser();
        
        try {
            tasks = new TaskList(storage.load());
        } catch (ChatterboxException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main command loop for the Chatterbox application.
     * Handles user input, command execution, and program termination.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = parser.parseCommand(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ChatterboxException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        
        ui.close();
    }

    /**
     * Returns a response for the GUI based on the user's input.
     *
     * @param input User input string.
     * @return Response string to display.
     */
    public String getResponse(String input) {
        assert input != null : "User input must not be null";
        guiUi.clear();
        try {
            Command command = parser.parseCommand(input);
            assert command != null : "Parser must return a command";
            lastCommandType = mapCommandType(command);
            lastIsExit = command.isExit();
            command.execute(tasks, guiUi, storage);
            return guiUi.consumeOutput();
        } catch (ChatterboxException e) {
            lastCommandType = "default";
            lastIsExit = false;
            guiUi.showError(e.getMessage());
            return guiUi.consumeOutput();
        }
    }

    /**
     * Returns a welcome message for the GUI.
     *
     * @return Welcome message string.
     */
    public String getWelcomeMessage() {
        guiUi.clear();
        guiUi.showWelcome();
        return guiUi.consumeOutput();
    }

    /**
     * Returns the type of the last executed command.
     *
     * @return Command type string.
     */
    public String getCommandType() {
        return lastCommandType;
    }

    /**
     * Returns whether the last command was an exit command.
     *
     * @return True if last command was exit, false otherwise.
     */
    public boolean isExit() {
        return lastIsExit;
    }

    private String mapCommandType(Command command) {
        assert command != null : "Command must not be null";
        if (command instanceof AddTodoCommand
                || command instanceof AddDeadlineCommand
                || command instanceof AddEventCommand) {
            return "AddCommand";
        }
        if (command instanceof MarkCommand) {
            return "ChangeMarkCommand";
        }
        if (command instanceof DeleteCommand) {
            return "DeleteCommand";
        }
        return "default";
    }

    /**
     * Launches the Chatterbox application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Chatterbox("./data/chatterbox.txt").run();
    }
}