/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

public class FormalComment extends Comment {

    private static final Pattern JAVADOC_TAG = Pattern.compile("@([A-Za-z0-9]+)");

    public FormalComment(Token t) {
        super(t);

        findJavadocs();
    }

    @Override
    public String getXPathNodeName() {
        return "FormalComment";
    }

    private void findJavadocs() {
        Collection<JavadocElement> kids = new ArrayList<>();

        Matcher javadocTagMatcher = JAVADOC_TAG.matcher(getFilteredComment());
        while (javadocTagMatcher.find()) {
            JavadocTag tag = JavadocTag.tagFor(javadocTagMatcher.group(1));
            int tagStartIndex = javadocTagMatcher.start(1);
            if (tag != null) {
                kids.add(new JavadocElement(getBeginLine(), getBeginLine(),
                        // TODO valid?
                        tagStartIndex, tagStartIndex + tag.label.length() + 1, tag));
            }
        }

        children = kids.toArray(new Node[0]);
    }
}
