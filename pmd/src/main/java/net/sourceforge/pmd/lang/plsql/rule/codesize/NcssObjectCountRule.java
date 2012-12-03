package net.sourceforge.pmd.lang.plsql.rule.codesize;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;
import net.sourceforge.pmd.lang.plsql.rule.codesize.AbstractNcssCountRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for Oracle Object declarations.
 * 
 * @author Stuart Turton
 */
public class NcssObjectCountRule extends AbstractNcssCountRule {
    private final static String CLASS_PATH =NcssObjectCountRule.class.getName(); 
    private final static Logger LOGGER = Logger.getLogger(NcssObjectCountRule.class.getPackage().getName()); 

    /**
     * Count type declarations. This includes Oracle Objects. 
     */
    public NcssObjectCountRule() {
	super(OracleObject.class);
	setProperty(MINIMUM_DESCRIPTOR, 1500d);
    }

    //@Override
    public Object visit(OracleObject node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(NcssObjectCountRule)");

	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTPackageSpecification node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTPackageSpecification)");

	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTypeSpecification)");

	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTriggerUnit)");
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTPackageBody node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTPackageBody)");
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTProgramUnit)");
          return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTypeMethod)");
          return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTMethodDeclaration)");
	return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTFieldDeclaration)");
	return NumericConstants.ONE;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        LOGGER.entering(CLASS_PATH,"visit(getViolationParameters)");
        LOGGER.warning("Node Count ==" + point.getScore() );
	return new String[] { String.valueOf((int) point.getScore()) };
    }
}
