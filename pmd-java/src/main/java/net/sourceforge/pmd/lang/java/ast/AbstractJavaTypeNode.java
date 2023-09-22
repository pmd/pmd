/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypingContext;
import net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver;
import net.sourceforge.pmd.util.AssertionUtil;

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

    void forceTypeResolution() {
        getTypeMirror();
    }

    <T> T assertNonNullAfterTypeRes(T value) {
        assert value != null : "Something went wrong after type resolution of " + this;
        return value;
    }

    @Override
    public @NonNull JTypeMirror getTypeMirror() {
        return getTypeMirror(TypingContext.DEFAULT);
    }

    @Override
    public @NonNull JTypeMirror getTypeMirror(TypingContext context) {
        if (context.isEmpty() && typeMirror != null) {
            return typeMirror;
        }

        LazyTypeResolver resolver = getRoot().getLazyTypeResolver();
        JTypeMirror result;
        try {
            result = this.acceptVisitor(resolver, context);
            assert result != null : "LazyTypeResolver returned null";
        } catch (RuntimeException e) {
            throw AssertionUtil.contexted(e).addContextValue("Resolving type of", this);
        } catch (AssertionError e) {
            throw AssertionUtil.contexted(e).addContextValue("Resolving type of", this);
        }

        if (context.isEmpty() && typeMirror == null) {
            typeMirror = result; // cache it
        }
        return result;
    }

    JTypeMirror getTypeMirrorInternal() {
        return typeMirror;
    }

    void setTypeMirror(JTypeMirror mirror) {
        typeMirror = mirror;
    }


}
