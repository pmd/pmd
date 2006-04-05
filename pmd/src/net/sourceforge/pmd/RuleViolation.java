/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.CanSuppressWarnings;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.MethodScope;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RuleViolation implements IRuleViolation {

    public static class RuleViolationComparator implements Comparator {
        //
        // Changed logic of Comparator so that rules in the same file
        // get grouped together in the output report.
        // DDP 7/11/2002
        //
        public int compare(Object o1, Object o2) {
            IRuleViolation r1 = (IRuleViolation) o1;
            IRuleViolation r2 = (IRuleViolation) o2;
            if (!r1.getFilename().equals(r2.getFilename())) {
                return r1.getFilename().compareTo(r2.getFilename());
            }

            if (r1.getBeginLine() != r2.getBeginLine())
                return r1.getBeginLine() - r2.getBeginLine();

            if (r1.getDescription() != null && r2.getDescription() != null && !r1.getDescription().equals(r2.getDescription())) {
                return r1.getDescription().compareTo(r2.getDescription());
            }

            if (r1.getBeginLine() == r2.getBeginLine()) {
                return 1;
            }
            
            // line number diff maps nicely to compare()
            return r1.getBeginLine() - r2.getBeginLine();
        }
    }

    private Rule rule;
    private String description;
    private String filename;

    private String className;
    private String methodName;
    private String packageName;
    private int beginLine;
    private int endLine;

    private int beginColumn;
    private int endColumn;
    private boolean isSuppressed;

    public RuleViolation(Rule rule, RuleContext ctx, SimpleNode node) {
        this(rule, ctx, node, rule.getMessage());
    }

    public RuleViolation(Rule rule, RuleContext ctx, SimpleNode node, String specificMsg) {
        this.rule = rule;
        this.filename = ctx.getSourceCodeFilename();
        this.description = specificMsg;

        if (node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) == null) {
            // This takes care of nodes which are outside a class definition - i.e., import declarations
            className = "";
        } else {
            // default to symbol table lookup
            className = node.getScope().getEnclosingClassScope().getClassName() == null ? "" : node.getScope().getEnclosingClassScope().getClassName();
        }

        methodName = node.getFirstParentOfType(ASTMethodDeclaration.class) == null ? "" : ((MethodScope) node.getScope().getEnclosingMethodScope()).getName();

        packageName = node.getScope().getEnclosingSourceFileScope().getPackageName() == null ? "" : node.getScope().getEnclosingSourceFileScope().getPackageName();

        beginLine = node.getBeginLine();
        endLine = node.getEndLine();
        beginColumn = node.getBeginColumn();
        endColumn = node.getEndColumn();

        // TODO combine this duplicated code
        // TODO same for duplicated code in ASTTypeDeclaration && ASTClassOrInterfaceBodyDeclaration
        List parentTypes = node.getParentsOfType(ASTTypeDeclaration.class);
        if (node instanceof ASTTypeDeclaration) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTClassOrInterfaceBodyDeclaration.class));
        if (node instanceof ASTClassOrInterfaceBodyDeclaration) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTFormalParameter.class));
        if (node instanceof ASTFormalParameter) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTLocalVariableDeclaration.class));
        if (node instanceof ASTLocalVariableDeclaration) {
            parentTypes.add(node);
        }
        for (Iterator i = parentTypes.iterator(); i.hasNext();) {
            CanSuppressWarnings t = (CanSuppressWarnings) i.next();
            if (t.hasSuppressWarningsAnnotationFor(getRule())) {
                isSuppressed = true;
            }
        }
    }

    public Rule getRule() {
        return rule;
    }

    public boolean isSuppressed() {
        return this.isSuppressed;
    }

    public int getBeginColumn() {
        return beginColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getPackageName() {
        return packageName;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getVariableName() {
        return "";
    }

    public String toString() {
        return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + beginLine;
    }

}
