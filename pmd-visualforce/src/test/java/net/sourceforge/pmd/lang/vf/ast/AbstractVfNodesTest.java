/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractVfNodesTest {

    public <T extends VfNode> void assertNumberOfNodes(Class<T> clazz, String source, int number) {
        Set<T> nodes = getNodes(clazz, source);
        assertEquals("Exactly " + number + " element(s) expected", number, nodes.size());
    }

    /**
     * Run the JSP parser on the source, and return the nodes of type clazz.
     *
     * @param clazz
     * @param source
     * @return Set
     */
    public <T extends VfNode> Set<T> getNodes(Class<T> clazz, String source) {
        VfParser parser = new VfParser(new JavaCharStream(new StringReader(source)));
        Node rootNode = parser.CompilationUnit();
        Set<T> nodes = new HashSet<>();
        addNodeAndSubnodes(rootNode, nodes, clazz);
        return nodes;
    }

    /**
     * Return a subset of allNodes, containing the items in allNodes that are of
     * the given type.
     *
     * @param clazz
     * @param allNodes
     * @return Set
     */
    @SuppressWarnings("unchecked")
    public <T extends VfNode> Set<T> getNodesOfType(Class<T> clazz, Set<VfNode> allNodes) {
        Set<T> result = new HashSet<>();
        for (Node node : allNodes) {
            if (clazz.equals(node.getClass())) {
                result.add((T) node);
            }
        }
        return result;
    }

    /**
     * Add the given node and its subnodes to the set of nodes. If clazz is not
     * null, only nodes of the given class are put in the set of nodes.
     */
    @SuppressWarnings("unchecked")
    private <T extends VfNode> void addNodeAndSubnodes(Node node, Set<T> nodes, Class<T> clazz) {
        if (null != node) {
            if ((null == clazz) || (clazz.equals(node.getClass()))) {
                nodes.add((T) node);
            }
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                addNodeAndSubnodes(node.jjtGetChild(i), nodes, clazz);
            }
        }
    }

}
