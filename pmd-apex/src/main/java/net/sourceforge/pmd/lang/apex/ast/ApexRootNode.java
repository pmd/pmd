/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.apache.commons.lang3.StringUtils;

import apex.jorje.semantic.ast.AstNode;
import net.sourceforge.pmd.lang.ast.RootNode;

public abstract class ApexRootNode<T extends AstNode> extends AbstractApexNode<T> implements RootNode {
    public ApexRootNode(T node) {
        super(node);
    }

    // For top level classes, the begin is the first character of the source
    // end the end is the last
    @Override
    public int getBeginLine() {
        return 1;
    }
    @Override
    public int getBeginColumn() {
        return 1;
    }
    @Override
    public int getEndLine() {
        String code = node.getDefiningType().getCodeUnitDetails().getSource().getBody();
        int lineCount = StringUtils.countMatches(code, "\n");
        if (!code.endsWith("\n") && !code.endsWith("\r\n")) {
            lineCount += 1;
        }
        return lineCount;
    }
    @Override
    public int getEndColumn() {
        String code = node.getDefiningType().getCodeUnitDetails().getSource().getBody();
        return code.length() - code.lastIndexOf('\n') + 1;
    }
}
