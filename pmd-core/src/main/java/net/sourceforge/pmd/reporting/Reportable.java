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
 */
public interface Reportable {

    /**
     * Returns the location at which this element should be reported.
     *
     * <p>Use this instead of {@link Node#getBeginColumn()}/{@link Node#getBeginLine()}, etc.
     */
    FileLocation getReportLocation();

    /**
     * Return the nearest node in the tree that encloses this reportable.
     * This node is used to determine suppressions that apply to the violation.
     */
    default @NonNull Node getSuppressionNode(AstInfo<?> astInfo) {
        FileLocation loc = getReportLocation();
        int startOffset = astInfo.getTextDocument().offsetAtLineColumn(loc.getStartPos());

        Optional<Node> foundNode = NodeFindingUtil.findNodeAt(astInfo.getRootNode(), startOffset);
        // default to the root node
        return foundNode.orElse(astInfo.getRootNode());

    }
}
