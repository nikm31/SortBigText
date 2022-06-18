package impl;

import interfaces.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtilsImpl implements FileUtils {
    private final Logger logger = Logger.getLogger(FileUtilsImpl.class.getName());
    private final Path currentPath;

    public FileUtilsImpl() {
        currentPath = Paths.get("");
    }

    @Override
    public File writeFileToDisk(String text, String filename) {
        Path fileToWrite = currentPath.resolve(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite.toString()))) {
            logger.log(Level.INFO, "Writing file to disk: " + filename);
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        return fileToWrite.toAbsolutePath().toFile();
    }

    @Override
    public void deleteFiles(List<File> files) {
        files.forEach(File::delete);
        logger.log(Level.INFO, "Deleting temp files is done!");
    }
}
