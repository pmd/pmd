/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
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
public class ScopeAndDeclarationFinder extends JavaParserVisitorAdapter {

    private ClassLoader classLoader;

    /**
     * A stack of scopes reflecting the scope hierarchy when a node is visited.
     * This is used to set the parents of the created scopes correctly.
     */
    private Deque<Scope> scopes = new ArrayDeque<>();

    /**
     * Creates a new {@link ScopeAndDeclarationFinder} using the current class
     * loader.
     */
    public ScopeAndDeclarationFinder() {
        this(ScopeAndDeclarationFinder.class.getClassLoader());
    }

    /**
     * Creates a new {@link ScopeAndDeclarationFinder}.
     *
     * @param classLoader
     *            the class loader to use to resolve types, see
     *            {@link SourceFileScope} and {@link TypeSet}
     */
    public ScopeAndDeclarationFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

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
    private void addScope(Scope newScope, JavaNode node) {
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
    private void createLocalScope(JavaNode node) {
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
    private void createMethodScope(JavaNode node) {
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
    private void createClassScope(JavaNode node) {
        Scope s = ((JavaNode) node.getParent()).getScope();
        ClassNameDeclaration classNameDeclaration = new ClassNameDeclaration(node);
        s.addDeclaration(classNameDeclaration);

        if (node instanceof ASTClassOrInterfaceBody) {
            addScope(new ClassScope(classNameDeclaration), node);
        } else {
            addScope(new ClassScope(node.getImage(), classNameDeclaration), node);
        }
    }

    /**
     * Creates a new global scope for an AST node. The new scope is stored on
     * the scope stack.
     *
     * @param node
     *            the AST node for which the scope has to be created.
     */
    private void createSourceFileScope(ASTCompilationUnit node) {
        // When we do full symbol resolution, we'll need to add a truly
        // top-level GlobalScope.
        SourceFileScope scope;
        ASTPackageDeclaration n = node.getPackageDeclaration();
        if (n != null) {
            scope = new SourceFileScope(classLoader, n.getChild(0).getImage());
        } else {
            scope = new SourceFileScope(classLoader);
        }
        scope.configureImports(node.findChildrenOfType(ASTImportDeclaration.class));

        scopes.push(scope);
        node.setScope(scope);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        createSourceFileScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        createClassScope(node);
        ((ClassScope) node.getScope()).setIsEnum(true);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        if (node.isAnonymousInnerClass() || node.isEnumChild()) {
            createClassScope(node);
            cont(node);
        } else {
            super.visit(node, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        // top-level blocks for methods should have the same scope as parameters, just skip them
        // same applies to catch statements defining exceptions + the catch block, and for-blocks
        if (node.getParent() instanceof ASTMethodDeclaration
                || node.getParent() instanceof ASTConstructorDeclaration
                || node.getParent() instanceof ASTLambdaExpression
                || node.getParent() instanceof ASTCatchStatement
                || node.getParent() instanceof ASTForStatement) {
            super.visit(node, null);
        } else {
            createLocalScope(node);
            cont(node);
        }
        return data;
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        createMethodScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        createMethodScope(node);
        ASTMethodDeclarator md = node.getFirstChildOfType(ASTMethodDeclarator.class);
        node.getScope().getEnclosingScope(ClassScope.class).addDeclaration(new MethodNameDeclaration(md));
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        createLocalScope(node);
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
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        VariableNameDeclaration decl = new VariableNameDeclaration(node);
        node.getScope().addDeclaration(decl);
        node.setNameDeclaration(decl);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    private void cont(AbstractJavaNode node) {
        super.visit(node, null);
        scopes.pop();
    }
}
