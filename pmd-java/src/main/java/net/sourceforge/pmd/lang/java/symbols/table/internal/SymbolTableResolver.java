/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.formalsOf;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.varsOfInit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
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
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;
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

    /**
     * Resolve symbol tables in the given file. After this, each node's
     * {@link JavaNode#getSymbolTable()} returns a non-null value, which
     * is used for AST disambiguation.
     *
     * This pass needs to disambiguate some type nodes early, namely those
     * in the "extends" or "implements" clause of type declarations. This
     * is because members inherited from supertypes need to be part of the
     * symbol tables, and so they influence disambiguation too.
     */
    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit root) {
        SymbolTableHelper helper = new SymbolTableHelper(root.getPackageName(), processor);
        new MyVisitor(root, helper).traverse();
    }


    @FunctionalInterface
    private interface TableLinker<T> {

        /**
         * Create a symbol table, given its parent, a helper instance,
         * and some other data passed by {@link MyVisitor#pushOnStack(TableLinker, Object)}.
         *
         * @param stackTop Top of the stack, becomes the parent of the new node
         * @param helper   Shared helper
         * @param data     Additional parameter
         */
        AbstractSymbolTable createAndLink(JSymbolTable stackTop, SymbolTableHelper helper, T data);
    }

    private static class MyVisitor extends SideEffectingVisitorAdapter<Void> {

        private final ASTCompilationUnit root;
        private final SymbolTableHelper helper;
        private JSymbolTable myStackTop;

        MyVisitor(ASTCompilationUnit root, SymbolTableHelper helper) {
            this.root = root;
            this.helper = helper;
            // this is the only place pushOnStack can be circumvented
            myStackTop = EmptySymbolTable.getInstance();
        }


        /**
         * Start the analysis.
         */
        public void traverse() {
            assert myStackTop instanceof EmptySymbolTable
                : "Top should be an empty symtable when starting the traversal";

            root.jjtAccept(this, null);

            assert myStackTop instanceof EmptySymbolTable
                : "Unbalanced stack push/pop! Top is " + myStackTop;
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
            pushed += pushOnStack(ImportOnDemandSymbolTable::new, isImportOnDemand.get(true));
            pushed += pushOnStack(JavaLangSymbolTable::new, node);
            pushed += pushOnStack(SamePackageSymbolTable::new, node);
            pushed += pushOnStack(SingleImportSymbolTable::new, isImportOnDemand.get(false));
            // types declared inside the compilation unit
            pushed += pushOnStack(TypeOnlySymTable::new, node.getTypeDeclarations());

            setTopSymbolTable(node);

            for (ASTAnyTypeDeclaration td : node.getTypeDeclarations()) {
                // preprocess all sibling types
                processTypeHeader(td);
            }

            // All of the header symbol tables belong to the CompilationUnit
            visitSubtree(node);


            popStack(pushed);
        }


        private void processTypeHeader(ASTAnyTypeDeclaration node) {
            setTopSymbolTable(node.getModifiers());

            int pushed = 0;
            pushed += pushOnStack(TypeOnlySymTable::new, node); // pushes its own name, overrides type params of enclosing type

            NodeStream<? extends JavaNode> notBody = node.children().take(node.getNumChildren() - 1).drop(1);

            if (pushOnStack(TypeOnlySymTable::new, node.getTypeParameters()) > 0) {
                // there are type parameters: the extends/implements/type parameter section know about them

                for (JavaNode it : notBody) {
                    setTopSymbolTable(it);
                }

                popStack();
            }

            // resolve the supertypes, necessary for TypeMemberSymTable
            helper.earlyDisambig(notBody); // extends/implements

            setTopSymbolTable(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTAnyTypeDeclaration node, Void data) {
            int pushed = restoreStack(node.getSymbolTable());

            // the following is just for the body
            helper.pushCtxType(node.getSymbol());

            pushed += pushOnStack(TypeMemberSymTable::new, node); // methods & fields & inherited classes
            pushed += pushOnStack(TypeOnlySymTable::new, node.getTypeParameters()); // shadow type names of the former 2

            setTopSymbolTable(node.getBody());

            // preprocess siblings
            node.getDeclarations()
                .filterIs(ASTAnyTypeDeclaration.class)
                .forEach(this::processTypeHeader);


            // process fields first, their type is needed for JSymbolTable#resolveValue
            helper.earlyDisambig(node.getDeclarations()
                                     .filterIs(ASTFieldDeclaration.class)
                                     .map(ASTFieldDeclaration::getTypeNode));


            visitSubtree(node.getBody());

            helper.popCtxType();

            popStack(pushed);
        }

        @Override
        public void visit(ASTAnonymousClassDeclaration node, Void data) {

            // the supertype node, should be disambiguated to access members of the type
            helper.earlyDisambig(node.asStream().parents()
                                     .filterIs(ASTConstructorCall.class)
                                     .map(ASTConstructorCall::getTypeNode));

            helper.pushCtxType(node.getSymbol());
            int pushed = pushOnStack(TypeMemberSymTable::new, node); // methods & fields & inherited classes

            setTopSymbolTableAndRecurse(node.getBody());

            helper.popCtxType();
            popStack(pushed);
        }

        @Override
        public void visit(ASTMethodOrConstructorDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());

            int pushed = 0;
            pushed += pushOnStack(TypeOnlySymTable::new, node.getTypeParameters());
            pushed += pushOnStack(VarOnlySymTable::new, formalsOf(node));

            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTRecordConstructorDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());

            int pushed = 0;
            pushed += pushOnStack(VarOnlySymTable::new, node);

            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTLambdaExpression node, Void data) {
            int pushed = pushOnStack(VarOnlySymTable::new, formalsOf(node));
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
                if (st instanceof ASTLocalVariableDeclaration) {
                    pushed += pushOnStack(VarOnlySymTable::new, ((ASTLocalVariableDeclaration) st).getVarIds());
                } else if (st instanceof ASTLocalClassStatement) {
                    ASTClassOrInterfaceDeclaration local = ((ASTLocalClassStatement) st).getDeclaration();
                    pushed += pushOnStack(TypeOnlySymTable::new, local);
                    processTypeHeader(local);
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

            int pushed = pushOnStack(VarOnlySymTable::new, node.getVarId());
            node.getBody().jjtAccept(this, data);
            popStack(pushed);
        }

        @Override
        public void visit(ASTForStatement node, Void data) {
            int pushed = pushOnStack(VarOnlySymTable::new, varsOfInit(node));
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
            int pushed = pushOnStack(VarOnlySymTable::new, node.getParameter().getVarId());
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }


        // <editor-fold defaultstate="collapsed" desc="Stack manipulation routines">

        private void setTopSymbolTable(JavaNode node) {
            InternalApiBridge.setSymbolTable(node, peekStack());
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


        /**
         * Create a new symbol table using {@link TableLinker#createAndLink(JSymbolTable, SymbolTableHelper, Object)},
         * linking it to the top of the stack as its parent.
         *
         * Pushes must naturally be balanced with {@link #popStack()} calls.
         *
         * @param data Additional param passed to the linker. Passing parameters
         *             like this avoids having to create a capturing lambda.
         *
         * @return 1 if the table was pushed, 0 if not
         */
        private <T> int pushOnStack(TableLinker<T> tableLinker, T data) {
            AbstractSymbolTable created = tableLinker.createAndLink(peekStack(), helper, data);
            return pushOnStack(created) ? 1 : 0;
        }

        private boolean pushOnStack(AbstractSymbolTable table) {
            assert table.getParent() == peekStack() : "Wrong parent";
            if (table.isPrunable()) {
                return false; // and don't set the stack top
            }
            this.myStackTop = table;
            return true;
        }

        private int restoreStack(final JSymbolTable leaf) {
            assert leaf != null : "Table not set";

            final JSymbolTable curTop = this.myStackTop;
            int i = 0;
            JSymbolTable parent = leaf;
            while (parent != null && parent != curTop) {  // NOPMD - intentional check for reference equality
                i++;
                parent = parent.getParent();
            }

            assert parent != null : "Sibling left dangling tables on the stack";

            this.myStackTop = leaf;
            return i;
        }

        private JSymbolTable popStack() {
            JSymbolTable curTop = this.myStackTop;
            this.myStackTop = curTop.getParent();
            return curTop;
        }

        private void popStack(int times) {
            while (times-- > 0) {
                popStack();
            }
        }

        private JSymbolTable peekStack() {
            return this.myStackTop;
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
