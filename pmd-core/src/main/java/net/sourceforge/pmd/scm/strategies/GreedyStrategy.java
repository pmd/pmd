/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;

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
    private final Map<Node, HashSet<Node>> transitivelyDependingNodes = new HashMap<>();

    private void startFetchingDirect(Node currentNode) {
        while (currentNode.jjtGetParent() != null) {
            currentNode = currentNode.jjtGetParent();
        }
        fetchDirectDependentsFrom(currentNode);
    }

    private void fetchDirectDependentsFrom(Node currentNode) {
        // process depending nodes
        if (!directlyDependingNodes.containsKey(currentNode)) {
            directlyDependingNodes.put(currentNode, new HashSet<Node>());
        }
        directlyDependingNodes.get(currentNode).addAll(ops.getLanguage().getDirectlyDependingNodes(currentNode));

        // process dependencies
        Set<Node> dependencies = ops.getLanguage().getDirectlyDependencies(currentNode);
        for (Node dependency: dependencies) {
            if (!directlyDependingNodes.containsKey(dependency)) {
                directlyDependingNodes.put(dependency, new HashSet<Node>());
            }
            directlyDependingNodes.get(dependency).add(currentNode);
        }

        // recurse
        for (int i = 0; i < currentNode.jjtGetNumChildren(); ++i) {
            fetchDirectDependentsFrom(currentNode.jjtGetChild(i));
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
    private Set<Node> ensuringIndirectlyDependentNodesCalculatedFor(Node currentNode) {
        final Set<Node> oldValue = transitivelyDependingNodes.get(currentNode);
        if (oldValue == null) {
            transitivelyDependingNodes.put(currentNode, new HashSet<Node>());
            final HashSet<Node> calculated = new HashSet<>();
            final HashSet<Node> directlyDepending = directlyDependingNodes.get(currentNode);
            for (Node dependingNode: directlyDepending) {
                calculated.addAll(ensuringIndirectlyDependentNodesCalculatedFor(dependingNode));
            }
            calculated.add(currentNode);
            // not updating in-place, see vertex state description
            transitivelyDependingNodes.put(currentNode, calculated);
            return calculated;
        } else {
            // in other two cases no need to do anything
            return oldValue;
        }
    }

    private Set<Node> getNodesToRemove(Node node) {
        // first, check if information is already fetched
        if (!transitivelyDependingNodes.isEmpty()) {
            return ensuringIndirectlyDependentNodesCalculatedFor(node);
        }
        // then, check if node has definitely empty set of directly depending nodes
        Set<Node> directlyDependingNodes = ops.getLanguage().getDirectlyDependingNodes(node);
        if (directlyDependingNodes != null && directlyDependingNodes.isEmpty()) {
            HashSet<Node> result = new HashSet<>();
            result.add(node);
            return result;
        }
        // finally, perform full calculation
        startFetchingDirect(node);
        return ensuringIndirectlyDependentNodesCalculatedFor(node);
    }

    private int previousPosition = 0;
    private int positionCountdown;

    private void findNodeToRemove(Node currentNode) throws Exception {
        previousPosition += 1;
        positionCountdown -= 1;
        if (positionCountdown < 0) {
            ops.tryRemoveNodes(getNodesToRemove(currentNode));
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
