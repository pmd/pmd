/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.expression.VariableExpression;

public abstract class AbstractDmlStatement<T extends AstNode> extends AbstractApexNode<T> {
    protected AbstractDmlStatement(T node) {
        super(node);
    }

    public Optional<String> getRunAsMode() {
        try {
            Optional<VariableExpression> runAsMode = (Optional<VariableExpression>) FieldUtils.readField(node, "runAsModeVariable", true);
            return runAsMode.map(v -> v.getIdentifier().getValue());
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            // ignored
            return Optional.empty();
        }
    }
}
