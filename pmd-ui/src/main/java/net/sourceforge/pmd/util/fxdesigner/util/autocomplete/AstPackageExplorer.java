/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.Language;


/**
 * Finds XPath node names by looking into the classpath
 * directory corresponding to the AST of a language. This
 * is ok for Java, Apex, etc. but not e.g. for XML.
 */
class AstPackageExplorer implements NodeNameFinder {
    private List<String> availableNodeNames = new ArrayList<>();


    AstPackageExplorer(Language language) {
        availableNodeNames =
            getClasses("net.sourceforge.pmd.lang."
                           + language.getTerseName() + ".ast")
                .filter(files -> files.getSimpleName().startsWith("AST"))
                .map(m -> m.getSimpleName().substring("AST".length()))
                .collect(Collectors.toList());

    }


    @Override
    public List<String> getNodeNames() {
        return availableNodeNames;
    }


    /** Finds the classes in the given package by looking in the classpath directories. */
    private static Stream<Class<?>> getClasses(String packageName) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        String path = packageName.replace('.', '/');

        Enumeration<URL> resources;

        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            return Stream.empty();
        }

        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        List<Class<?>> classes = new ArrayList<>();

        for (File directory : dirs) {
            addClasses(classes, directory, packageName);
        }

        return classes.stream();
    }


    private static void addClasses(List<Class<?>> classes, File directory, String packageName) {

        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - ".class".length())));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return;
    }


}
