/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowsList;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.internal.typeops.TypeOps;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstExecSymbol<T extends ASTMethodOrConstructorDeclaration>
    extends AbstractAstTParamOwner<T>
    implements JExecutableSymbol {

    private final JClassSymbol owner;
    private List<JFormalParamSymbol> formals;

    public AbstractAstExecSymbol(T node, AstSymFactory factory, JClassSymbol owner) {
        super(node, factory);
        this.owner = owner;
    }

    @Override
    public List<JFormalParamSymbol> getFormalParameters() {
        if (formals == null) {
            formals = CollectionUtil.map(node.getFormalParameters().iterator(),
                                         p -> new AstFormalParamSym(p.getVarId(), factory, this));
        }

        return formals;
    }

    @Override
    public List<JTypeMirror> getFormalParameterTypes(Substitution substitution) {
        return CollectionUtil.map(getFormalParameters(), i -> i.getTypeMirror(substitution));
    }

    @Override
    public List<JTypeMirror> getThrownExceptionTypes(Substitution substitution) {
        ASTThrowsList throwsList = node.getThrowsList();

        return throwsList == null ? Collections.emptyList()
                                  : CollectionUtil.map(throwsList.iterator(), t ->  TypeOps.subst(t.getTypeMirror(), substitution));
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
        return node.getArity();
    }


}
