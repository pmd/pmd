/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * This is a utility class for working with Files.
 *
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public final class FileUtil {

    private FileUtil() {
    }

    /**
     * Helper method to get a filename without its extension
     *
     * @param fileName
     *            String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;

        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }

    /**
     * Normalizes the filename by taking the casing into account, e.g. on
     * Windows, the filename is changed to lowercase only.
     *
     * @param fileName
     *            the file name
     * @return the normalized file name
     */
    public static String normalizeFilename(String fileName) {
        if (fileName != null && File.separatorChar == '\\') {
            // windows
            return fileName.toLowerCase(Locale.ROOT);
        }
        return fileName;
    }

    public static @NonNull Path toExistingPath(String root) throws FileNotFoundException {
        Path file = Paths.get(root);
        if (!Files.exists(file)) {
            throw new FileNotFoundException(root);
        }
        return file;
    }

    /**
     * Handy method to find a certain pattern into a file. While this method
     * lives in the FileUtils, it was designed with with unit test in mind (to
     * check result redirected into a file)
     *
     * @param file
     * @param pattern
     * @return
     */
    public static boolean findPatternInFile(final File file, final String pattern) {

        Pattern regexp = Pattern.compile(pattern);
        Matcher matcher = regexp.matcher("");

        try {
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                matcher.reset(line); // reset the input
                if (matcher.find()) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * Reads the file, which contains the filelist. This is used for the
     * command line arguments --filelist/-filelist for both PMD and CPD.
     * The separator in the filelist is a comma and/or newlines.
     *
     * @param filelist the file which contains the list of path names
     *
     * @return a list of file paths
     *
     * @throws IOException if the file couldn't be read
     */
    public static List<Path> readFilelistEntries(Path filelist) throws IOException {
        return Files.readAllLines(filelist).stream()
                    .flatMap(it -> Arrays.stream(it.split(",")))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .map(Paths::get)
                    .collect(Collectors.toList());
    }


}
