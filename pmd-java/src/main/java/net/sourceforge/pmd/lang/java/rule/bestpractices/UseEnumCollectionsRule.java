/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Detect cases where EnumSet and EnumMap can be used.
 *
 * @author Clément Fournier
 */
public class UseEnumCollectionsRule extends AbstractJavaRulechainRule {

    public UseEnumCollectionsRule() {
        super(ASTConstructorCall.class);
    }


    @Override
    public Object visit(ASTConstructorCall call, Object data) {
        JTypeMirror builtType = call.getTypeMirror();

        if (!builtType.isRaw()) {
            boolean isMap = TypeTestUtil.isExactlyA(HashMap.class, builtType);
            if (isMap || TypeTestUtil.isExactlyA(HashSet.class, builtType)) {

                List<JTypeMirror> typeArgs = ((JClassType) builtType).getTypeArgs();
                JTypeDeclSymbol keySymbol = typeArgs.get(0).getSymbol();

                if (keySymbol instanceof JClassSymbol && ((JClassSymbol) keySymbol).isEnum()) {
                    String enumCollectionReplacement = isMap ? "EnumMap" : "EnumSet";
                    asCtx(data).addViolation(call.getTypeNode(), enumCollectionReplacement);
                }
            }
        }
        return null;
    }
}
