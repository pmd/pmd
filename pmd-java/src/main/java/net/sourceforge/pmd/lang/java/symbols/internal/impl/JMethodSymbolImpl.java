/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.symbols.internal.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JTypeParameterSymbol;


public final class JMethodSymbolImpl
    extends JAccessibleDeclarationSymbolImpl<ASTMethodDeclaration>
    implements JMethodSymbol {

    private final boolean isDefault;
    private final Lazy<List<net.sourceforge.pmd.lang.java.symbols.internal.JLocalVariableSymbol>> myFormalParameters;
    private final Lazy<List<JTypeParameterSymbol>> myTypeParameters;


    /**
     * Constructor for methods found through reflection.
     *
     * @param method Method for which to create a reference
     */
    public JMethodSymbolImpl(Method method) {
        super(method.getModifiers(), method.getName(), method.getDeclaringClass());
        this.isDefault = method.isDefault();
        this.myFormalParameters = Lazy.lazy(
            () -> Arrays.stream(method.getParameters())
                        .map(JLocalVariableSymbolImpl::new)
                        .collect(Collectors.toList()));

        this.myTypeParameters = Lazy.lazy(
            () -> Arrays.stream(method.getTypeParameters())
                        .map(tv -> new JTypeParameterSymbolImpl(this, tv))
                        .collect(Collectors.toList()));
    }


    /**
     * Constructor using the AST node.
     *
     * @param node Node representing the method declaration
     */
    public JMethodSymbolImpl(ASTMethodDeclaration node) {
        super(Objects.requireNonNull(node), getModifiers(node), node.getMethodName());
        this.isDefault = node.isDefault();
        this.myFormalParameters =
            Lazy.lazy(
                () -> node.getFormalParameters()
                          .asList()
                          .stream()
                          .map(ASTFormalParameter::getVariableDeclaratorId)
                          .map(JLocalVariableSymbolImpl::new)
                          .collect(Collectors.toList()));

        this.myTypeParameters =
            Lazy.lazy(
                () -> node.getTypeParameters().stream()
                          .map(tp -> new JTypeParameterSymbolImpl(this, tp))
                          .collect(Collectors.toList())
            );
    }


    @Override
    public boolean isSynchronized() {
        return Modifier.isSynchronized(myModifiers);
    }


    @Override
    public boolean isNative() {
        return Modifier.isNative(myModifiers);
    }


    @Override
    public boolean isDefault() {
        return isDefault;
    }


    @Override
    public boolean isStrict() {
        return Modifier.isStrict(myModifiers);
    }


    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(myModifiers);
    }


    @Override
    public boolean isStatic() {
        return Modifier.isStatic(myModifiers);
    }


    @Override
    public boolean isFinal() {
        return Modifier.isFinal(myModifiers);
    }

    // Modifier.TRANSIENT is identical to the bit mask used for varargs
    // the reflect API uses that because they can never occur together I guess


    @Override
    public boolean isVarargs() {
        return (myModifiers & Modifier.TRANSIENT) != 0;
    }


    @Override
    public List<JTypeParameterSymbol> getTypeParameters() {
        return myTypeParameters.getValue();
    }


    @Override
    public List<net.sourceforge.pmd.lang.java.symbols.internal.JLocalVariableSymbol> getFormalParameters() {
        return myFormalParameters.getValue();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JMethodSymbolImpl that = (JMethodSymbolImpl) o;
        return isDefault == that.isDefault
            && Objects.equals(myFormalParameters, that.myFormalParameters);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isDefault);
    }


    private static int getModifiers(ASTMethodDeclaration declaration) {
        int i = accessNodeToModifiers(declaration);

        if (declaration.isVarargs()) {
            i |= Modifier.TRANSIENT;
        }
        return i;
    }
}
