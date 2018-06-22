/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

public class FormalComment extends Comment {

    public FormalComment(Token t) {
        super(t);

        if (t.image.trim().startsWith("/**")) {
            findJavadocs(t.image);
        }
    }

    @Override
    public String getXPathNodeName() {
        return "FormalComment";
    }

    private void findJavadocs(String commentText) {
        Collection<JavadocElement> kids = new ArrayList<>();

        Map<String, Integer> tags = CommentUtil.javadocTagsIn(commentText);
        for (Map.Entry<String, Integer> entry : tags.entrySet()) {
            JavadocTag tag = JavadocTag.tagFor(entry.getKey());
            if (tag == null) {
                continue;
            }
            kids.add(new JavadocElement(getBeginLine(), getBeginLine(),
                    // TODO valid?
                    entry.getValue() + 1, entry.getValue() + tag.label.length() + 1, tag));
        }

        children = kids.toArray(new Node[0]);
    }
}
