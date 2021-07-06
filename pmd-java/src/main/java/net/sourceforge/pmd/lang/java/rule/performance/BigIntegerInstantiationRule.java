/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Rule that marks instantiations of new {@link BigInteger} or {@link BigDecimal} objects, when there is a well-known
 * constant available, such as {@link BigInteger#ZERO}.
 */
public class BigIntegerInstantiationRule extends AbstractJavaRulechainRule {


    private static final Set<String> CONSTANTS = CollectionUtil.setOf("0", "0.", "1");

    public BigIntegerInstantiationRule() {
        super(ASTConstructorCall.class);
    }


    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        LanguageVersion languageVersion = node.getAstInfo().getLanguageVersion();
        boolean jdk15 = languageVersion
                .compareTo(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5")) >= 0;
        boolean jdk9 = languageVersion
                .compareTo(LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("9")) >= 0;

        if (TypeTestUtil.isA(BigInteger.class, node) || jdk15 && TypeTestUtil.isA(BigDecimal.class, node)) {

            @NonNull
            ASTArgumentList arguments = node.getArguments();
            if (arguments.size() == 1) {
                ASTExpression firstArg = arguments.get(0);
                if (firstArg instanceof ASTStringLiteral) {
                    String img = ((ASTStringLiteral) firstArg).getConstValue();
                    if (CONSTANTS.contains(img) || jdk15 && "10".equals(img)
                            || jdk9 && "2".equals(img)) {
                        addViolation(data, node);
                    }
                } else if (firstArg instanceof ASTNumericLiteral) {
                    Number val = ((ASTNumericLiteral) firstArg).getConstValue();
                    if (val.equals(0) || val.equals(1) || jdk15 && val.equals(10)) {
                        addViolation(data, node);
                    }
                }

            }
        }
        return data;
    }
}
