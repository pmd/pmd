/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Using a DateFormatter (SimpleDateFormatter) which is static can cause
 * unexpected results when used in a multi-threaded environment. This rule will
 * find static (Simple)DateFormatters which are used in an unsynchronized
 * manner.
 * Refer to these Bug Parade issues:
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4093418.html">4093418</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4228335.html">4228335</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4261469.html">4261469</a>
 * see RFE1020790 - Check for SimpleDateFormat as singleton http://sourceforge.net/tracker/index.php?func=detail&aid=1020790&group_id=56262&atid=479924
 * @author Allan Caplan
 */
public class UnsynchronizedStaticDateFormatterRule extends AbstractJavaRule {

    private static Set<String> targets = CollectionUtil.asSet(new String[] {
    	"DateFormat", "SimpleDateFormat", "java.text.DateFormat","java.text.SimpleDateFormat"
    	});

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic()) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !targets.contains(cit.getImage())) {
            return data;
        }
        ASTVariableDeclaratorId var = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        for (NameOccurrence occ: var.getUsages()) {
            Node n = occ.getLocation();
            if (n.getFirstParentOfType(ASTSynchronizedStatement.class) != null) {
                continue;
            }
            // ignore usages, that don't call a method.
            if (!n.getImage().contains(".")) {
                continue;
            }

            ASTMethodDeclaration method = n.getFirstParentOfType(ASTMethodDeclaration.class);
            if (method != null && !method.isSynchronized()) {
                addViolation(data, n);
            }
        }
        return data;
    }
}