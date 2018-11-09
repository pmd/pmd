/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.scopes.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.symbols.refs.JClassReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JFieldReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JMethodReference;
import net.sourceforge.pmd.lang.java.symbols.refs.JSymbolicClassReference;
import net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;


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
public final class JSourceFileScope extends AbstractJScope {

    private static final Logger LOG = Logger.getLogger(ClassTypeResolver.class.getName());


    private final ASTCompilationUnit root;
    private final PMDASMClassLoader classLoader;

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
        this.classLoader = PMDASMClassLoader.getInstance(classLoader);

        List<ASTImportDeclaration> imports = root.findChildrenOfType(ASTImportDeclaration.class);

        List<String> staticImports = new ArrayList<>();

        // go through the imports
        for (ASTImportDeclaration anImportDeclaration : imports) {
            String strPackage = anImportDeclaration.getPackageName();
            if (anImportDeclaration.isStatic()) {
                if (anImportDeclaration.isImportOnDemand()) {
                    importOnDemandStaticClasses.add(JavaTypeDefinition.forClass(loadClass(strPackage)));
                } else { // not import on-demand
                    String strName = anImportDeclaration.getImportedName();
                    String fieldName = strName.substring(strName.lastIndexOf('.') + 1);

                    Class<?> staticClassWithField = loadClass(strPackage);
                    if (staticClassWithField != null) {
                        JavaTypeDefinition typeDef = getFieldType(JavaTypeDefinition.forClass(staticClassWithField),
                                                                  fieldName, currentAcu.getType());
                        staticFieldImageToTypeDef.put(fieldName, typeDef);
                    }

                    List<JavaTypeDefinition> typeList = staticNamesToClasses.get(fieldName);

                    if (typeList == null) {
                        typeList = new ArrayList<>();
                    }

                    typeList.add(JavaTypeDefinition.forClass(staticClassWithField));

                    staticNamesToClasses.put(fieldName, typeList);
                }
            } else { // non-static
                if (anImportDeclaration.isImportOnDemand()) {
                    importedOnDemand.add(strPackage);
                } else { // not import on-demand
                    String strName = anImportDeclaration.getImportedName();
                    importedClasses.put(strName, strName);
                    importedClasses.put(strName.substring(strPackage.length() + 1), strName);
                }
            }
        }

        // See https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5
        // for spec about import declarations

        for (ASTImportDeclaration anImport : imports) {

            String simpleName = anImport.getImportedSimpleName();
            String name = anImport.getImportedName();

            if (anImport.isImportOnDemand()) {
                if (anImport.isStatic()) {

                } else {
                    // Type-Import-on-Demand Declaration
                }


            } else {
                // imports a single name

                if (anImport.isStatic()) {
                    // Single-Static-Import Declaration
                    // fields or methods having the same name

                    String className = name.substring(0, name.lastIndexOf('.'));

                    Class<?> containerClass = loadClass(className);
                    if (containerClass == null) {
                        // these imports may not be found
                        importedTypes.put(simpleName, null);
                    } else {
                        importedTypes.put(simpleName, new JClassReference(this, containerClass));
                    }


                } else {
                    // Single-Type-Import Declaration
                    // https://docs.oracle.com/javase/specs/jls/se8/html/jls-7.html#jls-7.5.1
                    Class<?> loadedClass = loadClass(name);
                    if (loadedClass == null) {
                        // mark that the imported type wasn't resolved, but it's there!
                        // TODO Should we make a lazy ClassReference based on FQCN?
                        // The typeres visitor might have trouble with it, but typeIs() and TypeHelper would still be useful...
                        importedTypes.put(simpleName, null);
                    } else {
                        importedTypes.put(simpleName, new JClassReference(this, loadedClass));
                    }
                }
            }
        }

        Map<Boolean, List<ASTImportDeclaration>> splitOnDemand
                = imports.stream()
                         .collect(Collectors.partitioningBy(ASTImportDeclaration::isImportOnDemand));


    }


    @Override
    public Optional<JSymbolicClassReference> resolveTypeName(String simpleName) {
        return Optional.empty();
    }


    private Class<?> loadClass(String fullyQualifiedClassName) {
        try {
            return classLoader.loadClass(fullyQualifiedClassName);
            // ClassTypeResolver used to just ignore ClassNotFoundException, was there a reason for that?
        } catch (ClassNotFoundException | LinkageError e2) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Failed loading class " + fullyQualifiedClassName + "with an incomplete classpath.", e2);
            }
            return null;
        }
    }


    // loads imports-on-demand on-demand lel
    private class ImportOnDemandLoader {


    }


}
