/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.internal.NodeFindingUtil;
import net.sourceforge.pmd.lang.document.FileLocation;

/**
 * Interface implemented by those objects that can be the target of
 * a {@link RuleViolation}. {@link Node}s and {@link GenericToken tokens}
 * implement this interface.
 *
 * <p>This is meant to be reported with {@link RuleContext}'s API.
 *
 * @see RuleContext#at(Reportable)
 */
public interface Reportable {

    /**
     * Returns the location at which this element should be reported.
     *
     * <p>Use this instead of {@link Node#getBeginColumn()}/{@link Node#getBeginLine()}, etc.
     *
     * @return The location where this element should be reported
     */
    FileLocation getReportLocation();

    /**
     * Return the nearest node in the tree that encloses the violation.
     * This node is used to determine suppressions that apply to the violation.
     * For instance in {@link AbstractAnnotationSuppressor}, the ancestors
     * of that node are explored to find ones that have a {@code @SuppressWarnings}
     * annotation. When reporting objects that are not nodes (such as tokens),
     * a specific node that covers the token's region is searched from the tree.
     *
     * @param astInfo The AstInfo for the source file where the violation was reported.
     * @implNote The default implementation finds a node by searching
     * through descendants and is relatively expensive.
     */
    default @NonNull Node getSuppressionNode(AstInfo<?> astInfo) {
        FileLocation loc = getReportLocation();
        int startOffset = astInfo.getTextDocument().offsetAtLineColumn(loc.getStartPos());

        Optional<Node> foundNode = NodeFindingUtil.findNodeAt(astInfo.getRootNode(), startOffset);
        // default to the root node
        return foundNode.orElse(astInfo.getRootNode());

    }
}
