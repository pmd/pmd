/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class GraphUtil {

    private GraphUtil() {

    }


    /**
     * Generate a DOT representation for a graph.
     *
     * @param vertices     Set of vertices
     * @param successorFun Function fetching successors
     * @param colorFun     Color of vertex box
     * @param labelFun     Vertex label
     * @param <V>          Type of vertex, must be usable as map key (equals/hash)
     */
    public static <V> String toDot(
        Collection<? extends V> vertices,
        Function<? super V, ? extends Collection<? extends V>> successorFun,
        Function<? super V, DotColor> colorFun,
        Function<? super V, String> labelFun
    ) {
        // generates a DOT representation of the lattice
        // Visualize eg at http://webgraphviz.com/
        StringBuilder sb = new StringBuilder("strict digraph {\n");
        Map<V, String> ids = new HashMap<>();
        int i = 0;
        for (V node : vertices) {
            String id = "n" + i++;
            ids.put(node, id);
            sb.append(id)
              .append(" [ shape=box, color=")
              .append(colorFun.apply(node).toDot())
              .append(", label=\"")
              .append(escapeDotString(labelFun.apply(node)))
                .append("\" ];\n");
        }

        List<String> edges = new ArrayList<>();

        for (V node : vertices) {
            // edges
            String id = ids.get(node);
            for (V succ : successorFun.apply(node)) {
                String succId = ids.get(succ);
                edges.add(id + " -> " + succId + ";\n");
            }
        }

        edges.sort(Comparator.naturalOrder()); // for reproducibility in tests
        edges.forEach(sb::append);

        return sb.append('}').toString();
    }


    @NonNull
    private static String escapeDotString(String string) {
        return string.replaceAll("\\R", "\\\n")
                     .replaceAll("\"", "\\\"");
    }

    public enum DotColor {
        GREEN, BLACK;

        String toDot() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
