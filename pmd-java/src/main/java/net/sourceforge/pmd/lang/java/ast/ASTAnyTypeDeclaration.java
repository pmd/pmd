/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;
import java.util.Locale;

import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;


/**
 * Groups class, enum, record, annotation and interface declarations.
 *
 * @author Clément Fournier
 */
public interface ASTAnyTypeDeclaration extends TypeNode, JavaQualifiableNode, AccessNode, JavaNode {

    /**
     * Returns the simple name of this type declaration. Returns null
     * if this is an anonymous class declaration.
     */
    // note that anonymous class declarations won't extend ASTAnyTypeDeclaration
    // until PMD 7.0.0 so this is @NonNull until then
    // @Nullable
    String getSimpleName();


    /**
     * @deprecated Use {@link #getSimpleName()}
     */
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@SimpleName")
    @Override
    String getImage();


    /**
     * Returns the binary name of this type declaration. This
     * is like {@link Class#getName()}.
     */
    // @NotNull
    String getBinaryName();


    /**
     * Finds the type kind of this declaration.
     *
     * @return The type kind of this declaration.
     *
     * @deprecated See {@link TypeKind}
     */
    @Deprecated
    TypeKind getTypeKind();


    /**
     * Retrieves the member declarations (fields, methods, classes, etc.) from the body of this type declaration.
     *
     * @return The member declarations declared in this type declaration
     */
    List<ASTAnyTypeBodyDeclaration> getDeclarations();


    /**
     * @deprecated Use {@link #getBinaryName()}
     */
    @Override
    @Deprecated
    JavaTypeQualifiedName getQualifiedName();


    /**
     * Returns true if this type declaration is nested inside an interface, class or annotation.
     */
    boolean isNested();


    /**
     * The kind of type this node declares.
     *
     * @deprecated This is not useful, not adapted to the problem, and
     *     does not scale to changes in the Java language. The only use
     *     of this is to get a name, this can be replaced with {@link PrettyPrintingUtil}.
     *
     *     <p>Besides, the real problem is that
     *     <ul>
     *         <li>enums are also classes
     *         <li>annotations are also interfaces
     *         <li>there are also anonymous classes in PMD 7.0, so this
     *         cannot even be used to downcast safely
     *     </ul>
     *     We can also expect new kinds of type declarations (eg records)
     *     in the future, which will force us to add new constants and aggravates
     *     the problem.
     *
     *     Ultimately, dividing "kinds" with an enum is not adapted.
     *
     *     Same problem with {@link ASTAnyTypeBodyDeclaration.DeclarationKind}
     */
    @Deprecated
    enum TypeKind {
        CLASS, INTERFACE, ENUM, ANNOTATION, RECORD;


        public String getPrintableName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
