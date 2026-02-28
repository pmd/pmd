/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.internal.util.ClasspathClassLoader;

/**
 * Represents a classpath used for JVM languages during analysis.
 *
 * @since 7.21.0
 */
public class AnalysisClasspath {
    private final List<Path> classpath = new ArrayList<>();

    public void prepend(String classpath) {
        if (StringUtils.isBlank(classpath)) {
            return;
        }

        this.classpath.addAll(0, from(classpath).getClasspath());
    }

    public List<Path> getClasspath() {
        return classpath;
    }

    @Override
    public String toString() {
        return asString();
    }

    /**
     * Returns a platform dependend string of all classpath components.
     * @return
     */
    public String asString() {
        return classpath.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator));
    }

    public static AnalysisClasspath currentJvm() {
        Path javaHome = Paths.get(System.getProperty("java.home"));
        Path jrtFs = javaHome.resolve("lib/jrt-fs.jar");
        Path rtJar = javaHome.resolve("lib/rt.jar");

        if (Files.exists(jrtFs)) {
            AnalysisClasspath analysisClasspath = new AnalysisClasspath();
            analysisClasspath.prepend(jrtFs.toString());
            return analysisClasspath;
        } else if (Files.exists(rtJar)) {
            AnalysisClasspath analysisClasspath = new AnalysisClasspath();
            analysisClasspath.prepend(rtJar.toString());
            return analysisClasspath;
        }
        throw new IllegalStateException("Could not determine current jvm classpath");
    }

    public static AnalysisClasspath from(String classpath) {
        List<Path> paths = ClasspathClassLoader.parseClasspath(classpath)
                .stream().map(u -> Paths.get(URI.create(u.toString())))
                .collect(Collectors.toList());
        AnalysisClasspath analysisClasspath = new AnalysisClasspath();
        analysisClasspath.classpath.addAll(paths);
        return analysisClasspath;
    }
}
