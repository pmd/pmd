/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class XPathSuggestions {

    //G:\pmd\pmd-java\src\main\java\net\sourceforge\pmd\lang\java\ast\ASTMethodDeclaration.java


    private List<String> getFileList(final File folder) {

        List<String> fileNameList = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getFileList(fileEntry);
            } else {
                if (fileEntry.getName().contains("AST")) {
                    fileNameList.add(fileEntry.getName());
                }
            }
        }

        return fileNameList;
    }

    public List<String> getXpathSuggestions(List<String> fileNameList) {

        List<String> xPathSuggestions = new ArrayList<>();
        for (String s : fileNameList) {
            if (s.contains("AST")) {
                s.replace("AST", "");
                s.replace("java", "");
                xPathSuggestions.add(s);
            }
        }

        return xPathSuggestions;
    }


    public static void main(String[] args) {
        final File folder = new File("G:\\pmd\\pmd-java\\src\\main\\java\\net\\sourceforge\\pmd\\lang\\java\\ast\\");
        XPathSuggestions suggestions = new XPathSuggestions();
        System.out.println(suggestions.getFileList(folder));
        System.out.println(suggestions.getXpathSuggestions(suggestions.getFileList(folder)));

    }

}
