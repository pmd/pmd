/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
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
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and cannot be reused for several ACUs.
 *
 * @since 7.0.0
 */
public final class NSymbolTableResolver {

    private NSymbolTableResolver() {
        // façade
    }

    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        SymTableFactory helper = new SymTableFactory(root.getPackageName(), processor);
        new MyVisitor(root, helper).traverse();
    }

    private static class MyVisitor extends SideEffectingVisitorAdapter<Void> {

        private final ASTCompilationUnit root;
        private final SymTableFactory f;
        private final Deque<NSymbolTable> stack = new ArrayDeque<>();

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

            stack.push(NSymTableImpl.EMPTY);
            root.jjtAccept(this, null);
            stack.pop();

            assert stack.isEmpty()
                : "Unbalanced stack push/pop! Left " + stack;
        }

        @Override
        public void visit(ASTModifierList node, Void data) {
            // do nothing
        }

        @Override
        public void visit(ASTCompilationUnit node, Void data) {
            Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.children(ASTImportDeclaration.class)
                                                                            .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

            int pushed = 0;
            pushed += pushOnStack(f.importsOnDemand(top(), isImportOnDemand.get(true)));
            pushed += pushOnStack(f.javaLangSymTable(top()));
            pushed += pushOnStack(f.samePackageSymTable(top()));
            pushed += pushOnStack(f.singleImportsSymbolTable(top(), isImportOnDemand.get(false)));
            // types declared inside the compilation unit
            pushed += pushOnStack(f.typeOnlySymTable(top(), node.getTypeDeclarations()));

            // All of the header symbol tables belong to the CompilationUnit
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }


        @Override
        public void visit(ASTAnyTypeDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());

            int pushed = pushOnStack(f.typeHeader(top(), node.getSymbol()));

            for (JavaNode it : node.children().drop(1).take(node.getNumChildren() - 2)) {
                setTopSymbolTable(it);
            }

            popStack(pushed);
            pushed = 0;
            pushed += pushOnStack(f.typeBody(top(), node.getSymbol()));
            setTopSymbolTableAndRecurse(node.getBody());
            popStack(pushed);
        }

        @Override
        public void visit(ASTMethodOrConstructorDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());
            int pushed = pushOnStack(f.bodyDeclaration(top(), node.getFormalParameters(), node.getTypeParameters()));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTInitializer node, Void data) {
            int pushed = pushOnStack(f.bodyDeclaration(top(), null, null));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTLambdaExpression node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), formalsOf(node)));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTBlock node, Void data) {
            visitBlockLike(node);
        }

        @Override
        public void visit(ASTSwitchStatement node, Void data) {
            visitSwitch(node);
        }

        @Override
        public void visit(ASTSwitchExpression node, Void data) {
            visitSwitch(node);
        }

        private void visitSwitch(ASTSwitchLike node) {
            setTopSymbolTable(node);
            visitSubtree(node.getTestedExpression());
            visitBlockLike(stmtsOfSwitchBlock(node));
        }


        private void visitBlockLike(Iterable<? extends ASTStatement> node) {
            /*
             * Process the statements of a block in a sequence. Each local
             * var/class declaration is only in scope for the following
             * statements (and its own initializer).
             */
            int pushed = 0;
            for (ASTStatement st : node) {
                // TODO those sym table are not their own shadow groups, they should be merged
                if (st instanceof ASTLocalVariableDeclaration) {
                    pushed += pushOnStack(f.localVarSymTable(top(), ((ASTLocalVariableDeclaration) st).getVarIds()));
                } else if (st instanceof ASTLocalClassStatement) {
                    pushed += pushOnStack(f.localTypeSymTable(top(), ((ASTLocalClassStatement) st).getDeclaration().getSymbol()));
                }


                setTopSymbolTable(st);
                st.jjtAccept(this, null);
            }

            popStack(pushed);
        }

        @Override
        public void visit(ASTForeachStatement node, Void data) {
            // the varId is only in scope in the body and not the iterable expr
            setTopSymbolTableAndRecurse(node.getIterableExpr());

            int pushed = pushOnStack(f.localVarSymTable(top(), node.getVarId()));
            node.getBody().jjtAccept(this, data);
            popStack(pushed);
        }

        @Override
        public void visit(ASTForStatement node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), varsOfInit(node)));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTTryStatement node, Void data) {

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
                    ((JavaNode) child).jjtAccept(this, data);
                }
            } else {
                super.visit(node, data);
            }
        }

        @Override
        public void visit(ASTCatchClause node, Void data) {
            int pushed = pushOnStack(f.localVarSymTable(top(), node.getParameter().getVarId()));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }


        // <editor-fold defaultstate="collapsed" desc="Stack manipulation routines">

        private void setTopSymbolTable(JavaNode node) {
            InternalApiBridge.setSymbolTable(node, top());
        }

        private void setTopSymbolTableAndRecurse(JavaNode node) {
            setTopSymbolTable(node);
            visitSubtree(node);
        }

        private void visitSubtree(JavaNode node) {
            for (JavaNode child : node.children()) {
                child.jjtAccept(this, null);
            }
        }

        private int pushOnStack(NSymbolTable table) {
            if (table == top()) {
                return 0; // and don't set the stack top
            }
            stack.push(table);
            return 1;
        }

        private NSymbolTable popStack() {
            return stack.pop();
        }

        private void popStack(int times) {
            assert stack.size() > times : "Stack is too small (" + times + ") " + stack;
            while (times-- > 0) {
                popStack();
            }
        }

        private NSymbolTable top() {
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
