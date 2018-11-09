/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;


/**
 * Scope for single imports. Has highest precedence for an import, and
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class SingleImportScope extends AbstractImportScope {

    private static final Logger LOG = Logger.getLogger(SingleImportScope.class.getName());


    /**
     * Creates a scope for single imports, linking it to its parent, which cares about
     * import on demand declarations.
     *
     * @param parent        Parent scope
     * @param singleImports Import declarations, must not be on-demand!
     */
    SingleImportScope(SamePackageScope parent, List<ASTImportDeclaration> singleImports) {
        super(parent);

        for (ASTImportDeclaration anImport : singleImports) {
            // imports a single name

            String simpleName = anImport.getImportedSimpleName();
            String name = anImport.getImportedName();

            if (anImport.isStatic()) {
                // Single-Static-Import Declaration
                // fields or methods having the same name

                String className = name.substring(0, name.lastIndexOf('.'));

                Class<?> containerClass = loadClass(className);
                if (containerClass != null) {

                    List<JMethodReference> methods = Arrays.stream(containerClass.getDeclaredMethods())
                                                           .filter(m -> m.getName().equals(simpleName))
                                                           .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                           .map(m -> new JMethodReference(this, m))
                                                           .collect(Collectors.toList());

                    importedStaticMethods.put(simpleName, methods);

                    try {
                        Field field = containerClass.getDeclaredField(simpleName);
                        // it must be static, was already checked by compiler
                        importedStaticFields.put(simpleName, new JFieldReference(this, field));
                    } catch (NoSuchFieldException e) {
                        // ignore eh
                    }
                }

                // else these imports cannot be found

            } else {
                // Single-Type-Import Declaration

                Class<?> loadedClass = loadClass(name);
                if (loadedClass == null) {
                    // use true symbolic reference
                    importedTypes.put(simpleName, new JSymbolicClassReference(this,
                                                                              // FIXME the qualifiedname resolver should resolve this itself
                                                                              (JavaTypeQualifiedName) QualifiedNameFactory.ofString(name)));
                } else {
                    importedTypes.put(simpleName, new JSymbolicClassReference(this, loadedClass));
                }
            }
        }
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


}
