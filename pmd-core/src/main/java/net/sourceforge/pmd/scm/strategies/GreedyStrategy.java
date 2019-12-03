/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * A simple interactive (in a sense, compiler-driven) greedy strategy that tries to cut off
 * different AST subtrees with depending nodes until it cannot make any step.
 */
public class GreedyStrategy extends AbstractMinimizationStrategy {
    public static class Configuration extends AbstractConfiguration {
        @Override
        public MinimizationStrategy createStrategy() {
            return new GreedyStrategy(this);
        }
    }

    public static final MinimizationStrategyConfigurationFactory FACTORY = new AbstractFactory("greedy") {
        @Override
        public MinimizationStrategyConfiguration createConfiguration() {
            return new Configuration();
        }
    };

    private GreedyStrategy(Configuration configuration) {
        super(configuration);
    }

    private final Map<Node, HashSet<Node>> directlyDependingNodes = new HashMap<>();
    private final Map<Node, Set<Node>> transitivelyDependingNodes = new HashMap<>();

    private void fetchDirectDependentsFromSubtree(Node node) {
        // process depending nodes
        if (!directlyDependingNodes.containsKey(node)) {
            directlyDependingNodes.put(node, new HashSet<Node>());
        }
        directlyDependingNodes.get(node).addAll(ops.getNodeInformationProvider().getDirectlyDependingNodes(node));

        // process dependencies
        Set<Node> dependencies = ops.getNodeInformationProvider().getDirectDependencies(node);
        for (Node dependency: dependencies) {
            if (!directlyDependingNodes.containsKey(dependency)) {
                directlyDependingNodes.put(dependency, new HashSet<Node>());
            }
            directlyDependingNodes.get(dependency).add(node);
        }

        // recurse
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            fetchDirectDependentsFromSubtree(node.jjtGetChild(i));
        }
    }

    /**
     * This method implements Depth-First Search.
     *
     * Vertex state is determined by the <code>transitivelyDependingNodes.get(node)</code>:
     * <ul>
     *     <li><code>null</code> means this vertex is being visited for the first time</li>
     *     <li>empty set means this vertex was visited before (and one should check its directly dependent vertices)</li>
     *     <li>non-empty set means this vertex is fully processed</li>
     * </ul>
     */
    private Set<Node> indirectlyDependentNodesFor(Node currentNode) {
        final Set<Node> oldValue = transitivelyDependingNodes.get(currentNode);
        if (oldValue == null) {
            // mark this node as entered
            transitivelyDependingNodes.put(currentNode, new HashSet<Node>());

            // create separate set for ongoing calculation, see vertex state
            final HashSet<Node> calculated = new HashSet<>();
            // recurse
            final HashSet<Node> directlyDepending = directlyDependingNodes.get(currentNode);
            for (Node dependingNode: directlyDepending) {
                calculated.addAll(indirectlyDependentNodesFor(dependingNode));
            }
            calculated.add(currentNode);

            // finally, put real result to map
            transitivelyDependingNodes.put(currentNode, Collections.unmodifiableSet(calculated));

            return calculated;
        } else {
            // in other two cases no need to do anything
            return oldValue;
        }
    }

    private void collectNodesToRemove(Set<Node> result, Node node) {
        result.addAll(indirectlyDependentNodesFor(node));
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            collectNodesToRemove(result, node.jjtGetChild(i));
        }
    }

    private int previousPosition = 0;
    private int positionCountdown;

    /**
     * Traverse the passed subtree until successfully removing something.
     *
     * @see net.sourceforge.pmd.scm.SourceCodeMinimizer.ContinueException
     */
    private void findNodeToRemove(Node currentNode) throws Exception {
        previousPosition += 1;
        positionCountdown -= 1;
        if (positionCountdown < 0) {
            Set<Node> toBeRemoved = new HashSet<>();
            collectNodesToRemove(toBeRemoved, currentNode);
            ops.tryRemoveNodes(toBeRemoved);
            // if exception was not thrown, then removal was not successful
        }

        for (int i = 0; i < currentNode.jjtGetNumChildren(); ++i) {
            findNodeToRemove(currentNode.jjtGetChild(i));
        }
    }

    @Override
    public void performSinglePass(Node currentRoot) throws Exception {
        positionCountdown = previousPosition;
        previousPosition = 0;
        directlyDependingNodes.clear();
        transitivelyDependingNodes.clear();
        fetchDirectDependentsFromSubtree(currentRoot);
        // cannot remove root
        for (int i = 0; i < currentRoot.jjtGetNumChildren(); ++i) {
            findNodeToRemove(currentRoot.jjtGetChild(i));
        }
        // If we are here, then fast restart logic failed.
        // Trying to restart from scratch...
        previousPosition = 0;
        positionCountdown = 0;
        for (int i = 0; i < currentRoot.jjtGetNumChildren(); ++i) {
            findNodeToRemove(currentRoot.jjtGetChild(i));
        }
    }
}
