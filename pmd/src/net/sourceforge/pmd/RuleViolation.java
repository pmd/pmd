/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.CanSuppressWarnings;
import net.sourceforge.pmd.ast.SimpleNode;

public class RuleViolation implements IRuleViolation {

    public static class RuleViolationComparator implements Comparator<IRuleViolation> {
        //
        // Changed logic of Comparator so that rules in the same file
        // get grouped together in the output report.
        // DDP 7/11/2002
        //
        public int compare(IRuleViolation r1, IRuleViolation r2) {
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
    private String variableName;
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

        if (node != null) {
	        if (node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) == null) {
	            // This takes care of nodes which are outside a class definition - i.e., import declarations
	            className = "";
	        } else {
	            // default to symbol table lookup
	            className = node.getScope().getEnclosingClassScope().getClassName() == null ? "" : node.getScope().getEnclosingClassScope().getClassName();
	        }
	        // default to symbol table lookup
	        String qualifiedName = null;
	        List<ASTClassOrInterfaceDeclaration> parents = node.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
	        for ( ASTClassOrInterfaceDeclaration parent : parents )
	        {
	        	if (qualifiedName == null) {
	        		qualifiedName = parent.getScope().getEnclosingClassScope().getClassName();
	            } else {
	            	qualifiedName = parent.getScope().getEnclosingClassScope().getClassName() + "$" + qualifiedName;
	            }
	        }
	        // Sourcefile does not have an enclosing class scope...
	        if ( ! "net.sourceforge.pmd.symboltable.SourceFileScope".equals(node.getScope().getClass().getName() ) ) {
	        	className = node.getScope().getEnclosingClassScope().getClassName() == null ? "" : qualifiedName;
	        }
	        setVariableNameIfExists(node);

	        methodName = node.getFirstParentOfType(ASTMethodDeclaration.class) == null ? "" : node.getScope().getEnclosingMethodScope().getName();

	        packageName = node.getScope().getEnclosingSourceFileScope().getPackageName() == null ? "" : node.getScope().getEnclosingSourceFileScope().getPackageName();

	        beginLine = node.getBeginLine();
	        endLine = node.getEndLine();
	        beginColumn = node.getBeginColumn();
	        endColumn = node.getEndColumn();

	        // TODO combine this duplicated code
	        // TODO same for duplicated code in ASTTypeDeclaration && ASTClassOrInterfaceBodyDeclaration
	        List<SimpleNode> parentTypes = new ArrayList<SimpleNode>(node.getParentsOfType(ASTTypeDeclaration.class));
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
	        for (SimpleNode parentType : parentTypes) {
	            CanSuppressWarnings t = (CanSuppressWarnings) parentType;
	            if (t.hasSuppressWarningsAnnotationFor(getRule())) {
	                isSuppressed = true;
	            }
	        }
        } else {
        	className = "";
        	methodName = "";
        	packageName = "";
        	filename = "";
        }
    }

    private void setVariableNameIfExists(SimpleNode node) {
        variableName = (node.getClass().equals(ASTFieldDeclaration.class))
                ? ((ASTFieldDeclaration) node).getVariableName() : "";
        if ("".equals(variableName)) {
            variableName = (node.getClass().equals(ASTLocalVariableDeclaration.class))
                    ? ((ASTLocalVariableDeclaration) node).getVariableName() : "";
        }
        if ("".equals(variableName)) {
            variableName = (node.getClass().equals(ASTVariableDeclaratorId.class))
                    ? node.getImage() : "";
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
        return variableName;
    }

    public String toString() {
        return getFilename() + ":" + getRule() + ":" + getDescription() + ":" + beginLine;
    }

}
