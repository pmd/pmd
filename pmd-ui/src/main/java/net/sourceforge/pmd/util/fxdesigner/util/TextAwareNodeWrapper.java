/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.Collection;

import org.fxmisc.richtext.model.StyledDocument;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * Node wrapper providing convenience methods to get the text representing the node
 * from the code area.
 *
 * @author Clément Fournier
 * @since 6.5.0
 */
public interface TextAwareNodeWrapper {

    /**
     * Gets the rich text corresponding to the node in the code area.
     */
    StyledDocument<Collection<String>, String, Collection<String>> getNodeRichText();


    /**
     * Gets the text corresponding to the node in the code area.
     */
    String getNodeText();


    /**
     * Gets the underlying node.
     */
    Node getNode();


}
