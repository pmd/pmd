/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.fxdesigner;

import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.TreeNode;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

/**
 * @author Clément Fournier
 */
class ASTTreeNode implements TreeNode {

    private static DesignerController controller;

    private Node node;
    private ASTTreeNode parent;
    private ASTTreeNode[] kids;


    ASTTreeNode(Node theNode) {
        node = theNode;

        Node parent = node.jjtGetParent();
        if (parent != null) {
            this.parent = new ASTTreeNode(parent);
        }
    }


    private ASTTreeNode(ASTTreeNode parent, Node theNode) {
        node = theNode;
        this.parent = parent;
    }


    @Override
    public int getChildCount() {
        return node.jjtGetNumChildren();
    }


    @Override
    public boolean getAllowsChildren() {
        return false;
    }


    @Override
    public boolean isLeaf() {
        return node.jjtGetNumChildren() == 0;
    }


    @Override
    public TreeNode getParent() {
        return parent;
    }


    public Scope getScope() {
        if (node instanceof ScopedNode) {
            return ((ScopedNode) node).getScope();
        }
        return null;
    }


    @Override
    public Enumeration<TreeNode> children() {

        if (getChildCount() > 0) {
            getChildAt(0); // force it to build kids
        }

        Enumeration<TreeNode> e = new Enumeration<TreeNode>() {
            int i = 0;


            @Override
            public boolean hasMoreElements() {
                return kids != null && i < kids.length;
            }


            @Override
            public ASTTreeNode nextElement() {
                return kids[i++];
            }
        };
        return e;
    }


    @Override
    public TreeNode getChildAt(int childIndex) {

        if (kids == null) {
            kids = new ASTTreeNode[node.jjtGetNumChildren()];
            for (int i = 0; i < kids.length; i++) {
                kids[i] = new ASTTreeNode(this.parent, node.jjtGetChild(i));
            }
        }
        return kids[childIndex];
    }


    @Override
    public int getIndex(TreeNode node) {

        for (int i = 0; i < kids.length; i++) {
            if (kids[i] == node) {
                return i;
            }
        }
        return -1;
    }


    public String label() {
        LanguageVersionHandler languageVersionHandler = controller.getLanguageVersionHandler();
        StringWriter writer = new StringWriter();
        languageVersionHandler.getDumpFacade(writer, "", false).start(node);
        return writer.toString();
    }


    public String getToolTipText() {
        String tooltip = "Line: " + node.getBeginLine() + " Column: " + node.getBeginColumn();
        tooltip += " " + label();
        return tooltip;
    }


    public List<String> getAttributes() {
        List<String> result = new LinkedList<>();
        AttributeAxisIterator attributeAxisIterator = new AttributeAxisIterator(node);
        while (attributeAxisIterator.hasNext()) {
            Attribute attribute = attributeAxisIterator.next();
            result.add(attribute.getName() + "=" + attribute.getStringValue());
        }
        return result;
    }


    static void initialize(DesignerController ctrl) {
        controller = ctrl;
    }
}
