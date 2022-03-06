/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver;


/**
 * The root node of all Java ASTs.
 *
 * <pre class="grammar">
 *
 * CompilationUnit ::= RegularCompilationUnit
 *                   | ModularCompilationUnit
 *
 * RegularCompilationUnit ::=
 *   {@linkplain ASTPackageDeclaration PackageDeclaration}?
 *   {@linkplain ASTImportDeclaration ImportDeclaration}*
 *   {@linkplain ASTAnyTypeDeclaration TypeDeclaration}*
 *
 * ModularCompilationUnit ::=
 *   {@linkplain ASTImportDeclaration ImportDeclaration}*
 *   {@linkplain ASTModuleDeclaration ModuleDeclaration}
 *
 * </pre>
 */
public final class ASTCompilationUnit extends AbstractJavaNode implements JavaNode, GenericNode<JavaNode>, RootNode {

    private LazyTypeResolver lazyTypeResolver;
    private List<Comment> comments;
    private AstInfo<ASTCompilationUnit> astInfo;

    ASTCompilationUnit(int id) {
        super(id);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setAstInfo(AstInfo<ASTCompilationUnit> task) {
        this.astInfo = task;
    }

    @Override
    public AstInfo<ASTCompilationUnit> getAstInfo() {
        return astInfo;
    }

    void setComments(List<Comment> comments) {
        this.comments = comments;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the package declaration, if there is one.
     */
    public @Nullable ASTPackageDeclaration getPackageDeclaration() {
        return AstImplUtil.getChildAs(this, 0, ASTPackageDeclaration.class);
    }

    @Override
    public @NonNull ASTCompilationUnit getRoot() {
        return this;
    }

    /**
     * Returns the package name of this compilation unit. If there is no
     * package declaration, then returns the empty string.
     */
    public @NonNull String getPackageName() {
        ASTPackageDeclaration pack = getPackageDeclaration();
        return pack == null ? "" : pack.getName();
    }

    /**
     * Returns the top-level type declarations declared in this compilation
     * unit. This may be empty, eg if this a package-info.java, or a modular
     * compilation unit (but ordinary compilation units may also be empty).
     */
    public NodeStream<ASTAnyTypeDeclaration> getTypeDeclarations() {
        return children(ASTAnyTypeDeclaration.class);
    }

    @Override
    public @NonNull JSymbolTable getSymbolTable() {
        assert symbolTable != null : "Symbol table wasn't set";
        return symbolTable;
    }


    @Override
    public TypeSystem getTypeSystem() {
        assert lazyTypeResolver != null : "Type resolution not initialized";
        return lazyTypeResolver.getTypeSystem();
    }

    void setTypeResolver(LazyTypeResolver typeResolver) {
        this.lazyTypeResolver = typeResolver;
    }

    @NonNull LazyTypeResolver getLazyTypeResolver() {
        assert lazyTypeResolver != null : "Type resolution not initialized";
        return lazyTypeResolver;
    }

}
