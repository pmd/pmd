/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import net.sourceforge.pmd.lang.Language;


/**
 * Finds XPath node names by looking into the classpath
 * directory corresponding to the AST of a language. This
 * is ok for Java, Apex, etc. but not e.g. for XML.
 */
class AstPackageExplorer implements NodeNameFinder {
    private final List<String> availableNodeNames;


    AstPackageExplorer(Language language) {
        availableNodeNames =
            getClasses("net.sourceforge.pmd.lang."
                           + language.getTerseName() + ".ast")
                .filter(clazz -> clazz.getSimpleName().startsWith("AST"))
                .filter(clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                .map(m -> m.getSimpleName().substring("AST".length()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

    }


    @Override
    public List<String> getNodeNames() {
        return availableNodeNames;
    }


    /** Finds the classes in the given package by looking in the classpath directories. */
    private static Stream<Class<?>> getClasses(String packageName) {


        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;


        Enumeration<URL> resources;

        try {
            String path = packageName.replace('.', '/');
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            return Stream.empty();
        }

        final List<Class<?>> result = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            try {
                Files.walkFileTree(new File(resource.getFile()).toPath(),
                                   EnumSet.noneOf(FileVisitOption.class),
                                   1,
                                   getClassFileVisitor(packageName, result));

            } catch (IOException e) {
                // continue
                e.printStackTrace();
            }
        }

        return result.stream();
    }


    private static FileVisitor<Path> getClassFileVisitor(String packageName, List<Class<?>> accumulator) {
        return new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                final String extension = FilenameUtils.getExtension(file.toString());
                if ("class".equalsIgnoreCase(extension)) {
                    try {
                        accumulator.add(Class.forName(packageName + "." + FilenameUtils.getBaseName(file.getFileName().toString())));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };

    }


}
