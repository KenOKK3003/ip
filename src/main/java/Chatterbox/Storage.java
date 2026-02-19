package chatterbox;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Handles loading and saving of tasks to and from a file for the Chatterbox application.
 * Provides methods to persist and retrieve the task list.
 */
class Storage {
    /** Path to the data file. */
    private String filePath;
    /** Formatter for parsing dates from files. */
    private static final DateTimeFormatter FILE_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    
    /**
     * Constructs a Storage object with the specified file path.
     *
     * @param filePath Path to the data file for storing tasks.
     */
    public Storage(String filePath) {
        assert filePath != null && !filePath.isBlank() : "File path must not be blank";
        this.filePath = filePath;
    }
    
    /**
     * Checks if the data file exists.
     *
     * @return True if the file exists, false otherwise.
     */
    public boolean fileExists() {
        Path dataFilePath = Paths.get(filePath);
        return Files.exists(dataFilePath);
    }
    
    /**
     * Creates the data file and its parent directory if they don't exist.
     *
     * @throws ChatterboxException If an error occurs while creating the file.
     */
    public void createFile() throws ChatterboxException {
        try {
            Path dataFilePath = Paths.get(filePath);
            Path dataDirPath = dataFilePath.getParent();
            
            // Create data directory if it doesn't exist
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            // Create data file if it doesn't exist
            if (!Files.exists(dataFilePath)) {
                Files.createFile(dataFilePath);
            }
        } catch (IOException e) {
            throw new ChatterboxException("Error creating file: " + e.getMessage());
        }
    }
    
    /**
     * Loads tasks from the data file.
     * Creates the file and its parent directory if they do not exist.
     *
     * @return List of tasks loaded from the file.
     * @throws ChatterboxException If an error occurs while loading tasks.
     */
    public ArrayList<Task> load() throws ChatterboxException {
        ArrayList<Task> tasks = new ArrayList<>();
        
        try {
            Path dataFilePath = Paths.get(filePath);
            Path dataDirPath = dataFilePath.getParent();
            
            // Create data directory if it doesn't exist
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
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
            throw new ChatterboxException("Error loading tasks: " + e.getMessage());
        }
        
        return tasks;
    }
    
    private Task parseTaskFromFile(String line) {
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
            Task.TaskType type = Task.TaskType.fromString(typeStr);
            
            switch (type) {
            case TODO:
                return new ToDo(description, isDone);
                
            case DEADLINE:
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Deadline missing 'by' field: " + line);
                }
                String byStr = parts[3].trim();
                LocalDateTime by = LocalDateTime.parse(byStr, FILE_DATE_FORMATTER);
                return new Deadline(description, by, isDone);
                
            case EVENT:
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Event missing 'from' or 'to' field: " + line);
                }
                String fromStr = parts[3].trim();
                String toStr = parts[4].trim();
                LocalDateTime from = LocalDateTime.parse(fromStr, FILE_DATE_FORMATTER);
                LocalDateTime to = LocalDateTime.parse(toStr, FILE_DATE_FORMATTER);
                return new Event(description, from, to, isDone);
                
            default:
                throw new IllegalArgumentException("Unknown task type: " + typeStr);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in: " + line, e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse task: " + line, e);
        }
    }
    
    /**
     * Saves the given list of tasks to the data file.
     * Creates the file and its parent directory if they do not exist.
     *
     * @param tasks List of tasks to save.
     * @throws ChatterboxException If an error occurs while saving tasks.
     */
    public void save(ArrayList<Task> tasks) throws ChatterboxException {
        assert tasks != null : "Tasks list must not be null";
        try {
            Path dataFilePath = Paths.get(filePath);
            Path dataDirPath = dataFilePath.getParent();
            
            // Ensure data directory exists
            if (dataDirPath != null && !Files.exists(dataDirPath)) {
                Files.createDirectories(dataDirPath);
            }
            
            FileWriter writer = new FileWriter(filePath);
            
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
            
            writer.close();
            
        } catch (IOException e) {
            throw new ChatterboxException("Error saving tasks: " + e.getMessage());
        }
    }
}
