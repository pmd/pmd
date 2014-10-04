/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompoundTriggerBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;

/**
 * @author raik
 *         <p/>
 *         TODO What about initializers?  This only processes methods and constructors
 */
public class DataFlowFacade extends PLSQLParserVisitorAdapter {
    private final static String CLASS_PATH= DataFlowFacade.class.getCanonicalName();
    private final static Logger LOGGER = Logger.getLogger(DataFlowFacade.class.getName()); 

    private StatementAndBraceFinder sbf;
    private VariableAccessVisitor vav;

    public void initializeWith(DataFlowHandler dataFlowHandler, ASTInput node) {
        sbf = new StatementAndBraceFinder(dataFlowHandler);
        vav = new VariableAccessVisitor();
        node.jjtAccept(this, null);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTMethodDeclaration)");
        LOGGER.finest("visit(ASTMethodDeclaration): " 
                      + node.getClass().getCanonicalName() + " @ line " 
                      + node.getBeginLine() 
                      +", column " + node.getBeginColumn()
                      + " --- " + new Throwable().getStackTrace()
                );

        super.visit(node, data) ;
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        LOGGER.exiting(CLASS_PATH,"visit(ASTMethodDeclaration)");
        return data;
    }

    public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTriggerUnit)");
        LOGGER.finest("visit(ASTTriggerUnit): " 
                      + node.getClass().getCanonicalName() + " @ line " 
                      + node.getBeginLine() 
                      +", column " + node.getBeginColumn()
                      + " --- " + new Throwable().getStackTrace()
                );
        if (node.hasDescendantOfType(ASTCompoundTriggerBlock.class))
        {
          LOGGER.finest("visit(ASTTriggerUnit): treating ASTTriggerUnit like a PackageBody " 
                        + node.getClass().getCanonicalName() + " @ line " 
                        + node.getBeginLine() 
                        +", column " + node.getBeginColumn()
                        + " --- " + new Throwable().getStackTrace()
                      );
          //Pass
          super.visit(node, data) ;
        }
        {
          LOGGER.finest("visit(ASTTriggerUnit): treating ASTTriggerUnit as standalone " 
                        + node.getClass().getCanonicalName() + " @ line " 
                        + node.getBeginLine() 
                        +", column " + node.getBeginColumn()
                        + " --- " + new Throwable().getStackTrace()
                      );
          sbf.buildDataFlowFor(node);
          vav.compute(node);
        }
        LOGGER.exiting(CLASS_PATH,"visit(ASTTriggerUnit)");
        return data;
    }

    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTriggerTimingPointSection)");
        LOGGER.finest("visit(ASTTriggerTimingPointSection): " 
                      + node.getClass().getCanonicalName() + " @ line " 
                      + node.getBeginLine() 
                      +", column " + node.getBeginColumn()
                      + " --- " + new Throwable().getStackTrace()
                );
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        LOGGER.exiting(CLASS_PATH,"visit(ASTProgramUnit)");
        return data;
    }

    public Object visit(ASTProgramUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTProgramUnit)");
        LOGGER.finest("visit(ASTProgramUnit): " 
                      + node.getClass().getCanonicalName() + " @ line " 
                      + node.getBeginLine() 
                      +", column " + node.getBeginColumn()
                      + " --- " + new Throwable().getStackTrace()
                );
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        LOGGER.exiting(CLASS_PATH,"visit(ASTProgramUnit)");
        return data;
    }

    public Object visit(ASTTypeMethod node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTypeMethod)");
        LOGGER.finest("visit(ASTTypeMethod): " 
                      + node.getClass().getCanonicalName() + " @ line " 
                      + node.getBeginLine() 
                      +", column " + node.getBeginColumn()
                      + " --- " + new Throwable().getStackTrace()
                );
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        LOGGER.exiting(CLASS_PATH,"visit(ASTTypeMethod)");
        return data;
    }

    /*
    public Object visit(ASTConstructorDeclaration node, Object data) {
        sbf.buildDataFlowFor(node);
        vav.compute(node);
        return data;
    }
    */
}
