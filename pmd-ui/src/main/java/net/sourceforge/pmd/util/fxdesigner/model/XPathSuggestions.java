/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class XPathSuggestions {
    private List<String> xPathSuggestions = new ArrayList<>();
    final private File folder ;

    public XPathSuggestions(File folder) {
        this.folder = folder;
        xPathSuggestions = evaluateXpathSuggestions(getFileList(folder));
    }



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

    private List<String> evaluateXpathSuggestions(List<String> fileNameList) {
        for (String s : fileNameList) {
                xPathSuggestions.add(s.replace("AST", "").replace(".java",""));
        }
        return xPathSuggestions;
    }

    public List<String> getXPathSuggestions() {
        return xPathSuggestions;
    }


//    public static void main(String[] args) {
//        final
//        XPathSuggestions suggestions = new XPathSuggestions();
//        System.out.println(suggestions.getFileList(folder));
//        System.out.println(suggestions.getXpathSuggestions(suggestions.getFileList(folder)));
//    }

}
