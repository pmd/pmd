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
import net.sourceforge.pmd.lang.java.symbols.scopes.JScope;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;


/**
 * Scope for single imports. Has the highest precedence among imports.
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
     * @param loader        ClassLoader used to resolve static imports
     * @param singleImports Import declarations, must not be on-demand!
     * @param thisPackage   Package name of the current compilation unit, used to check for accessibility
     */
    public SingleImportScope(JScope parent, PMDASMClassLoader loader, List<ASTImportDeclaration> singleImports, String thisPackage) {
        super(parent, loader, thisPackage);

        for (ASTImportDeclaration anImport : singleImports) {
            if (anImport.isImportOnDemand()) {
                throw new IllegalArgumentException();
            }

            String simpleName = anImport.getImportedSimpleName();
            String name = anImport.getImportedName();

            if (anImport.isStatic()) {
                // Single-Static-Import Declaration
                // fields or methods having the same name

                String className = name.substring(0, name.lastIndexOf('.'));

                Class<?> containerClass = loadClass(className);
                if (containerClass != null) {

                    List<JMethodReference> methods = Arrays.stream(containerClass.getDeclaredMethods())
                                                           .filter(m -> Modifier.isStatic(m.getModifiers()))
                                                           .filter(this::isAccessible)
                                                           .filter(m -> m.getName().equals(simpleName))
                                                           .map(JMethodReference::new)
                                                           .collect(Collectors.toList());

                    importedStaticMethods.put(simpleName, methods);

                    // check for fields

                    try {
                        Field field = containerClass.getDeclaredField(simpleName);
                        if (field != null && Modifier.isStatic(field.getModifiers())) {
                            importedStaticFields.put(simpleName, new JFieldReference(field));
                        }
                    } catch (NoSuchFieldException e) {
                        // ignore eh
                    }
                }

                // containerClass==null, the imports cannot be found

            } else {
                // Single-Type-Import Declaration

                importedTypes.put(simpleName, new JSymbolicClassReference(
                        // FIXME the qualifiedname resolver should resolve this itself
                        (JavaTypeQualifiedName) QualifiedNameFactory.ofString(name, classLoader)));
            }
        }
    }


    @Override
    protected Logger getLogger() {
        return LOG;
    }


}
