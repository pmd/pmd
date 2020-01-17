/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.plsql.ast.ASTBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTDeclarativeUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTForAllStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTID;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTObjectDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTObjectNameDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaratorId;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * Visitor for scope creation. Visits all nodes of an AST and creates scope
 * objects for nodes representing syntactic entities which may contain
 * declarations. For example, a block may contain variable definitions (which
 * are declarations) and therefore needs a scope object where these declarations
 * can be associated, whereas an expression can't contain declarations and
 * therefore doesn't need a scope object. With the exception of global scopes,
 * each scope object is linked to its parent scope, which is the scope object of
 * the next embedding syntactic entity that has a scope.
 */
public class ScopeAndDeclarationFinder extends PLSQLParserVisitorAdapter {
    private static final Logger LOGGER = Logger.getLogger(ScopeAndDeclarationFinder.class.getName());

    /**
     * A stack of scopes reflecting the scope hierarchy when a node is visited.
     * This is used to set the parents of the created scopes correctly.
     */
    private Stack<Scope> scopes = new Stack<>();

    /**
     * Sets the scope of a node and adjusts the scope stack accordingly. The
     * scope on top of the stack is set as the parent of the given scope, which
     * is then also stored on the scope stack.
     *
     * @param newScope
     *            the scope for the node.
     * @param node
     *            the AST node for which the scope is to be set.
     * @throws java.util.EmptyStackException
     *             if the scope stack is empty.
     */
    private void addScope(Scope newScope, PLSQLNode node) {
        newScope.setParent(scopes.peek());
        scopes.push(newScope);
        node.setScope(newScope);
    }

    /**
     * Creates a new local scope for an AST node. The scope on top of the stack
     * is set as the parent of the new scope, which is then also stored on the
     * scope stack.
     *
     * @param node
     *            the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException
     *             if the scope stack is empty.
     */
    private void createLocalScope(PLSQLNode node) {
        addScope(new LocalScope(), node);
    }

    /**
     * Creates a new method scope for an AST node. The scope on top of the stack
     * is set as the parent of the new scope, which is then also stored on the
     * scope stack.
     *
     * @param node
     *            the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException
     *             if the scope stack is empty.
     */
    private void createMethodScope(PLSQLNode node) {
        addScope(new MethodScope(node), node);
    }

    /**
     * Creates a new class scope for an AST node. The scope on top of the stack
     * is set as the parent of the new scope, which is then also stored on the
     * scope stack.
     *
     * @param node
     *            the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException
     *             if the scope stack is empty.
     */
    private void createClassScope(PLSQLNode node) {
        if (node instanceof ASTDeclarativeUnit) {
            addScope(new ClassScope(), node);
        } else {
            addScope(new ClassScope(node.getImage()), node);
        }
    }

    /**
     * Creates a new global scope for an AST node. The new scope is stored on
     * the scope stack.
     *
     * @param node
     *            the AST node for which the scope has to be created.
     */
    private void createSourceFileScope(ASTInput node) {
        // When we do full symbol resolution, we'll need to add a truly
        // top-level GlobalScope.
        Scope scope;
        // %TODO generate a SchemaScope, based on inferred or explcitly
        // specified SchemaName
        ASTObjectDeclaration n = null; // node.getPackageDeclaration();
        if (n != null) {
            scope = new SourceFileScope(n.getChild(0).getImage());
        } else {
            scope = new SourceFileScope();
        }
        scopes.push(scope);
        node.setScope(scope);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        createSourceFileScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTPackageSpecification node, Object data) {
        createClassScope(node);
        Scope s = ((PLSQLNode) node.getParent()).getScope();
        s.addDeclaration(new ClassNameDeclaration(node));
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTPackageBody node, Object data) {
        createClassScope(node);
        Scope s = ((PLSQLNode) node.getParent()).getScope();
        s.addDeclaration(new ClassNameDeclaration(node));
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {
        createClassScope(node);
        Scope s = ((PLSQLNode) node.getParent()).getScope();
        s.addDeclaration(new ClassNameDeclaration(node));
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        createClassScope(node);
        Scope s = ((PLSQLNode) node.getParent()).getScope();
        s.addDeclaration(new ClassNameDeclaration(node));
        cont(node);
        return data;
    }

    /*
     * @Override public Object visit(ASTCompoundTriggerBlock node, Object data)
     * { createMethodScope(node); ASTMethodDeclarator md =
     * node.getFirstChildOfType(ASTMethodDeclarator.class);
     * node.getScope().getEnclosingClassScope().addDeclaration(new
     * MethodNameDeclaration(md)); cont(node); return data; }
     */

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        createMethodScope(node);
        // Treat a Timing Point Section like a packaged FUNCTION or PROCEDURE
        node.getScope().getEnclosingScope(ClassScope.class).addDeclaration(new MethodNameDeclaration(node));
        cont(node);
        return data;
    }

    // @Override
    // public Object visit(ASTEnumDeclaration node, Object data) {
    // createClassScope(node);
    // cont(node);
    // return data;
    // }

    // @Override
    // public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
    // createClassScope(node);
    // cont(node);
    // return data;
    // }

