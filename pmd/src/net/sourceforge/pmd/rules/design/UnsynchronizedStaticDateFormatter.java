/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * Using a DateFormatter (SimpleDateFormatter) which is static can cause
 * unexpected results when used in a multi threaded environment. This rule will
 * find static (Simple)DateFormatters which are used in an unsynchronized
 * manner.
 * Refer to these Bug Parade issues:
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4093418.html">4093418</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4228335.html">4228335</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4261469.html">4261469</a>
 * see RFE1020790 - Check for SimpleDateFormat as singleton http://sourceforge.net/tracker/index.php?func=detail&aid=1020790&group_id=56262&atid=479924
 * @author Allan Caplan
 */
public class UnsynchronizedStaticDateFormatter extends AbstractJavaRule {

    private static Set<String> targets = new HashSet<String>();
    static {
        targets.add("DateFormat");
        targets.add("SimpleDateFormat");
        targets.add("java.text.DateFormat");
        targets.add("java.text.SimpleDateFormat");
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic()) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !targets.contains(cit.getImage())) {
            return data;
        }
        ASTVariableDeclaratorId var = node.getFirstChildOfType(ASTVariableDeclaratorId.class);
        for (NameOccurrence occ: var.getUsages()) {
            Node n = occ.getLocation();
            if (n.getFirstParentOfType(ASTSynchronizedStatement.class) != null) {
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