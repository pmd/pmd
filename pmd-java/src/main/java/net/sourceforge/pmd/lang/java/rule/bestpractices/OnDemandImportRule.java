/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.28.0
 */
public final class OnDemandImportRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> ALLOW_STATIC_IMPORTS_FROM =
            PropertyFactory.stringListProperty("allowStaticImportsFrom")
                    .desc("Packages from which static on-demand imports are allowed. Use * to allow all.")
                    .defaultValues("org.junit.jupiter.api", "org.junit", "org.testng")
                    .build();

    private static final PropertyDescriptor<List<String>> ALLOW_TYPE_IMPORTS_FROM =
            PropertyFactory.stringListProperty("allowTypeImportsFrom")
                    .desc("Packages from which type on-demand imports are allowed. Use * to allow all.")
                    .defaultValue(Collections.emptyList())
                    .build();

    public OnDemandImportRule() {
        super(ASTImportDeclaration.class);
        definePropertyDescriptor(ALLOW_STATIC_IMPORTS_FROM);
        definePropertyDescriptor(ALLOW_TYPE_IMPORTS_FROM);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.isImportOnDemand()) {
            if (!isAllowed(node.getImportedName(), node.isStatic())) {
                ((RuleContext) data).addViolation(node);
            }
        }
        return null;
    }

    private boolean isAllowed(String importedName, boolean isStatic) {
        if (isStatic) {
            for (String allowedPackage : getProperty(ALLOW_STATIC_IMPORTS_FROM)) {
                if ("*".equals(allowedPackage)
                        || importedName.equals(allowedPackage)
                        || importedName.startsWith(allowedPackage + ".")) {
                    return true;
                }
            }
        } else {
            for (String allowedPackage : getProperty(ALLOW_TYPE_IMPORTS_FROM)) {
                if ("*".equals(allowedPackage)
                        || importedName.equals(allowedPackage)) {
                    return true;
                }
            }
        }
        return false;
    }
}
