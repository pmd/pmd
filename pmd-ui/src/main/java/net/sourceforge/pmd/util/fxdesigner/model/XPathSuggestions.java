/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;




public class XPathSuggestions {
    private List<String> xPathSuggestions = new ArrayList<>();
    private String language;
    private List<String> listOfSuggestions;


    public XPathSuggestions(String language) {
        this.language = language.replaceAll("[0-9]", "").replaceAll("//s", "").trim().toLowerCase();
        try {
            this.xPathSuggestions = createList(getClasses("net.sourceforge.pmd.lang."
                                                               + language.replaceAll("[0-9]", "").replaceAll("//s", "").trim().toLowerCase() + ".ast"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> getXPathSuggestions(String input) {
        List<String> resultsToDisplay = new ArrayList<>();
        for (String s: xPathSuggestions) {
            if (s.contains(input)) {
                resultsToDisplay.add(s);
            }
        }
        return resultsToDisplay;
    }

    private List<String> createList(Class[] classArray) {
        List<Class> fileNameList = Arrays.asList(classArray);

        for (Class c : fileNameList) {
            if (c.getSimpleName().startsWith("AST")) {
                xPathSuggestions.add(c.getSimpleName().substring("AST".length()));

            }
        }

        return xPathSuggestions;

    }

    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        ArrayList<Class> classes = new ArrayList<>();

        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes.toArray(new Class[0]);
    }


    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {

        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }



}
