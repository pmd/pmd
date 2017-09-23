/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.java.ast.TypeNode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerUtil {


    private static LanguageVersion[] supportedLanguageVersions;


    private DesignerUtil() {

    }


    /** Gets the XPath attributes of the node for display within a listview. */
    public static ObservableList<String> getAttributes(Node node) {
        ObservableList<String> result = FXCollections.observableArrayList();
        AttributeAxisIterator attributeAxisIterator = new AttributeAxisIterator(node);
        while (attributeAxisIterator.hasNext()) {
            Attribute attribute = attributeAxisIterator.next();
            result.add(attribute.getName() + " = "
                           + ((attribute.getValue() != null) ? attribute.getStringValue() : "null"));
        }

        if (node instanceof TypeNode) {
            result.add("typeof() = " + ((TypeNode) node).getType());
        }
        Collections.sort(result);
        return result;
    }


    public static LanguageVersion[] getSupportedLanguageVersions() {
        if (supportedLanguageVersions == null) {
            List<LanguageVersion> languageVersions = new ArrayList<>();
            for (LanguageVersion languageVersion : LanguageRegistry.findAllVersions()) {
                LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
                if (languageVersionHandler != null) {
                    Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
                    if (parser != null && parser.canParse()) {
                        languageVersions.add(languageVersion);
                    }
                }
            }
            supportedLanguageVersions = languageVersions.toArray(new LanguageVersion[languageVersions.size()]);
        }
        return supportedLanguageVersions;
    }
}
