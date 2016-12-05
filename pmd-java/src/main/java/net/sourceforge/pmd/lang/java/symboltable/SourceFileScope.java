/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * This scope is the outer most scope of a Java file. A Source File can contain
 * one ore more classes.
 */
public class SourceFileScope extends AbstractJavaScope {

    private final String packageImage;
    private final TypeSet types;

    public SourceFileScope(final ClassLoader classLoader) {
        this(classLoader, "");
    }

    public SourceFileScope(final ClassLoader classLoader, final String packageImage) {
        this.types = new TypeSet(classLoader);
        this.packageImage = packageImage;
        types.setASTCompilationUnitPackage(packageImage);
    }

    /**
     * Configures the type resolution for the symbol table.
     * 
     * @param classLoader
     *            the class loader to use to find additional classes
     * @param imports
     *            the import declarations
     */
    public void configureImports(final List<ASTImportDeclaration> imports) {
        for (ASTImportDeclaration i : imports) {
            if (i.isImportOnDemand()) {
                types.addImport(i.getImportedName() + ".*");
            } else {
                types.addImport(i.getImportedName());
            }
        }
    }

    public Set<String> getExplicitImports() {
        return types != null ? types.getExplicitImports() : Collections.<String>emptySet();
    }

    /**
     * Whether an auxclasspath has been configured or not. This can be used to
     * enable/disable more detailed symbol table analysis and type resolution
     * can be used - or to fall back to more simple implementation.
     * 
     * @return <code>true</code> if the auxclasspath is configured and types can
     *         be resolved reliably.
     * @see #resolveType(String)
     */
    public boolean hasAuxclasspath() {
        return types.hasAuxclasspath();
    }

    /**
     * Tries to resolve a class by name.
     * 
     * @param name
     *            the name of the class
     * @return the class or <code>null</code> if no class could be found
     */
    public Class<?> resolveType(String name) {
        return types.findClass(name);
    }

    public String getPackageName() {
        return packageImage;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException
     *             if declaration is not a {@link ClassNameDeclaration}
     */
    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (!(declaration instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException("A SourceFileScope can only contain classes.");
        }
        super.addDeclaration(declaration);
    }

    /**
     * Convenience method that casts the declarations to
     * {@link ClassNameDeclaration}s.
     * 
     * @see #getDeclarations()
     * @return all class name declarations
     */
    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return getDeclarations(ClassNameDeclaration.class);
    }

    public String toString() {
        return "SourceFileScope: " + glomNames(getClassDeclarations().keySet());
    }

    public ClassNameDeclaration findClassNameDeclaration(String name) {
        ImageFinderFunction finder = new ImageFinderFunction(name);
        Applier.apply(finder, getClassDeclarations().keySet().iterator());
        return (ClassNameDeclaration) finder.getDecl();
    }

    protected Set<NameDeclaration> findVariableHere(JavaNameOccurrence occ) {
        Set<NameDeclaration> result = new HashSet<>();
        ImageFinderFunction finder = new ImageFinderFunction(occ.getImage());
        Applier.apply(finder, getDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }
        return result;
    }

    /**
     * Returns a set of all types defined within this source file. This includes
     * all top-level types and nested types.
     * 
     * @return set of all types in this source file.
     */
    public Map<String, Node> getQualifiedTypeNames() {
        return getSubTypes(null, this);
    }

    private Map<String, Node> getSubTypes(String qualifyingName, Scope subType) {
        Map<String, Node> types = new HashMap<>();
        for (ClassNameDeclaration c : subType.getDeclarations(ClassNameDeclaration.class).keySet()) {
            String typeName = c.getName();
            if (qualifyingName != null) {
                typeName = qualifyingName + "." + typeName;
            }
            types.put(typeName, c.getNode());
            types.putAll(getSubTypes(typeName, c.getScope()));
        }
        return types;
    }
}
