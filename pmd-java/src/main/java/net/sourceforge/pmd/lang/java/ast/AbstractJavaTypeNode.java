/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * An extension of the SimpleJavaNode which implements the TypeNode interface.
 *
 * @see AbstractJavaNode
 * @see TypeNode
 */
abstract class AbstractJavaTypeNode extends AbstractJavaNode implements TypeNode {

    private JTypeMirror typeMirror;

    AbstractJavaTypeNode(int i) {
        super(i);
    }

    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        if (typeMirror == null) {
            try {
                if (this instanceof ASTLambdaExpression) {
                    // To avoid reentry, harmful for lambdas (there's a special
                    // variable resolution strategy for lambda parameters)
                    // for branching polys (conditional, switch) it's ok to reenter
                    typeMirror = getTypeSystem().ERROR_TYPE;
                }
                LazyTypeResolver resolver = getRoot().getLazyTypeResolver();
                typeMirror = this.acceptVisitor(resolver, null);
                assert typeMirror != null : "LazyTypeResolver returned null";
            } catch (Exception | AssertionError e) {
                // this will add every type in the chain
                throw addContextValue(e, "Resolving type of", this);
            }
        }
        return typeMirror;
    }

    private static ContextedRuntimeException addContextValue(Throwable e, String label, Object value) {
        return e instanceof ContextedRuntimeException ? ((ContextedRuntimeException) e).addContextValue(label, value)
                                                      : new ContextedRuntimeException(e).addContextValue(label, value);
    }

    JTypeMirror getTypeMirrorInternal() {
        return typeMirror;
    }

    void setTypeMirror(JTypeMirror mirror) {
        typeMirror = mirror;
    }


}
