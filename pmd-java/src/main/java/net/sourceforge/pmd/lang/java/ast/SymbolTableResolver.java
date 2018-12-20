/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.java.JavaLanguageHandler;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.EmptySymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ImportOnDemandSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SamePackageSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolveHelper;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and can't be reused for several ACUs.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SymbolTableResolver extends SideEffectingVisitorAdapter<AstAnalysisConfiguration> {

    private final SymbolTableResolveHelper myResolveHelper;
    // current top of the stack
    private JSymbolTable myStackTop;


    /**
     * Initialize this resolver using an analysis context and a root node.
     *
     * @param context The analysis context
     * @param root    The root node
     */
    public SymbolTableResolver(AstAnalysisConfiguration context, ASTCompilationUnit root) {
        ClassLoader classLoader = PMDASMClassLoader.getInstance(context.getTypeResolutionClassLoader());
        int jdkVersion = ((JavaLanguageHandler) context.getLanguageVersion().getLanguageVersionHandler()).getJdkVersion();

        String packageName = Optional.ofNullable(root.getFirstChildOfType(ASTPackageDeclaration.class))
                                     .map(ASTPackageDeclaration::getPackageNameImage).orElse("");

        myResolveHelper = new SymbolTableResolveHelper(packageName, classLoader, jdkVersion);
        // this is the only place pushOnStack can be circumvented
        myStackTop = EmptySymbolTable.getInstance();

    }

    // The AstAnalysisConfiguration is only used in the constructor
    // The parameter on the visit methods is thus unnecessary
    // TODO introduce another visitor with `void visit(Node);` signature


    @Override
    public void visit(ASTCompilationUnit node, AstAnalysisConfiguration data) {

        Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.findChildrenOfType(ASTImportDeclaration.class).stream()
                                                                        .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

        if (!isImportOnDemand.get(true).isEmpty()) {
            // there are on-demand imports
            pushOnStack((p, h) -> new ImportOnDemandSymbolTable(p, h, isImportOnDemand.get(true)));
        }

        pushOnStack(JavaLangSymbolTable::new);
        pushOnStack(SamePackageSymbolTable::new);

        if (!isImportOnDemand.get(false).isEmpty()) {
            // there are single imports
            pushOnStack((p, h) -> new SingleImportSymbolTable(p, h, isImportOnDemand.get(false)));
        }

        // All of the header symbol tables belong to the CompilationUnit
        node.setSymbolTable(peekStack());
    }


    private void pushOnStack(TableLinker tableLinker) {
        JSymbolTable parent = Objects.requireNonNull(peekStack(), "Cannot link to null parent");
        this.myStackTop = tableLinker.apply(parent, myResolveHelper);
    }


    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private JSymbolTable popStack() {
        JSymbolTable curTop = this.myStackTop;
        this.myStackTop = curTop.getParent();
        return curTop;
    }


    private JSymbolTable peekStack() {
        return this.myStackTop;
    }


    @FunctionalInterface
    private interface TableLinker extends BiFunction<JSymbolTable, SymbolTableResolveHelper, JSymbolTable> {
    }

}
