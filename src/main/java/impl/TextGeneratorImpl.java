package impl;

import interfaces.FileUtils;
import interfaces.TextGenerator;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextGeneratorImpl implements TextGenerator {

    private final Logger logger = Logger.getLogger(TextGeneratorImpl.class.getName());

    private final static String allSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase() + "0123456789";

    private final int SPLITTER_COUNT = 10;

    public final long charsInLine;

    public final long totalLines;

    private final FileUtils fileUtils;

    public TextGeneratorImpl(long charsInLine, long totalLines) {
        if (charsInLine < 2) {
            throw new IllegalArgumentException("Line size must be >= 2 chars");
        }
        this.charsInLine = charsInLine;
        this.totalLines = totalLines;
        this.fileUtils = new FileUtilsImpl();
    }

    @Override
    public List<File> generateTextParts() {

        long totalMemory = Runtime.getRuntime().maxMemory();
        long linesInOneFile = totalMemory / (charsInLine * SPLITTER_COUNT);
        long tempPartsCount = totalLines / linesInOneFile;

        if (totalMemory > (charsInLine * totalLines)) {
            tempPartsCount = 1;
            linesInOneFile = totalLines;
        }

        if (totalLines % linesInOneFile != 0) {
            tempPartsCount++;
        }

        List<File> tempFiles = new LinkedList<>();

        int linesCounter = (int) totalLines;

        for (int i = 0; i < tempPartsCount; i++) {
            logger.log(Level.INFO, "Generating Temp txt  №: " + i);
            StringBuilder tempText = new StringBuilder();

            for (int j = 0; j < linesInOneFile; j++) {
                tempText.append(RandomStringUtils.random((int) charsInLine, allSymbols.toCharArray()));
                tempText.append("\n");
            }

            tempFiles.add(fileUtils.writeFileToDisk(tempText.toString(), UUID.randomUUID().toString()));

            linesCounter -= linesInOneFile;

            if (linesCounter < linesInOneFile) {
                linesInOneFile = linesCounter;
            }
        }

        logger.log(Level.INFO, "Generating and writing temp Files is ready!"
                + "\nParts Count = " + tempPartsCount
                + "\nlinesInOneFile: " + linesInOneFile);

        return tempFiles;
    }
}
