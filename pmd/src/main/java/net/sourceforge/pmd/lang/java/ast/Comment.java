/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.javadoc.JavadocTag;

public abstract class Comment extends AbstractNode {

    protected Comment(Token t) {
    	super(-1, t.beginLine, t.endLine, t.beginColumn, t.endColumn);

        setImage(t.image);
        if (t.image.startsWith("/**")) {
            findJavadocs(t.image);
        }
    }

    public String toString() {
    	return getImage();
    }

    private void findJavadocs(String commentText) {

    	Collection<JavadocElement> kids = new ArrayList<JavadocElement>();

    	Map<String, Integer> tags = CommentUtil.javadocTagsIn(commentText);
    	for (Map.Entry<String, Integer> entry : tags.entrySet()) {
    		JavadocTag tag = JavadocTag.tagFor(entry.getKey());
    		if (tag == null) continue;
    		kids.add(
    			new JavadocElement(
    				getBeginLine(), getBeginLine(),	// TODO valid?
    				entry.getValue() + 1, entry.getValue() + tag.label.length() + 1 ,tag
    				)
    			);
    	}

    	children = kids.toArray(new Node[kids.size()]);
    }

}
