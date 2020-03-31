/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.formalsOf;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable;


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
            while (times-- > 0) {
                popStack();
            }
        }

        private NSymbolTable top() {
            return stack.getFirst();
        }

        static NodeStream<ASTStatement> stmtsOfSwitchBlock(ASTSwitchLike node) {
            return node.getBranches()
                       .filterIs(ASTSwitchFallthroughBranch.class)
                       .flatMap(ASTSwitchFallthroughBranch::getStatements);
        }


        static NodeStream<ASTLocalVariableDeclaration> stmtsOfResources(ASTResourceList node) {
            return node.toStream().map(ASTResource::asLocalVariableDeclaration);
        }


        // </editor-fold>

    }

}
