/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Implementation that handles a Document as a file in the filesystem and receives operations in a sorted manner
 * (i.e. the regions are sorted). This improves the efficiency of reading the file by only scanning it once while
 * operations are applied, until an instance of this document is closed.
 */
public class DocumentFile implements Document, Closeable {

    private static final Logger LOG = Logger.getLogger(DocumentFile.class.getName());

    private List<Integer> lineToOffset = new ArrayList<>();

    private final Path filePath;
    private final BufferedReader reader;
    private int currentPosition = 0;

    private final Path temporaryPath = Files.createTempFile("pmd-", ".tmp");
    private final Writer writer;

    public DocumentFile(final File file, final Charset charset) throws IOException {
        reader = Files.newBufferedReader(requireNonNull(file).toPath(), requireNonNull(charset));
        writer = Files.newBufferedWriter(temporaryPath, charset);
        this.filePath = file.toPath();
        mapLinesToOffsets();
    }

    private void mapLinesToOffsets() throws IOException {
        try (Scanner scanner = new Scanner(filePath)) {
            int currentGlobalOffset = 0;

            while (scanner.hasNextLine()) {
                lineToOffset.add(currentGlobalOffset);
                currentGlobalOffset += getLineLengthWithLineSeparator(scanner);
            }
        }
    }

    /**
     * Sums the line length without the line separation and the characters which matched the line separation pattern
     * @param scanner the scanner from which to read the line's length
     * @return the length of the line with the line separator.
     */
    private int getLineLengthWithLineSeparator(final Scanner scanner) {
        int lineLength = scanner.nextLine().length();
        final String lineSeparationMatch = scanner.match().group(1);

        if (lineSeparationMatch != null) {
            lineLength += lineSeparationMatch.length();
        }

        return lineLength;
    }

    @Override
    public void insert(int beginLine, int beginColumn, final String textToInsert) {
        try {
            tryToInsertIntoFile(beginLine, beginColumn, textToInsert);
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "An exception occurred when inserting into file " + filePath);
        }
    }

    private void tryToInsertIntoFile(int beginLine, int beginColumn, final String textToInsert) throws IOException {
        final int offset = mapToOffset(beginLine, beginColumn);
        writeUntilOffsetReached(offset);
        writer.write(textToInsert);
    }

    private int mapToOffset(final int line, final int column) {
        return lineToOffset.get(line) + column;
    }

    /**
     * Write characters between the current offset until the next offset to be read
     * @param nextOffsetToRead the position in which the reader will stop reading
     * @throws IOException if an I/O error occurs
     */
    private void writeUntilOffsetReached(final int nextOffsetToRead) throws IOException {
        if (nextOffsetToRead < currentPosition) {
            throw new IllegalStateException();
        }
        final char[] bufferToCopy = new char[nextOffsetToRead - currentPosition];
        reader.read(bufferToCopy);
        writer.write(bufferToCopy);
        currentPosition = nextOffsetToRead;
    }

    @Override
    public void replace(final RegionByLine regionByLine, final String textToReplace) {
        try {
            tryToReplaceInFile(mapToRegionByOffset(regionByLine), textToReplace);
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "An exception occurred when replacing in file " + filePath.toAbsolutePath());
        }
    }

    private RegionByOffset mapToRegionByOffset(final RegionByLine regionByLine) {
        final int startOffset = mapToOffset(regionByLine.getBeginLine(), regionByLine.getBeginColumn());
        final int endOffset = mapToOffset(regionByLine.getEndLine(), regionByLine.getEndColumn());

        return new RegionByOffsetImp(startOffset, endOffset - startOffset);
    }

    private void tryToReplaceInFile(final RegionByOffset regionByOffset, final String textToReplace) throws IOException {
        writeUntilOffsetReached(regionByOffset.getOffset());
        reader.skip(regionByOffset.getLength());
        currentPosition = regionByOffset.getOffsetAfterEnding();
        writer.write(textToReplace);
    }

    @Override
    public void delete(final RegionByLine regionByOffset) {
        try {
            tryToDeleteFromFile(mapToRegionByOffset(regionByOffset));
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "An exception occurred when deleting from file " + filePath.toAbsolutePath());
        }
    }

    private void tryToDeleteFromFile(final RegionByOffset regionByOffset) throws IOException {
        writeUntilOffsetReached(regionByOffset.getOffset());
        reader.skip(regionByOffset.getLength());
        currentPosition = regionByOffset.getOffsetAfterEnding();
    }

    @Override
    public void close() throws IOException {
        if (reader.ready()) {
            writeUntilEOF();
        }
        reader.close();
        writer.close();

        Files.move(temporaryPath, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeUntilEOF() throws IOException {
        IOUtils.copy(reader, writer);
    }

    /* package-private */ List<Integer> getLineToOffset() {
        return lineToOffset;
    }
}
