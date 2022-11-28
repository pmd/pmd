/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstExecSymbol<T extends ASTMethodOrConstructorDeclaration>
    extends AbstractAstTParamOwner<T>
    implements JExecutableSymbol {

    private final JClassSymbol owner;
    private final List<JFormalParamSymbol> formals;
    private final List<SymAnnot> declaredAnnotations;

    protected AbstractAstExecSymbol(T node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory);
        this.owner = owner;

        formals = CollectionUtil.map(
            node.getFormalParameters(),
            p -> new AstFormalParamSym(p.getVarId(), factory, this)
        );
        
        NodeStream<ASTAnnotation> annotStream = node.getDeclaredAnnotations();
        if (annotStream.isEmpty()) {
            declaredAnnotations = Collections.emptyList();
        } else {
            final List<SymAnnot> annotations = new ArrayList<>();
            annotStream.forEach(n -> annotations.add(new AstSymbolicAnnot(n)));
            declaredAnnotations = Collections.unmodifiableList(annotations);
        }
    }

    @Override
    public List<JFormalParamSymbol> getFormalParameters() {
        return formals;
    }


    @Override
    public List<JTypeMirror> getFormalParameterTypes(Substitution subst) {
        return CollectionUtil.map(getFormalParameters(), i -> i.getTypeMirror(subst));
    }

    @Override
    public List<JTypeMirror> getThrownExceptionTypes(Substitution subst) {
        return CollectionUtil.map(
            ASTList.orEmpty(node.getThrowsList()),
            t -> t.getTypeMirror().subst(subst)
        );
    }
    
    @Override
    public List<SymAnnot> getDeclaredAnnotations() {
        return declaredAnnotations;
    }
    

    @Override
    public @NonNull JClassSymbol getEnclosingClass() {
        return owner;
    }


    @Override
    public boolean isVarargs() {
        return node.isVarargs();
    }

    @Override
    public int getArity() {
        return formals.size();
    }


}
