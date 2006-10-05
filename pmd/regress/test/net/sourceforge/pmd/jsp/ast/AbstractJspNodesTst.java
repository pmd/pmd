package test.net.sourceforge.pmd.jsp.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.jsp.ast.JspCharStream;
import net.sourceforge.pmd.jsp.ast.JspParser;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AbstractJspNodesTst extends TestCase {

    public void assertNumberOfNodes(Class clazz, String source, int number) {
        Set nodes = getNodes(clazz, source);
        assertEquals("Exactly " + number + " element(s) expected", number, nodes.size());
    }

    /**
     * Run the JSP parser on the source, and return the nodes of type clazz.
     *
     * @param clazz
     * @param source
     * @return Set 
     */
    public Set getNodes(Class clazz, String source) {
        JspParser parser = new JspParser(new JspCharStream(new StringReader(source)));
        Node rootNode = parser.CompilationUnit();
        Set nodes = new HashSet();
        addNodeAndSubnodes(rootNode, nodes, clazz);
        return nodes;
    }

    /**
     * Return a subset of allNodes, containing the items in allNodes
     * that are of the given type.
     *
     * @param clazz
     * @param allNodes
     * @return Set 
     */
    public Set getNodesOfType(Class clazz, Set allNodes) {
        Set result = new HashSet();
        for (Iterator i = allNodes.iterator(); i.hasNext();) {
            Object node = i.next();
            if (clazz.equals(node.getClass())) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Add the given node and its subnodes to the set of nodes. If clazz is not null, only
     * nodes of the given class are put in the set of nodes.
     *
     * @param node
     * @param nodex
     * @param clazz
     */
    private void addNodeAndSubnodes(Node node, Set nodes, Class clazz) {
        if (null != node) {
            if ((null == clazz) || (clazz.equals(node.getClass()))) {
                nodes.add(node);
            }
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            addNodeAndSubnodes(node.jjtGetChild(i), nodes, clazz);
        }
    }

}
