/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
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
public final class SymbolTableResolver extends SideEffectingVisitorAdapter<Void> {

    private final SymbolTableResolveHelper myResolveHelper;
    private final ASTCompilationUnit root;
    // current top of the stack
    private JSymbolTable myStackTop;


    /**
     * Initialize this resolver using an analysis context and a root node.
     *
     * @param symResolver Resolver for external references
     * @param jdkVersion  JDK version, determines which classes are present
     *                    in the java.lang package
     * @param root        The root node
     */
    public SymbolTableResolver(SymbolResolver symResolver,
                               int jdkVersion,
                               ASTCompilationUnit root,
                               SemanticChecksLogger logger) {
        this.root = root;
        myResolveHelper = new SymbolTableResolveHelper(root.getPackageName(), symResolver, jdkVersion, logger);
        // this is the only place pushOnStack can be circumvented
        myStackTop = EmptySymbolTable.getInstance();

    }

    /**
     * Start the analysis.
     *
     */
    public void traverse() {
        assert myStackTop instanceof EmptySymbolTable
            : "Top should be an empty symtable when starting the traversal";

        root.jjtAccept(this, null);

        assert myStackTop instanceof EmptySymbolTable
            : "Unbalanced stack push/pop! Top is " + myStackTop;
    }

    // The AstAnalysisConfiguration is only used in the constructor
    // The parameter on the visit methods is unnecessary
    // TODO introduce another visitor with `void visit(Node);` signature


    @Override
    public void visit(ASTCompilationUnit node, Void data) {
        Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.children(ASTImportDeclaration.class)
                                                                        .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

        int pushed = 0;
        pushed += pushOnStack((p, h) -> new ImportOnDemandSymbolTable(p, h, isImportOnDemand.get(true)));
        pushed += pushOnStack(JavaLangSymbolTable::new);
        pushed += pushOnStack((p, h) -> new SamePackageSymbolTable(p, h, node.getPackageDeclaration()));
        pushed += pushOnStack((p, h) -> new SingleImportSymbolTable(p, h, isImportOnDemand.get(false)));

        // All of the header symbol tables belong to the CompilationUnit
        setTopSymbolTableAndRecurse(node);
        popStack(pushed);
    }


    private void setTopSymbolTableAndRecurse(JavaNode node) {
        InternalApiBridge.setSymbolTable(node, peekStack());

        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            node.jjtGetChild(i).jjtAccept(this, null);
        }
    }

    /**
     * Create a new symbol table using {@link TableLinker#createAndLink(JSymbolTable, SymbolTableResolveHelper)},
     * linking it to the top of the stack as its parent.
     *
     * Pushes must naturally be balanced with {@link #popStack()} calls.
     *
     * @return 1 if the table was pushed, 0 if not
     */
    private int pushOnStack(TableLinker tableLinker) {
        JSymbolTable parent = Objects.requireNonNull(peekStack(), "Cannot link to null parent");
        AbstractSymbolTable created = tableLinker.createAndLink(parent, myResolveHelper);
        if (created.isPrunable()) {
            return 0; // and don't set the stack top
        }
        this.myStackTop = created;
        return 1;
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
    private interface TableLinker {

        /** Create a symbol table, linking it to the top of the stack (param 0). */
        AbstractSymbolTable createAndLink(JSymbolTable stackTop, SymbolTableResolveHelper helper);
    }

}
