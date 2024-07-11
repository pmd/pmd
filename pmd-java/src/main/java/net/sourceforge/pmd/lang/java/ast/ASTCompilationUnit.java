/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.ast.internal.LazyTypeResolver;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;


/**
 * The root node of all Java ASTs.
 *
 * <pre class="grammar">
 *
 * CompilationUnit ::= OrdinaryCompilationUnit
 *                   | UnnamedClassCompilationUnit
 *                   | ModularCompilationUnit
 *
 * RegularCompilationUnit ::=
 *   {@linkplain ASTPackageDeclaration PackageDeclaration}?
 *   {@linkplain ASTImportDeclaration ImportDeclaration}*
 *   {@linkplain ASTTypeDeclaration TypeDeclaration}*
 *
 * UnnamedClassCompilationUnit ::=
 *   {@linkplain ASTImportDeclaration ImportDeclaration}*
 *   {@linkplain ASTFieldDeclaration FieldDeclaration}*
 *   {@linkplain ASTMethodDeclaration MethodDeclaration}
 *   {@linkplain ASTBodyDeclaration BodyDeclaration}*
 *
 * ModularCompilationUnit ::=
 *   {@linkplain ASTImportDeclaration ImportDeclaration}*
 *   {@linkplain ASTModuleDeclaration ModuleDeclaration}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/477">JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)</a> (Java 23)
 * @see #isImplicitlyDeclaredClass()
 */
public final class ASTCompilationUnit extends AbstractJavaNode implements RootNode {

    private LazyTypeResolver lazyTypeResolver;
    private List<JavaComment> comments;
    private AstInfo<ASTCompilationUnit> astInfo;

    ASTCompilationUnit(int id) {
        super(id);
        setRoot(this);
    }

    public List<JavaComment> getComments() {
        return comments;
    }

    void setAstInfo(AstInfo<ASTCompilationUnit> task) {
        this.astInfo = task;
    }

    @Override
    public AstInfo<ASTCompilationUnit> getAstInfo() {
        return astInfo;
    }

    void setComments(List<JavaComment> comments) {
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
    public NodeStream<ASTTypeDeclaration> getTypeDeclarations() {
        return children(ASTTypeDeclaration.class);
    }

    /**
     * Returns the module declaration, if this is a modular compilation unit.
     */
    public @Nullable ASTModuleDeclaration getModuleDeclaration() {
        return firstChild(ASTModuleDeclaration.class);
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

    @Experimental("Implicitly Declared Classes and Instance Main Methods is a Java 22 / Java 23 Preview feature")
    @NoAttribute
    public boolean isImplicitlyDeclaredClass() {
        return children(ASTMethodDeclaration.class).nonEmpty();
    }
}
