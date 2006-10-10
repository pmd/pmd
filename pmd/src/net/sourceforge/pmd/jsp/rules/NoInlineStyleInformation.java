package net.sourceforge.pmd.jsp.rules;

import java.util.Set;

import net.sourceforge.pmd.jsp.ast.ASTAttribute;
import net.sourceforge.pmd.jsp.ast.ASTElement;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * This rule checks that no "style" elements (like <B>, <FONT>, ...) are used, and that no
 * "style" attributes (like "font", "size", "align") are used.
 *
 * @author pieter_van_raemdonck
 */
public class NoInlineStyleInformation extends AbstractJspRule {

    // These lists should probably be extended
	
    /**
     * List of HTML element-names that define style.
     */
    private static final Set STYLE_ELEMENT_NAMES = CollectionUtil.asSet(
    		new String[]{"B", "I", "FONT", "BASEFONT", "U", "CENTER"}
    		);

    /**
     * List of HTML element-names that can have attributes defining style.
     */
    private static final Set ELEMENT_NAMES_THAT_CAN_HAVE_STYLE_ATTRIBUTES = CollectionUtil.asSet(
    		new String[]{"P", "TABLE", "THEAD", "TBODY", "TFOOT", "TR", "TD", "COL", "COLGROUP"}
    		);

    /**
     * List of attributes that define style when they are attributes of HTML elements with
     * names in ELEMENT_NAMES_THAT_CAN_HAVE_STYLE_ATTRIBUTES.
     */
    private static final Set STYLE_ATTRIBUTES = CollectionUtil.asSet(
    		new String[]{"STYLE", "FONT", "SIZE", "COLOR", "FACE", "ALIGN", "VALIGN", "BGCOLOR"}
    		);
    
    public Object visit(ASTAttribute node, Object data) {
        if (isStyleAttribute(node)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    public Object visit(ASTElement node, Object data) {
        if (isStyleElement(node)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    /**
     * Checks whether the name of the elementNode argument is one of STYLE_ELEMENT_NAMES.
     *
     * @param elementNode
     * @return boolean
     */
    private boolean isStyleElement(ASTElement elementNode) {
        return STYLE_ELEMENT_NAMES.contains(elementNode.getName().toUpperCase());
    }

    /**
     * Checks whether the attributeNode argument is a style attribute of a HTML element
     * that can have style attributes.
     *
     * @param elementNode
     * @return boolean
     */
    private boolean isStyleAttribute(ASTAttribute attributeNode) {
        if (STYLE_ATTRIBUTES.contains(attributeNode.getName().toUpperCase())) {
            if (attributeNode.jjtGetParent() instanceof ASTElement) {
                ASTElement parent = (ASTElement) attributeNode.jjtGetParent();
                if (ELEMENT_NAMES_THAT_CAN_HAVE_STYLE_ATTRIBUTES.contains(parent
                        .getName().toUpperCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
