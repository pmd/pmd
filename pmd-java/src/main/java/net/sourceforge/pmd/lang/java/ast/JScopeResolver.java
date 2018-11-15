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
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.EmptyScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.JavaLangScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageScope;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportScope;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Visitor that builds the symbol table tree for a compilation unit.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JScopeResolver extends SideEffectingVisitorAdapter<AstAnalysisConfiguration> {

    private String packageName;
    private final PMDASMClassLoader classLoader;
    private final int jdkVersion;
    private JScope top;


    public JScopeResolver(AstAnalysisConfiguration data) {
        this.classLoader = PMDASMClassLoader.getInstance(data.getTypeResolutionClassLoader());
        this.jdkVersion = ((JavaLanguageHandler) data.getLanguageVersion().getLanguageVersionHandler()).getJdkVersion();
    }


    @Override
    public void visit(ASTCompilationUnit node, AstAnalysisConfiguration data) {

        packageName = Optional.ofNullable(node.getFirstChildOfType(ASTPackageDeclaration.class))
                              .map(ASTPackageDeclaration::getPackageNameImage).orElse("");

        Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.findChildrenOfType(ASTImportDeclaration.class).stream()
                                                                        .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

        top = EmptyScope.getInstance();

        if (!isImportOnDemand.get(true).isEmpty()) {
            // there are on-demand imports
            top = new ImportOnDemandScope(top, classLoader, isImportOnDemand.get(true), packageName);
        }

        top = new JavaLangScope(top, jdkVersion);

        top = new SamePackageScope(top, classLoader, packageName);

        if (!isImportOnDemand.get(false).isEmpty()) {
            // there are single imports
            top = new SingleImportScope(top, classLoader, isImportOnDemand.get(false), packageName);
        }

        node.setSymbolTable(top);
    }
}
