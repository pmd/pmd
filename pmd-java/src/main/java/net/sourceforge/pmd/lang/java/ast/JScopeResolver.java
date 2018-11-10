/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.AstAnalysisConfiguration;
import net.sourceforge.pmd.lang.java.symbols.scopes.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.ImportOnDemandSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SamePackageSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.scopes.internal.SingleImportSymbolTable;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JScopeResolver extends SideEffectingVisitorAdapter<AstAnalysisConfiguration> {

    private JSymbolTable top;
    private String packageName;
    private PMDASMClassLoader classLoader;


    @Override
    public void visit(ASTCompilationUnit node, AstAnalysisConfiguration data) {

        classLoader = PMDASMClassLoader.getInstance(data.getTypeResolutionClassLoader());

        Map<Boolean, List<ASTImportDeclaration>> isImportOnDemand = node.findChildrenOfType(ASTImportDeclaration.class).stream()
                                                                        .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));

        packageName = Optional.ofNullable(node.getFirstChildOfType(ASTPackageDeclaration.class))
                              .map(ASTPackageDeclaration::getPackageNameImage).orElse("");

        // assume there's an empty list

        if (!isImportOnDemand.get(true).isEmpty()) {
            // there are on-demand imports
            top = new ImportOnDemandSymbolTable(classLoader, isImportOnDemand.get(true), packageName);
        }

        top = new SamePackageSymbolTable(top, classLoader, packageName);

        if (!isImportOnDemand.get(false).isEmpty()) {
            // there are single imports
            top = new SingleImportSymbolTable(top, classLoader, isImportOnDemand.get(true), packageName);
        }

        node.setSymbolTable(top);
    }
}
