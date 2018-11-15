/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.java.JavaLanguageHandler;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.EmptySymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ImportOnDemandSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SamePackageSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Visitor that builds all symbol table stacks for a compilation unit.
 * It's bound to a compilation unit and can't be reused for several ACUs.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SymbolTableResolver extends SideEffectingVisitorAdapter<AstAnalysisConfiguration> {

    private final String packageName;
    private final PMDASMClassLoader classLoader;
    private final int jdkVersion;
    // current top of the stack
    private JSymbolTable top;


    /**
     * Initialize this resolver using an analysis context and a root node.
     *
     * @param context The analysis context
     * @param root    The root node
     */
    public SymbolTableResolver(AstAnalysisConfiguration context, ASTCompilationUnit root) {
        this.classLoader = PMDASMClassLoader.getInstance(context.getTypeResolutionClassLoader());
        this.jdkVersion = ((JavaLanguageHandler) context.getLanguageVersion().getLanguageVersionHandler()).getJdkVersion();

        packageName = Optional.ofNullable(root.getFirstChildOfType(ASTPackageDeclaration.class))
                              .map(ASTPackageDeclaration::getPackageNameImage).orElse("");

        this.top = EmptySymbolTable.getInstance();
    }


    @Override
    public void visit(ASTCompilationUnit node, AstAnalysisConfiguration data) {

        Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.findChildrenOfType(ASTImportDeclaration.class).stream()
                                                                        .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

        if (!isImportOnDemand.get(true).isEmpty()) {
            // there are on-demand imports
            pushOnStack(new ImportOnDemandSymbolTable(peekStack(), classLoader, isImportOnDemand.get(true), packageName));
        }

        pushOnStack(new JavaLangSymbolTable(peekStack(), jdkVersion));
        pushOnStack(new SamePackageSymbolTable(peekStack(), classLoader, packageName));

        if (!isImportOnDemand.get(false).isEmpty()) {
            // there are single imports
            pushOnStack(new SingleImportSymbolTable(peekStack(), classLoader, isImportOnDemand.get(false), packageName));
        }

        // All of the header symbol tables belong to the CompilationUnit
        node.setSymbolTable(peekStack());
    }


    private JSymbolTable getTopOfStack() {
        return top;
    }


    private void pushOnStack(JSymbolTable table) {
        if (!table.getParent().equals(peekStack())) {
            throw new IllegalStateException("Tried to push a table that is not linked to the current stack top");
        }
        this.top = table;
    }


    private JSymbolTable popStack() {
        JSymbolTable curTop = this.top;
        this.top = curTop.getParent();
        return curTop;
    }


    private JSymbolTable peekStack() {
        return this.top;
    }

}
