/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.internal.CommonPropertyDescriptors;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Rule attempts to count all public methods and public attributes
 * defined in a class.
 *
 * <p>If a class has a high number of public operations, it might be wise
 * to consider whether it would be appropriate to divide it into
 * subclasses.</p>
 *
 * <p>A large proportion of public members and operations means the class
 * has high potential to be affected by external classes. Futhermore,
 * increased effort will be required to thoroughly test the class.</p>
 *
 * @author ported from Java original of aglover
 */
public class ExcessivePublicCountRule extends AbstractApexRule {


    private static final PropertyDescriptor<Integer> REPORT_LEVEL =
        CommonPropertyDescriptors.reportLevelProperty()
                                 .desc("Minimum number of parameters to trigger a violation")
                                 .defaultValue(20)
                                 .require(positive())
                                 .build();

    public ExcessivePublicCountRule() {
        definePropertyDescriptor(REPORT_LEVEL);
        addRuleChainVisit(ASTUserClass.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        long publicMethods =
            node.findChildrenOfType(ASTMethod.class)
                .stream()
                .filter(it -> it.getModifiers().isPublic() && !it.isSynthetic())
                .count();

        long publicFields =
            node.findChildrenOfType(ASTFieldDeclarationStatements.class)
                .stream()
                .filter(it -> it.getModifiers().isPublic() && !it.getModifiers().isStatic())
                .count();

        if (publicFields + publicMethods > getProperty(REPORT_LEVEL)) {
            addViolation(data, node);
        }

        return data;
    }
}
