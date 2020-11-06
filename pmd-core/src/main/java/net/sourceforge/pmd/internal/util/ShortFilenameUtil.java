/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cli.PMDParameters;

public final class ShortFilenameUtil {

    private ShortFilenameUtil() {
    }

    /**
     * Determines the filename that should be used in the report depending on the
     * option "shortnames". If the option is enabled, then the filename in the report
     * is without the directory prefix of the directories, that have been analyzed.
     * If the option "shortnames" is not enabled, then the inputFileName is returned as-is.
     *
     * @param inputPathPrefixes
     * @param inputFileName
     * @return
     *
     * @see PMDConfiguration#isReportShortNames()
     * @see PMDParameters#isShortnames()
     */
    public static String determineFileName(List<String> inputPathPrefixes, String inputFileName) {
        for (final String prefix : inputPathPrefixes) {
            final Path prefPath = Paths.get(prefix).toAbsolutePath();
            final String prefPathString = prefPath.toString();

            if (inputFileName.startsWith(prefPathString)) {
                if (prefPath.toFile().isDirectory()) {
                    return trimAnyPathSep(inputFileName.substring(prefPathString.length()));
                } else {
                    if (inputFileName.indexOf(File.separatorChar) == -1) {
                        return inputFileName;
                    }
                    return trimAnyPathSep(inputFileName.substring(prefPathString.lastIndexOf(File.separatorChar)));
                }
            }
        }

        return inputFileName;
    }

    private static String trimAnyPathSep(String name) {
        return name != null && name.charAt(0) == File.separatorChar ? name.substring(1) : name;
    }
}
