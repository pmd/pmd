/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.qname;

import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;


/**
 * Specializes JavaQualifiedName for operations.
 *
 * @author Clément Fournier
 * @since 6.1.0
 *
 * @deprecated See {@link JavaQualifiedName}
 */
@Deprecated
public final class JavaOperationQualifiedName extends JavaQualifiedName {


    private final JavaTypeQualifiedName parent;
    private final String operation;
    private final boolean isLambda;


    JavaOperationQualifiedName(JavaTypeQualifiedName parent, String operation, boolean isLambda) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(parent);

        this.parent = parent;
        this.operation = operation;
        this.isLambda = isLambda;
    }


    @Override
    public JavaTypeQualifiedName getClassName() {
        return parent;
    }


    @Override
    public boolean isOperation() {
        return true;
    }


    @Override
    public boolean isClass() {
        return false;
    }


    /**
     * Returns true if this qualified name identifies a lambda expression.
     */
    public boolean isLambda() {
        return isLambda;
    }


    /**
     * Returns the operation specific part of the name. It
     * identifies an operation in its namespace.
     *
     * @return The operation string.
     */
    @Override
    public String getOperation() {
        return operation;
    }



    @Override
    protected boolean structurallyEquals(JavaQualifiedName qname) {
        JavaOperationQualifiedName that = (JavaOperationQualifiedName) qname;
        return isLambda == that.isLambda
                && this.operation.equals(that.operation)
                && this.parent.equals(that.parent);
    }


    @Override
    protected int buildHashCode() {
        return parent.hashCode() * 31 + Objects.hash(isLambda, operation);
    }

    @Override
    protected String buildToString() {
        return parent.toString() + "#" + operation;
    }
}

