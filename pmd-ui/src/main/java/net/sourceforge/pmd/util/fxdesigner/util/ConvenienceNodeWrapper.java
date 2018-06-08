/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.Collection;

import org.fxmisc.richtext.model.StyledDocument;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.5.0
 */
public interface ConvenienceNodeWrapper {


    StyledDocument<Collection<String>, String, Collection<String>> getNodeRichText();


    Node getNode();


    String getNodeText();

}
