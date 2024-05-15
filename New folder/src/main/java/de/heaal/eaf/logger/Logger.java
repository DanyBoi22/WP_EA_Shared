package de.heaal.eaf.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    /**
     * Logs the line of data into a specified csv file
     *
     * @param data String of float values
     * @param filePath file name to write the data in
     */
    public static void logLineToCSV(Float[] data, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            StringBuilder sb = new StringBuilder();
            for (Float number : data) {
                sb.append(number).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove the last comma
            writer.write(sb.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a log file with specified filePath.
     * If there is already existing file with this name,
     * creates new file with the same name and adds/increments counter at the end.
     * -> "file" already exist, creates "file1". "file1" exists, creates "file2"
     *
     * @param filePath file name you want to create
     * @return the file path of newly created file or null
     */
    public static String createLogFile(String filePath) {
        String newFilePath = filePath;

        // Check if the file already exists
        File file = new File(filePath);
        int counter = 1;
        while (file.exists()) {
            // Append counter to the file name
            int dotIndex = filePath.lastIndexOf(".");
            if (dotIndex != -1) {
                newFilePath = filePath.substring(0, dotIndex) + counter + filePath.substring(dotIndex);
            } else {
                newFilePath = filePath + counter;
            }
            file = new File(newFilePath);
            counter++;
        }

        // Create the new file
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return newFilePath;
    }
}
