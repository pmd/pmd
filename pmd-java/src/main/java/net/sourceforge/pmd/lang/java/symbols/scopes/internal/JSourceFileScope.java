/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symbols.refs.JClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;


/**
 * Scope corresponding to a source file. Manages the set of imports
 * of the file. Manages the type names from the same package.
 *
 * All types declared within a source file are also
 * accessible by simple name, but this is TODO
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JSourceFileScope extends AbstractJScope {

    private final ASTCompilationUnit root;

    private final ClassLoader classLoader;

    private final Map<String, JClassReference> importedTypes = new HashMap<>();
    private final Map<String, JMethodReference> importedStaticMethods = new HashMap<>();
    private final Map<String, JFieldReference> importedStaticField = new HashMap<>();


    /**
     * Creates a source file scope from an AST root.
     *
     * @param root AST root
     */
    JSourceFileScope(ASTCompilationUnit root, ClassLoader classLoader) {
        super(JavaLangScope.getInstance());
        this.root = root;
        this.classLoader = classLoader;

        List<ASTImportDeclaration> imports = root.findChildrenOfType(ASTImportDeclaration.class);

        List<String> staticImports = new ArrayList<>();

        for (ASTImportDeclaration anImport : imports) {
            if (anImport.isImportOnDemand()) {

            } else {
                // imports a single name

                if (anImport.isStatic()) {
                    // field or method
                } else {
                    // type
                    anImport.getImportedSimpleName();
                    anImport.getImportedName();

                }
            }


        }

        Map<Boolean, List<ASTImportDeclaration>> splitOnDemand
                = imports.stream()
                         .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));


    }


    @Override
    public Optional<JClassReference> resolveTypeName(String simpleName) {
        return Optional.empty();
    }


    // loads imports-on-demand on-demand lel
    private class ImportOnDemandLoader {


    }


}
