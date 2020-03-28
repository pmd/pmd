/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.internal;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.TypeOnlySymTable.localClassesOf;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.TypeOnlySymTable.nestedClassesOf;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.formalsOf;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.resourceIds;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.varsOfBlock;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.varsOfInit;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.VarOnlySymTable.varsOfSwitchBlock;

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
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTRecordConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResourceList;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeBody;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.SideEffectingVisitorAdapter;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and cannot be reused for several ACUs.
 *
 * @since 7.0.0
 */
public final class SymbolTableResolver {

    private final SymbolTableHelper myResolveHelper;
    private final ASTCompilationUnit root;
    // current top of the stack
    private JSymbolTable myStackTop;


    /**
     * Initialize this resolver using an analysis context and a root node.
     *
     * @param symResolver Resolver for external references
     * @param root        The root node
     */
    public SymbolTableResolver(SymbolResolver symResolver,
                               SemanticChecksLogger logger,
                               ASTCompilationUnit root) {
        this.root = root;
        myResolveHelper = new SymbolTableHelper(root.getPackageName(), symResolver, logger);
        // this is the only place pushOnStack can be circumvented
        myStackTop = EmptySymbolTable.getInstance();

    }

    /**
     * Start the analysis.
     */
    public void traverse() {
        assert myStackTop instanceof EmptySymbolTable
            : "Top should be an empty symtable when starting the traversal";

        root.jjtAccept(new MyVisitor(), null);

        assert myStackTop instanceof EmptySymbolTable
            : "Unbalanced stack push/pop! Top is " + myStackTop;
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
        AbstractSymbolTable created = tableLinker.createAndLink(peekStack(), myResolveHelper, data);
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

    @FunctionalInterface
    private interface TableLinker<T> {

        /**
         * Create a symbol table, given its parent, a helper instance,
         * and some other data passed by {@link #pushOnStack(TableLinker, Object)}.
         *
         * @param stackTop Top of the stack, becomes the parent of the new node
         * @param helper   Shared helper
         * @param data     Additional parameter
         */
        AbstractSymbolTable createAndLink(JSymbolTable stackTop, SymbolTableHelper helper, T data);
    }

    private class MyVisitor extends SideEffectingVisitorAdapter<Void> {


        // The AstAnalysisConfiguration is only used in the constructor
        // The parameter on the visit methods is unnecessary
        // TODO introduce another visitor with `void visit(Node);` signature

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

            // All of the header symbol tables belong to the CompilationUnit
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }


        @Override
        public void visit(ASTAnyTypeDeclaration node, Void data) {
            setTopSymbolTable(node.getModifiers());

            int pushed = 0;
            pushed += pushOnStack(TypeOnlySymTable::new, node); // pushes its own name, overrides type params of enclosing type

            if (pushOnStack(TypeOnlySymTable::new, node.getTypeParameters()) > 0) {
                // there are type parameters: the extends/implements/type parameter section know about them

                NodeStream<? extends JavaNode> notBody = node.children().drop(1).filterNot(it -> it instanceof ASTTypeBody);
                for (JavaNode it : notBody) {
                    setTopSymbolTable(it);
                }

                popStack();
            }

            // the following is just for the body

            pushed += pushOnStack(TypeMemberSymTable::new, node); // methods & fields & inherited classes
            pushed += pushOnStack(TypeOnlySymTable::new, nestedClassesOf(node)); // declared classes
            pushed += pushOnStack(TypeOnlySymTable::new, node.getTypeParameters()); // shadow type names of the former 2

            setTopSymbolTableAndRecurse(node.getBody());

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
            int pushed = 0;
            pushed += pushOnStack(VarOnlySymTable::new, varsOfBlock(node));
            pushed += pushOnStack(TypeOnlySymTable::new, localClassesOf(node));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTForeachStatement node, Void data) {
            int pushed = pushOnStack(VarOnlySymTable::new, node.getVarId());
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
        }

        @Override
        public void visit(ASTForStatement node, Void data) {
            int pushed = pushOnStack(VarOnlySymTable::new, varsOfInit(node));
            setTopSymbolTableAndRecurse(node);
            popStack(pushed);
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

            // since there's no node for the block we have to set children individually
            int pushed = pushOnStack(VarOnlySymTable::new, varsOfSwitchBlock(node));

            for (JavaNode notExpr : node.children().drop(1)) {
                setTopSymbolTableAndRecurse(notExpr);
            }

            popStack(pushed);
        }

        @Override
        public void visit(ASTTryStatement node, Void data) {

            ASTResourceList resources = node.getResources();
            if (resources != null) {
                int pushed = pushOnStack(VarOnlySymTable::new, resourceIds(resources));

                setTopSymbolTableAndRecurse(resources);

                ASTBlock body = node.getBody();
                body.jjtAccept(this, data);

                popStack(pushed); // pop resources table before visiting catch & finally

                for (Node child : body.asStream().followingSiblings()) {
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
    }

}
