/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            getClassesInPackage("net.sourceforge.pmd.lang." + language.getTerseName() + ".ast")
                .filter(clazz -> clazz.getSimpleName().startsWith("AST"))
                .filter(clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                .map(m -> m.getSimpleName().substring("AST".length()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

    }


    @Override
    public List<String> getNodeNames() {
        return availableNodeNames;
    }

    // TODO move to some global Util


    /** Finds the classes in the given package by looking in the classpath directories. */
    private static Stream<Class<?>> getClassesInPackage(String packageName) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        Stream<URL> resources;

        try {
            String path = packageName.replace('.', '/');
            resources = enumerationAsStream(classLoader.getResources(path));
        } catch (IOException e) {
            return Stream.empty();
        }

        return resources.flatMap(resource -> {
            try {
                return getClasses(resource, packageName);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                return Stream.empty();
            }
        });
    }


    /** Maps paths to classes. */
    private static Stream<Class<?>> getClasses(URL url, String packageName) throws IOException, URISyntaxException {
        return getPathsInDir(url)
            .stream()
            .filter(path -> "class".equalsIgnoreCase(FilenameUtils.getExtension(path.toString())))
            .<Class<?>>map(path -> {
                try {
                    return Class.forName(packageName + "." + FilenameUtils.getBaseName(path.getFileName().toString()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull);
    }


    private static List<Path> getPathsInDir(URL url) throws URISyntaxException, IOException {

        URI uri = url.toURI();

        if ("jar".equals(uri.getScheme())) {
            // we have to do this to look inside a jar
            try (FileSystem fs = getFileSystem(uri)) {
                // we have to cut out the path to the jar + '!'
                // to get a path that's relative to the root of the jar filesystem
                // This is equivalent to a packageName.replace('.', '/') but more reusable
                String schemeSpecific = uri.getSchemeSpecificPart();
                String fsRelativePath = schemeSpecific.substring(schemeSpecific.indexOf('!') + 1);
                return Files.walk(fs.getPath(fsRelativePath), 1)
                            .collect(Collectors.toList()); // buffer everything, before closing the filesystem

            }
        } else {
            try (Stream<Path> paths = Files.walk(new File(url.getFile()).toPath(), 1)) {
                return paths.collect(Collectors.toList()); // buffer everything, before closing the original stream
            }
        }
    }


    private static FileSystem getFileSystem(URI uri) throws IOException {

        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, Collections.<String, String>emptyMap());
        }
    }


    // TODO move to IteratorUtil
    private static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                new Iterator<T>() {
                    @Override
                    public T next() {
                        return e.nextElement();
                    }


                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }
                },
                Spliterator.ORDERED), false);
    }


}
