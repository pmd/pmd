/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValueArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 *
 */
public class AstSymbolicAnnot implements SymbolicValue.SymAnnot {

    private final ASTAnnotation node;
    private final Set<String> attrNames;

    public AstSymbolicAnnot(ASTAnnotation node) {
        this.node = node;
        attrNames = node.getMembers().collect(Collectors.mapping(ASTMemberValuePair::getName, Collectors.toSet()));
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String attrName) {
        return ofNode(node.getAttribute(attrName));
    }

    @Override
    public Set<String> getAttributeNames() {
        return attrNames;
    }

    @Override
    public RetentionPolicy getRetention() {
        RetentionPolicy retention = node.getTypeMirror().getSymbol().getAnnotationRetention();
        if (retention == null) {
            retention = RetentionPolicy.CLASS;
        }
        return retention;
    }

    @Override
    public boolean isOfType(String binaryName) {
        return myBinaryName().equals(binaryName);
    }

    private @NonNull String myBinaryName() {
        return node.getTypeMirror().getSymbol().getBinaryName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymAnnot)) {
            return false;
        }
        SymAnnot that = (SymAnnot) o;
        if (!that.isOfType(myBinaryName())) {
            return false;
        } else if (!getAttributeNames().equals(that.getAttributeNames())) {
            return false;
        }

        for (String attrName : getAttributeNames()) {
            if (!Objects.equals(getAttribute(attrName), that.getAttribute(attrName))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myBinaryName(), getAttributeNames());
    }

    static SymbolicValue ofNode(ASTMemberValue valueNode) {
        if (valueNode == null) {
            return null;
        }

        { // note: this returns null for enums, annotations, and classes
            Object constValue = valueNode.getConstValue();
            if (constValue != null) {
                return SymbolicValue.of(valueNode.getTypeSystem(), constValue);
            }
        }

        if (valueNode instanceof ASTMemberValueArrayInitializer) {
            // array
            List<SymbolicValue> elements = new ArrayList<>(valueNode.getNumChildren());
            for (ASTMemberValue elt : ((ASTMemberValueArrayInitializer) valueNode)) {
                SymbolicValue symElt = ofNode(elt);
                if (symElt == null) {
                    return null;
                }
                elements.add(symElt);
            }
            return SymArray.forElements(elements);
        } else if (valueNode instanceof ASTClassLiteral) {
            // class
            JTypeDeclSymbol symbol = ((ASTClassLiteral) valueNode).getTypeNode().getTypeMirror().getSymbol();
            if (symbol instanceof JClassSymbol) {
                return SymClass.ofBinaryName(symbol.getTypeSystem(), ((JClassSymbol) symbol).getBinaryName());
            }
        } else if (valueNode instanceof ASTNamedReferenceExpr) {
            // enum constants
            ASTNamedReferenceExpr refExpr = (ASTNamedReferenceExpr) valueNode;
            JTypeMirror t = refExpr.getTypeMirror();
            if (t instanceof JClassType && ((JClassType) t).getSymbol().isEnum()) {
                return SymEnum.fromBinaryName(t.getTypeSystem(),
                                              ((JClassType) t).getSymbol().getBinaryName(),
                                              refExpr.getName());
            }
        }
        return null;
    }
}
