/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents either a {@code case} or {@code default} label inside
 * a {@linkplain ASTSwitchStatement switch statement}.
 *
 * <pre>
 *
 * SwitchLabel ::=  "case" {@linkplain ASTExpression Expression} ":"
 *                | "case" "null [ "," "default" ] ":"
 *                | "case" ( {@linkplain ASTTypePattern TypePattern} | {@linkplain ASTRecordPattern RecordPattern} ) ":"
 *                | "default" ":"
 *
 * </pre>
 *
 * <p>Note: case null and the case patterns are a Java 19 Preview and Java 20 Preview language feature</p>
 *
 * @see <a href="https://openjdk.org/jeps/433">JEP 433: Pattern Matching for switch (Fourth Preview)</a>
 * @see <a href="https://openjdk.org/jeps/432">JEP 432: Record Patterns (Second Preview)</a>
 */
public class ASTSwitchLabel extends AbstractJavaNode {

    private boolean isDefault;


    @InternalApi
    @Deprecated
    public ASTSwitchLabel(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTSwitchLabel(JavaParser p, int id) {
        super(p, id);
    }


    @InternalApi
    @Deprecated
    public void setDefault() {
        isDefault = true;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
