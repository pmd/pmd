/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTRecordConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and cannot be reused for several ACUs.
 *
 * @since 7.0.0
 */
public final class SymbolTableResolver {

    private SymbolTableResolver() {
        // façade
    }

    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        SymTableFactory helper = new SymTableFactory(root.getPackageName(), processor);
        new MyVisitor(root, helper).traverse();
    }

    private static class MyVisitor extends JavaVisitorBase<Void, Void> {

        private final ASTCompilationUnit root;
        private final SymTableFactory f;
        private final Deque<JSymbolTable> stack = new ArrayDeque<>();

        /*
            TODO do disambiguation entirely in this visitor
             This is because qualified ctor invocations need the type of their LHS
             This is tricky because disambig needs to proceed bottom up
         */

        MyVisitor(ASTCompilationUnit root, SymTableFactory helper) {
            this.root = root;
            f = helper;
        }


        /**
         * Start the analysis.
         */
        public void traverse() {
            assert stack.isEmpty()
                : "Stack should be empty when starting the traversal";

            stack.push(SymbolTableImpl.EMPTY);
            root.acceptVisitor(this, null);
            stack.pop();

            assert stack.isEmpty()
                : "Unbalanced stack push/pop! Left " + stack;
        }

        @Override
        public Void visit(ASTModifierList node, Void data) {
            // do nothing
            return null;
        }

        @Override
        public Void visit(ASTCompilationUnit node, Void data) {
            Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.children(ASTImportDeclaration.class)
                                                                            .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

            int pushed = 0;
            pushed += pushOnStack(f.importsOnDemand(top(), isImportOnDemand.get(true)));
            pushed += pushOnStack(f.javaLangSymTable(top()));
            pushed += pushOnStack(f.samePackageSymTable(top()));
            pushed += pushOnStack(f.singleImportsSymbolTable(top(), isImportOnDemand.get(false)));

            NodeStream<ASTAnyTypeDeclaration> typeDecls = node.getTypeDeclarations();

            // types declared inside the compilation unit
            pushed += pushOnStack(f.typesInFile(top(), typeDecls));

            setTopSymbolTable(node);

            for (ASTAnyTypeDeclaration td : typeDecls) {
                // preprocess all sibling types
                processTypeHeader(td);
            }

            // All of the header symbol tables belong to the CompilationUnit
            visitChildren(node, null);

            popStack(pushed);

            return null;
        }


        private void processTypeHeader(ASTAnyTypeDeclaration node) {
            setTopSymbolTable(node.getModifiers());

            int pushed = pushOnStack(f.selfType(top(), node.getSymbol()));
            pushed += pushOnStack(f.typeHeader(top(), node.getSymbol()));

            NodeStream<? extends JavaNode> notBody = node.children().drop(1).take(node.getNumChildren() - 2);
            for (JavaNode it : notBody) {
                setTopSymbolTable(it);
            }

            popStack(pushed - 1);

            // resolve the supertypes, necessary for TypeMemberSymTable
            f.disambig(notBody, node, true); // extends/implements

            setTopSymbolTable(node);
            popStack();
        }

        @Override
        public Void visitTypeDecl(ASTAnyTypeDeclaration node, Void data) {
            int pushed = 0;

            // the following is just for the body
            // helper.pushCtxType(node.getSymbol());

            pushed += pushOnStack(f.typeBody(top(), node.getSymbol()));

            setTopSymbolTable(node.getBody());

            // preprocess siblings
            node.getDeclarations()
                .filterIs(ASTAnyTypeDeclaration.class)
                .forEach(this::processTypeHeader);


            // process fields first, their type is needed for JSymbolTable#resolveValue
            f.disambig(node.getDeclarations()
                           .filterIs(ASTFieldDeclaration.class)
                           .map(ASTFieldDeclaration::getTypeNode),
                       node,
                       false);

            visitChildren(node.getBody(), null);

            // helper.popCtxType();

            popStack(pushed);

            return null;
        }

        @Override
        public Void visit(ASTAnonymousClassDeclaration node, Void data) {

            // the supertype node, should be disambiguated to access members of the type
            f.disambig(node.asStream().parents()
                           .filterIs(ASTConstructorCall.class)
                           .map(ASTConstructorCall::getTypeNode),
                       node.getEnclosingType(),
                       false);

            // helper.pushCtxType(node.getSymbol());
            int pushed = pushOnStack(f.typeBody(top(), node.getSymbol())); // methods & fields & inherited classes

            setTopSymbolTableAndRecurse(node.getBody());

            // helper.popCtxType();
            popStack(pushed);

            return null;
        }


        @Override
        public Void visitMethodOrCtor(ASTMethodOrConstructorDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());
            int pushed = pushOnStack(f.bodyDeclaration(top(), node.getFormalParameters(), node.getTypeParameters()));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTInitializer node, Void data) {
            int pushed = pushOnStack(f.bodyDeclaration(top(), null, null));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }


        @Override
        public Void visit(ASTRecordConstructorDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());
            int pushed = pushOnStack(f.recordCtor(top(), node.getSymbol()));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }


        @Override
        public Void visit(ASTLambdaExpression node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), formalsOf(node)));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTBlock node, Void data) {
            return visitBlockLike(node);
        }

        @Override
        public Void visit(ASTSwitchStatement node, Void data) {
            return visitSwitch(node);
        }

        @Override
        public Void visit(ASTSwitchExpression node, Void data) {
            return visitSwitch(node);
        }

        private Void visitSwitch(ASTSwitchLike node) {
            setTopSymbolTable(node);
            visitChildren(node.getTestedExpression(), null);
            visitBlockLike(stmtsOfSwitchBlock(node));
            return null;
        }


        private Void visitBlockLike(Iterable<? extends ASTStatement> node) {
            /*
             * Process the statements of a block in a sequence. Each local
             * var/class declaration is only in scope for the following
             * statements (and its own initializer).
             */
            int pushed = 0;
            for (ASTStatement st : node) {
                if (st instanceof ASTLocalVariableDeclaration) {
                    pushed += pushOnStack(f.localVarSymTable(top(), ((ASTLocalVariableDeclaration) st).getVarIds()));
                } else if (st instanceof ASTLocalClassStatement) {
                    ASTAnyTypeDeclaration local = ((ASTLocalClassStatement) st).getDeclaration();
                    pushed += pushOnStack(f.localTypeSymTable(top(), local.getSymbol()));
                    processTypeHeader(local);
                }

                setTopSymbolTable(st);
                st.acceptVisitor(this, null);
            }

            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTForeachStatement node, Void data) {
            // the varId is only in scope in the body and not the iterable expr
            setTopSymbolTableAndRecurse(node.getIterableExpr());

            int pushed = pushOnStack(f.localVarSymTable(top(), node.getVarId().getSymbol()));
            node.getBody().acceptVisitor(this, data);
            popStack(pushed);
            return null;
        }

        @Override
        public Void visit(ASTForStatement node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), varsOfInit(node)));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }


        // TODO constructors of inner classes push a scope that depends on type resolution of the qualifier
        // Eg `foo.new Bar()` doesn't require an import for Bar

        @Override
        public Void visit(ASTTryStatement node, Void data) {

            ASTResourceList resources = node.getResources();
            if (resources != null) {
                NodeStream<ASTStatement> union =
                        NodeStream.union(
                                stmtsOfResources(resources),
                                // use the body instead of unwrapping it so
                                // that it has the correct symbol table too
                                NodeStream.of(node.getBody())
                        );
                visitBlockLike(union);

                for (Node child : node.getBody().asStream().followingSiblings()) {
                    ((JavaNode) child).acceptVisitor(this, data);
                }
            } else {
                super.visit(node, data);
            }

            return null;
        }

        @Override
        public Void visit(ASTCatchClause node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), node.getParameter().getVarId().getSymbol()));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
            return null;
        }


        // <editor-fold defaultstate="collapsed" desc="Stack manipulation routines">

        private void setTopSymbolTable(JavaNode node) {
            InternalApiBridge.setSymbolTable(node, top());
        }

        private void setTopSymbolTableAndRecurse(JavaNode node) {
            setTopSymbolTable(node);
            visitChildren(node, null);
        }

        private int pushOnStack(JSymbolTable table) {
            if (table == top()) {
                return 0; // and don't set the stack top
            }
            stack.push(table);
            return 1;
        }

        private JSymbolTable popStack() {
            return stack.pop();
        }

        private void popStack(int times) {
            assert stack.size() > times : "Stack is too small (" + times + ") " + stack;
            while (times-- > 0) {
                popStack();
            }
        }

        private JSymbolTable top() {
            return stack.getFirst();
        }


        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Convenience methods">


        static NodeStream<ASTStatement> stmtsOfSwitchBlock(ASTSwitchLike node) {
            return node.getBranches()
                       .filterIs(ASTSwitchFallthroughBranch.class)
                       .flatMap(ASTSwitchFallthroughBranch::getStatements);
        }


        static NodeStream<ASTLocalVariableDeclaration> stmtsOfResources(ASTResourceList node) {
            return node.toStream().map(ASTResource::asLocalVariableDeclaration);
        }


        static NodeStream<ASTVariableDeclaratorId> varsOfInit(ASTForStatement node) {
            return NodeStream.of(node.getInit())
                             .filterIs(ASTLocalVariableDeclaration.class)
                             .flatMap(ASTLocalVariableDeclaration::getVarIds);
        }

        static NodeStream<ASTVariableDeclaratorId> formalsOf(ASTLambdaExpression node) {
            return node.getParameters().toStream().map(ASTLambdaParameter::getVarId);
        }

        static NodeStream<ASTVariableDeclaratorId> formalsOf(ASTMethodOrConstructorDeclaration node) {
            return node.getFormalParameters().toStream().map(ASTFormalParameter::getVarId);
        }
        // </editor-fold>


    }

}
