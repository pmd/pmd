/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * <h2>Implementation</h3>
 *
 * <p>In PMD, program points are modelled as AST nodes. Each node has {@linkplain net.sourceforge.pmd.lang.java.ast.JavaNode#getSymbolTable()
 * a reference}
 * to the innermost enclosing symbol table which dominates it. Since each symbol table
 * has a reference to its parent, an AST node has in fact a reference to a whole <i>table stack</i>.
 * These references are resolved by a {@link net.sourceforge.pmd.lang.java.symbols.table.internal.SymbolTableResolver}
 * after parsing the file.
 *
 * <p>The following describes the most general form of the bottom part of any stack
 * (before the top-level type declaration), in increasing order of precedence:
 * <ul>
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.EmptySymbolTable}: Contains nothing. This is the shared root of all symbol table stacks, for
 *          implementation simplicity.
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.ImportOnDemandSymbolTable}: Types imported from a package or type by an import-on-demand,
 *          and static method or field names imported from a type by a static-import-on-demand;
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.JavaLangSymbolTable}: Top-level types implicitly imported from {@code java.lang};
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.SamePackageSymbolTable}: Top-level types from the same package, which are implicitly imported
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.SingleImportSymbolTable}: Types imported by single-type-imports, and static methods and
 *          fields imported by a single-static-import.
 *      <li>{@link net.sourceforge.pmd.lang.java.symbols.table.internal.MemberTypeSymTable}: Types declared in the compilation unit
 * </ul>
 * These dominate the whole compilation unit and thus are all linked to the {@link net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit}.
 *
 * <p>NOTE: this structure is not published API. Rule writers should not rely on it, and should only use this interface.
 *
 * <h2>Main differences with the current symbol table framework</h2>
 *
 * <ul>
 *     <li>Symbol tables resolve names.
 *     <li>Symbol tables don't index the AST like {@link net.sourceforge.pmd.lang.symboltable.Scope}s do. In fact, the structure of a scope
 *     stack doesn't aim to reflect the syntactic structure of the compilation unit, but to reify part
 *     of the semantics of name resolution.
 *     <li>The structure of table stacks is internal API, contrary to the structure of {@link net.sourceforge.pmd.lang.symboltable.Scope} trees.
 *     <li>Symbol tables don't store usages, a separate component could be used for that
 *     <li>{@link net.sourceforge.pmd.lang.java.symbols.JElementSymbol}s don't store a reference to their declaring SymbolTable
 *         like {@link net.sourceforge.pmd.lang.symboltable.NameDeclaration} does with {@link net.sourceforge.pmd.lang.symboltable.Scope}.
 *     <li>This is for now considered Java-specific and won't be abstracted into pmd-core until it's
 *     stable
 * </ul>
 *
 * <h2>Why not keep the current symbol table</h2>
 *
 * <p>The current symbol table framework was not built with the same goals in mind.
 * It indexes the AST to reduce it to a simpler representation, which is mostly
 * shortcuts to nodes. That representation hasn't proved very useful in rules,
 * which mostly only use it to resolve variable accesses.
 *
 * <p>The biggest issue is that it was not designed to abstract over whether we
 * have a node to represent a declaration or not. It can't work on reflection
 * data, and thus cannot really help type resolution, even if a good symbol table
 * would take the burden of resolving references off a type checker. The
 * shortcomings of the current symbol table make the current typeres duplicate
 * logic, and ultimately perform tasks that are not its responsibility,
 * which is probably why {@link net.sourceforge.pmd.lang.java.typeresolution.ClassTypeResolver} is so huge and nasty.
 *
 * <p>Having an abstraction layer to unify them allows the AST analyses to
 * be complementary, and rely on each other, instead of being so self-reliant.
 * The abstraction provided by {@link net.sourceforge.pmd.lang.java.symbols.JElementSymbol} may in the future be used
 * to build global indices of analysed projects to implement multifile analysis.
 *
 * <p>The goals of this rewrite should be:
 * <ul>
 *      <li>To make our language analyses passes share information and be complementary
 *      <li>To have a symbol table that is precise and exhaustive enough that all rules can depend on it
 *      <li>To modularize the system so that it's more easily testable and documentable
 * </ul>
 *
 *
 * <h2>TODO</h2>
 *
 * <p>The stack slice corresponding to a type declaration T will probably have the
 * following form:
 * <ul>
 *      <li> Superinterfaces: constants, member types, abstract and default methods
 *           inherited from the direct superinterfaces of T
 *      <li> Superclass: methods, fields, and member types inherited from
 *           the direct superclass of T
 *      <li> Member types: Member types of T (their names are shadowed by
 *           the type parameters of T, but they shadow any import)
 *      <li> Type parameters of T
 *      <li> T's static members: static methods, fields and types defined by T.
 *      <li> T's non-static members: static methods, fields and types defined by T.
 *      <li> Children symbol tables of T's body:
 *          <ul>
 *              <li>Each class initializer, lambda, anonymous class, member class,
 *                  constructor, or method declaration gets its own symbol table, which can
 *                  contain local classes, anonymous classes, etc, and the cycle continues
 *              <li>Static contexts (initializers, static methods, static nested classes)
 *                  have as parent the static members symbol table of T, because they can't access
 *                  T's non-static members. Non-static contexts are children to the non-static table,
 *                  which is itself child of the static table, so they can use all declarations.
 *          </ul>
 * </ul>
 *
 * <p>The trickiest point is the symbol tables for the supertypes of T. Obviously these also can inherit
 * members, and hence we'll have to resolve them recursively based on reflection data.
 *
 * <p>We can probably ruse to be able to share them across all analysed classes, but access control
 * will need to be taken into account. Probably we'll need two steps. Say we're building the symbol table
 * of a type T with supertype S:
 * <ul>
 *      <li>1. Resolve the declarations of S and organize them by access restrictions within an object shared
 *          across the analysis
 *      <li>2. Build a "view" of that shared object based on where T is (its package), which filters out
 *          inaccessible declarations from S, and add it to the inherited tables of T
 * </ul>
 * We'll probably need to proceed depth-first. If you're wondering about the performance costs of exploring
 * a whole type hierarchy, I'd say this is exactly what MissingOverrideRule does for now. It can then be
 * rewritten to make use of this framework. Plus, type hierarchies are unlikely to be extremely deep and correct
 * caching can make this ok performance-wise. Ideally this data would be cached between runs, which would
 * allow for multifile analysis. This can be scheduled as future work and could probably integrated into
 * the framework, which is made possible by the abstraction provided by {@link net.sourceforge.pmd.lang.java.symbols.JElementSymbol}.
 *
 * <p>Some other rules could use the symbol table stack, e.g. UnnecessaryQualifiedName, UnusedImports,
 * MissingOverride, etc.
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;
