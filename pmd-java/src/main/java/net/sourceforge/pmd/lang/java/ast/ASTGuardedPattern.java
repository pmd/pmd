/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A guarded pattern (JDK17 Preview). This can be found
 * in {@link ASTSwitchLabel}s.
 *
 * <pre class="grammar">
 *
 * GuardedPattern ::= {@linkplain ASTPattern} "&&" {@linkplain ASTConditionalAndExpression}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
*/
@Experimental
public final class ASTGuardedPattern extends AbstractJavaNode implements ASTPattern {

    ASTGuardedPattern(int id) {
        super(id);
    }

    ASTGuardedPattern(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

}
