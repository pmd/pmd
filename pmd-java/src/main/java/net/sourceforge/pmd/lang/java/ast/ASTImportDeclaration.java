/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents an import declaration in a Java file.
 *
 * <pre>
 *
 * ImportDeclaration ::= "import" "static"? {@linkplain ASTName Name} ( "." "*" )? ";"
 *
 * </pre>
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-7.html#jls-7.5">JLS 7.5</a>
 */
// TODO should this really be a type node?
// E.g. for on-demand imports, what's the type of this node? There's no type name, just a package name
// for on-demand static imports?
// for static imports of a field? the type of the field or the type of the enclosing type?
// for static imports of a method?
// I don't think we can work out a spec without surprising corner cases, and #1207 will abstract
// things away anyway, so I think we should make it a regular node
public class ASTImportDeclaration extends AbstractJavaTypeNode {

    private boolean isImportOnDemand;
    private boolean isStatic;
    private Package pkg;

    @InternalApi
    @Deprecated
    public ASTImportDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTImportDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * @deprecated Will be made private with 7.0.0
     */
    @InternalApi
    @Deprecated
    public void setImportOnDemand() {
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


    /**
     * @deprecated Will be made private with 7.0.0
     */
    @InternalApi
    @Deprecated
    public void setStatic() {
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
     * @deprecated this will be removed with PMD 7.0.0
     */
    @Deprecated
    public ASTName getImportedNameNode() {
        return (ASTName) getChild(0);
    }


    /**
     * Returns the full name of the import. For on-demand imports, this is the name without
     * the final dot and asterisk.
     */
    public String getImportedName() {
        return getChild(0).getImage();
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
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @InternalApi
    @Deprecated
    public void setPackage(Package packge) {
        this.pkg = packge;
    }


    /**
     * Returns the {@link Package} instance representing the package of the
     * type or method imported by this declaration. This may be null if the
     * auxclasspath is not correctly set, as this method depends on correct
     * type resolution.
     *
     * @deprecated this will be removed with PMD 7.0.0
     */
    @Deprecated
    public Package getPackage() {
        return this.pkg;
    }
}