    @Override
    public Object visit(ASTObjectDeclaration node, Object data) {
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    /*
     * @Override public Object visit(ASTMethodDeclaration node, Object data) {
     * createMethodScope(node); // // A method declaration my be- //
     * ASTProgramUnit - a standalone or packaged FUNCTION or PROCEDURE //
     * ASTTypeMethod - an OBJECT TYPE method // // The Method declarator is
     * below the ASTProgramUnit / ASTTypeMethod /// List<ASTMethodDeclarator>
     * methodDeclarators =
     * node.findDescendantsOfType(ASTMethodDeclarator.class); if
     * (!methodDeclarators.isEmpty() ) { //Use first Declarator in the list
     * ASTMethodDeclarator md = methodDeclarators.get(0);
     * LOGGER.finest("ClassScope skipped for Schema-level method: methodName=" +
     * node.getMethodName() + "; Image=" + node.getImage() );
     *
     * } //ASTMethodDeclarator md =
     * node.getFirstChildOfType(ASTMethodDeclarator.class); // A PLSQL Method
     * (FUNCTION|PROCEDURE) may be schema-level try {
     * node.getScope().getEnclosingClassScope().addDeclaration(new
     * MethodNameDeclaration(md)); } catch (Exception e) { //@TODO possibly add
     * to a pseudo-ClassScope equivalent to the Schema name
     * LOGGER.finest("ProgramUnit getEnclosingClassScope Exception string=\""+e.
     * getMessage()+"\"");
     * if("getEnclosingClassScope() called on SourceFileScope".equals(e.
     * getMessage())) {
     * LOGGER.finest("ClassScope skipped for Schema-level method: methodName=" +
     * node.getMethodName() + "; Image=" + node.getImage() );
     *
     * //A File-level/Schema-level object may have a Schema-name explicitly
     * specified in the declaration ASTObjectNameDeclaration on =
     * md.getFirstChildOfType(ASTObjectNameDeclaration.class); if( 1 <
     * on.getNumChildren()) { ASTID schemaName =
     * on.getFirstChildOfType(ASTID.class);
     * LOGGER.finest("SchemaName for Schema-level method: methodName=" +
     * node.getMethodName() + "; Image=" + node.getImage() + "is " +
     * schemaName.getImage() );
     *
     * } } } cont(node); return data; }
     */

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        createMethodScope(node);
        ASTMethodDeclarator md = node.getFirstChildOfType(ASTMethodDeclarator.class);
        // A PLSQL Method (FUNCTION|PROCEDURE) may be schema-level
        try {
            node.getScope().getEnclosingScope(ClassScope.class).addDeclaration(new MethodNameDeclaration(md));
        } catch (Exception e) {
            // @TODO possibly add to a pseudo-ClassScope equivalent to the
            // Schema name
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("ProgramUnit getEnclosingClassScope Exception string=\"" + e.getMessage() + "\"");
            }
            if ("getEnclosingClassScope() called on SourceFileScope".equals(e.getMessage())) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("ClassScope skipped for Schema-level method: methodName=" + node.getMethodName()
                            + "; Image=" + node.getImage());
                }

                // A File-level/Schema-level object may have a Schema-name
                // explicitly specified in the declaration
                ASTObjectNameDeclaration on = md.getFirstChildOfType(ASTObjectNameDeclaration.class);
                if (1 < on.getNumChildren()) {
                    ASTID schemaName = on.getFirstChildOfType(ASTID.class);
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("SchemaName for Schema-level method: methodName=" + node.getMethodName()
                                + "; Image=" + node.getImage() + "is " + schemaName.getImage());
                    }

                }
            }
        }
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        createMethodScope(node);
        ASTMethodDeclarator md = node.getFirstChildOfType(ASTMethodDeclarator.class);
        // A PLSQL Method (FUNCTION|PROCEDURE) may be schema-level
        try {
            node.getScope().getEnclosingScope(ClassScope.class).addDeclaration(new MethodNameDeclaration(md));
        } catch (Exception e) {
            // @TODO possibly add to a pseudo-ClassScope equivalent to the
            // Schema name
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("ProgramUnit getEnclosingClassScope Exception string=\"" + e.getMessage() + "\"");
            }
            if ("getEnclosingClassScope() called on SourceFileScope".equals(e.getMessage())) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("ClassScope skipped for Schema-level method: methodName=" + node.getMethodName()
                            + "; Image=" + node.getImage());
                }

                // A File-level/Schema-level object may have a Schema-name
                // explicitly specified in the declaration
                ASTObjectNameDeclaration on = md.getFirstChildOfType(ASTObjectNameDeclaration.class);
                if (1 < on.getNumChildren()) {
                    ASTID schemaName = on.getFirstChildOfType(ASTID.class);
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("SchemaName for Schema-level method: methodName=" + node.getMethodName()
                                + "; Image=" + node.getImage() + "is " + schemaName.getImage());
                    }

                }
            }
        }
        cont(node);
        return data;
    }

    // TODO - what about while loops and do loops?
    @Override
    public Object visit(ASTForStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTForAllStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTVariableOrConstantDeclaratorId node, Object data) {
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        node.getScope().addDeclaration(decl);
        node.setNameDeclaration(decl);
        return super.visit(node, data);
    }

    // @Override
    // public Object visit(ASTSwitchStatement node, Object data) {
    // createLocalScope(node);
    // cont(node);
    // return data;
    // }

    private void cont(PLSQLNode node) {
        super.visit(node, null);
        scopes.pop();
    }
}
