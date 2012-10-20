package net.sourceforge.pmd.util.designer;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
//import net.sourceforge.pmd.lang.plsql.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;

public class DFAPLSQLGraphRule extends AbstractPLSQLRule {

    private List<ASTMethodDeclaration> methods;
    private List<ASTProgramUnit> programUnits;
    private List<ASTTriggerUnit> triggerUnits;
    private List<ASTTypeMethod> typeMethods;
    // SRT private List<ASTConstructorDeclaration> constructors;

    public DFAPLSQLGraphRule() {
	super();
	super.setUsesDFA();
    }

    public List<ASTMethodDeclaration> getMethods() {
	return this.methods;
    }

    public List<ASTProgramUnit> getProgramUnits() {
	return this.programUnits;
    }

    public List<ASTTriggerUnit> getTriggerUnits() {
	return this.triggerUnits;
    }

    /* SRT public List<ASTConstructorDeclaration> getConstructors() {
	return this.constructors;
    } */

    /**
     * 
     * @param node
     * @param data
     * @return 
     */
    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
	methods.add(node);
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
	triggerUnits.add(node);
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
	programUnits.add(node);
	return super.visit(node, data);
    }
    @Override
    public Object visit(ASTTypeMethod node, Object data) {
	typeMethods.add(node);
	return super.visit(node, data);
    }

    /* @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
	constructors.add(node);
	return super.visit(node, data);
    }*/

    @Override
    public Object visit(ASTInput acu, Object data) {
	methods = new ArrayList<ASTMethodDeclaration>();
	programUnits = new ArrayList<ASTProgramUnit>();
	triggerUnits = new ArrayList<ASTTriggerUnit>();
	typeMethods = new ArrayList<ASTTypeMethod>();
	//constructors = new ArrayList<ASTConstructorDeclaration>();
	return super.visit(acu, data);
    }
}
