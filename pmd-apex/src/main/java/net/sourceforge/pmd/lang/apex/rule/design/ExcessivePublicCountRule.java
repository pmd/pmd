/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.internal.AbstractCounterCheckRule;

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
public class ExcessivePublicCountRule extends AbstractCounterCheckRule<ASTUserClass> {

    public ExcessivePublicCountRule() {
        super(ASTUserClass.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 20;
    }

    @Override
    protected int getMetric(ASTUserClass node) {
        int publicMethods =
                node.children(ASTMethod.class)
                        .filter(it -> it.getModifiers().isPublic())
                        .count();
        int publicFields =
                node.children(ASTFieldDeclarationStatements.class)
                        .filter(it -> it.getModifiers().isPublic() && !it.getModifiers().isStatic())
                        .count();

        int publicProperties =
                node.children(ASTProperty.class)
                        .filter(it -> it.getModifiers().isPublic() && !it.getModifiers().isStatic())
                        .count();

        return publicFields + publicMethods + publicProperties;
    }

    @Override
    protected Object[] getViolationParameters(ASTUserClass node, int metric, int limit) {
        return new Object[] { node.getSimpleName(), metric, limit };
    }
}
