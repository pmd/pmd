/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Receiver parameter. A receiver parameter is syntactically part of a
 * {@linkplain ASTFormalParameters formal parameter list}, though it does
 * not declare a variable or affect the arity of the method in any way.
 * Its only purpose is to annotate the type of the object on which the
 * method call is issued. It was introduced with Java 8.
 *
 * <p>For example:
 * <pre>
 * class Foo {
 *   abstract void foo(@Bar Foo this);
 * }
 * </pre>
 *
 * <p>Receiver parameters are only allowed on two types of declarations:
 * <ul>
 * <li>Instance method declarations of a class or interface (not annotation) type
 * <li>Constructor declaration of a non-static inner class. It then has
 * the type of the enclosing instance.
 * </ul>
 * In both cases it must be the first parameter of the formal parameter
 * list, and is entirely optional.
 *
 * <pre class="grammar">
 *
 * ReceiverParameter ::= {@link ASTClassOrInterfaceType ClassOrInterfaceType} (&lt;IDENTIFIER&gt; ".")? "this"
 *
 * </pre>
 */
public final class ASTReceiverParameter extends AbstractJavaNode {


    ASTReceiverParameter(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the type of the receiver parameter (eg {@code Foo} in {@code Foo this}.
     * In an instance method, that type must be the class or interface in which the method
     * is declared, and the name of the receiver parameter must be {@code this}.
     *
     * <p> In an inner class's constructor, the type of the receiver parameter
     * must be the class or interface which is the immediately enclosing type
     * declaration of the inner class, call it C, and the name of the parameter
     * must be {@code Identifier.this} where {@code Identifier} is the simple name of C.
     */
    @NonNull
    public ASTClassOrInterfaceType getReceiverType() {
        return (ASTClassOrInterfaceType) getChild(0);
    }

}
