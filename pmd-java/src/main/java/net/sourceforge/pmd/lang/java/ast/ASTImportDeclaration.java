/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an import declaration in a Java file.
 *
 * <pre class="grammar">
 *
 * ImportDeclaration ::= "import" "static"? Name ( "." "*" )? ";"
 *
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-7.html#jls-7.5">JLS 7.5</a>
 */
public final class ASTImportDeclaration extends AbstractJavaNode implements ASTTopLevelDeclaration {

    private boolean isImportOnDemand;
    private boolean isStatic;

    ASTImportDeclaration(int id) {
        super(id);
    }


    void setImportOnDemand() {
        isImportOnDemand = true;
    }


    // @formatter:off
    /**
     * Returns true if this is an import-on-demand declaration,
     * aka "wildcard import".
     *
     * <ul>
     *     <li>If this is a static import, then the imported names are those
     *     of the accessible static members of the named type;
     *     <li>Otherwise, the imported names are the names of the accessible types
     *     of the named type or named package.
     * </ul>
     */
    // @formatter:on
    public boolean isImportOnDemand() {
        return isImportOnDemand;
    }


    void setStatic() {
        isStatic = true;
    }


    /**
     * Returns true if this is a static import. If this import is not on-demand,
     * {@link #getImportedSimpleName()} returns the name of the imported member.
     */
    public boolean isStatic() {
        return isStatic;
    }


    /**
     * Returns the full name of the import. For on-demand imports, this is the name without
     * the final dot and asterisk.
     */
    public String getImportedName() {
        return super.getImage();
    }


    @Override
    public String getImage() {
        // the image was null before 7.0, best keep it that way
        return null;
    }

    /**
     * Returns the simple name of the type or method imported by this declaration.
     * For on-demand imports, returns {@code null}.
     */
    public String getImportedSimpleName() {
        if (isImportOnDemand) {
            return null;
        }

        String importName = getImportedName();
        return importName.substring(importName.lastIndexOf('.') + 1);
    }


    /**
     * Returns the "package" prefix of the imported name. For type imports, including on-demand
     * imports, this is really the package name of the imported type(s). For static imports,
     * this is actually the qualified name of the enclosing type, including the type name.
     */
    public String getPackageName() {
        String importName = getImportedName();
        if (isImportOnDemand) {
            return importName;
        }
        if (importName.indexOf('.') == -1) {
            return "";
        }
        int lastDot = importName.lastIndexOf('.');
        return importName.substring(0, lastDot);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
