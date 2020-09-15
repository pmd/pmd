/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.lang.annotation.RetentionPolicy;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;

/**
 *
 */
public class AstSymbolicAnnot implements SymbolicValue.SymAnnot {

    private final ASTAnnotation node;

    public AstSymbolicAnnot(ASTAnnotation node) {
        this.node = node;
    }

    @Override
    public boolean valueEquals(Object o) {
        return false;
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String attrName) {
        return ofNode(node.getAttribute(attrName));
    }

    @Override
    public Set<String> getAttributeNames() {
        return node.getMembers().collect(Collectors.mapping(ASTMemberValuePair::getName, Collectors.toSet()));
    }

    @Override
    public RetentionPolicy getRetention() {
        return Optional.ofNullable(node.getTypeNode().getTypeMirror().getSymbol())
                       .filter(sym -> sym instanceof JClassSymbol)
                       .map(sym -> ((JClassSymbol) sym).getAnnotationRetention())
                       .orElse(RetentionPolicy.CLASS);
    }

    @Override
    public boolean isOfType(String binaryName) {
        return false;
    }

    static SymbolicValue ofNode(ASTMemberValue valueNode) {
        if (valueNode == null) {
            return null;
        }

        // note: this returns null for enums & annotations
        Object constValue = valueNode.getConstValue();
        return SymbolicValue.of(valueNode.getTypeSystem(), constValue);
    }
}
