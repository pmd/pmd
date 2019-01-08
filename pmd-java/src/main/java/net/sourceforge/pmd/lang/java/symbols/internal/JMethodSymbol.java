/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


/**
 * Reference to a method.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JMethodSymbol
    extends JAccessibleDeclarationSymbol<ASTMethodDeclaration>
    implements JTypeParameterOwnerSymbol, JMaybeStaticSymbol, JMaybeFinalSymbol {

    private final boolean isDefault;
    private final Lazy<List<JLocalVariableSymbol>> myFormalParameters;
    private final Lazy<List<JTypeParameterSymbol>> myTypeParameters;

    /**
     * Constructor for methods found through reflection.
     *
     * @param method Method for which to create a reference
     */
    public JMethodSymbol(Method method) {
        super(method.getModifiers(), method.getName(), method.getDeclaringClass());
        this.isDefault = method.isDefault();
        this.myFormalParameters = new Lazy<>(
            () -> Arrays.stream(method.getParameters())
                        .map(JLocalVariableSymbol::new)
                        .collect(Collectors.toList()));

        this.myTypeParameters = new Lazy<>(
            () -> Arrays.stream(method.getTypeParameters())
                        .map(tv -> new JTypeParameterSymbol(this, tv))
                        .collect(Collectors.toList()));
    }


    /**
     * Constructor using the AST node.
     *
     * @param node Node representing the method declaration
     */
    public JMethodSymbol(ASTMethodDeclaration node) {
        super(Objects.requireNonNull(node), getModifiers(node), node.getMethodName());
        this.isDefault = node.isDefault();
        this.myFormalParameters =
            new Lazy<>(
                () -> node.getFormalParameters()
                          .asList()
                          .stream()
                          .map(ASTFormalParameter::getVariableDeclaratorId)
                          .map(JLocalVariableSymbol::new)
                          .collect(Collectors.toList()));

        this.myTypeParameters =
            new Lazy<>(
                () -> node.getTypeParameters().stream()
                          .map(tp -> new JTypeParameterSymbol(this, tp))
                          .collect(Collectors.toList())
            );
    }

    boolean isSynchronized() {
        return Modifier.isSynchronized(myModifiers);
    }


    boolean isNative() {
        return Modifier.isNative(myModifiers);
    }


    boolean isDefault() {
        return isDefault;
    }


    boolean isStrict() {
        return Modifier.isStrict(myModifiers);
    }


    boolean isAbstract() {
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

    boolean isVarargs() {
        return (myModifiers & Modifier.TRANSIENT) != 0;
    }


    @Override
    public List<JTypeParameterSymbol> getTypeParameters() {
        return myTypeParameters.getValue();
    }


    public List<JLocalVariableSymbol> getFormalParameters() {
        return myFormalParameters.getValue();
    }


    private static int getModifiers(ASTMethodDeclaration declaration) {
        int i = accessNodeToModifiers(declaration);

        if (declaration.isVarargs()) {
            i |= Modifier.TRANSIENT;
        }
        return i;
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
        JMethodSymbol that = (JMethodSymbol) o;
        return isDefault == that.isDefault
            && Objects.equals(myFormalParameters, that.myFormalParameters);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isDefault);
    }
}
