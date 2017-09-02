/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.Paragraph;
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpansBuilder;

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


    /**
     * Styles the text corresponding to the node on the textarea with style class {@literal .node-highlight}.
     *
     * @param codeArea CodeArea
     * @param node     Node to highlight
     */
    public static void highlightNode(CodeArea codeArea, Node node) {


        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lengthBefore = lengthUntil(codeArea, node.getBeginLine(), node.getBeginColumn());
        int lengthHighlighted = lengthBetween(codeArea,
                                              node.getBeginLine(), node.getBeginColumn(),
                                              node.getEndLine(), node.getEndColumn());
        int lengthAfter = codeArea.getLength() - lengthBefore - lengthHighlighted;

        spansBuilder.add(new StyleSpan<>(Collections.emptyList(), lengthBefore));

        spansBuilder.add(new StyleSpan<>(Collections.singleton("node-highlight"), lengthHighlighted));

        spansBuilder.add(new StyleSpan<>(Collections.emptyList(), lengthAfter >= 0 ? lengthAfter : 0));

        codeArea.setStyleSpans(0, spansBuilder.create());

    }


    /** Length in characters before the specified position. */
    private static int lengthUntil(CodeArea codeArea, int line, int column) {
        List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
        int length = 0;
        for (int i = 0; i < line - 1; i++) {
            length += paragraphs.get(i).length() + 1;
        }
        return length + column - 1;
    }


    /** Length in characters between the two positions. */
    private static int lengthBetween(CodeArea codeArea, int l1, int c1, int l2, int c2) {
        int par1 = l1 - 1;
        int par2 = l2 - 1;
        if (l1 == l2) {
            return c2 - c1 + 1;
        } else if (l1 < l2) {
            List<Paragraph<Collection<String>>> paragraphs = codeArea.getParagraphs();
            int length = paragraphs.get(par1).length() - c1 + 1;
            for (int i = par1 + 1; i < par2; i++) {
                length += paragraphs.get(i).length() + 1;
            }
            return length + c2 + 1;
        } else {
            throw new IllegalArgumentException();
        }
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
