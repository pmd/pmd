/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.JavaComment;
import net.sourceforge.pmd.lang.java.ast.JavadocComment;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Detects unnecessary imports.
 *
 * <p>For PMD 7 I had hoped this rule could be rewritten to use the
 * symbol table implementation directly instead of reimplementing a
 * symbol table (with less care). This would be good for performance
 * and correctness. Modifying the symbol table chain to track which
 * import is used is hard though, mostly because the API to expose
 * is unclear (we wouldn't want symbol tables to expose a mutable API).
 */
public class UnnecessaryImportRule extends AbstractJavaRule {

    private static final String UNUSED_IMPORT_MESSAGE = "Unused import ''{0}''";
    private static final String UNUSED_STATIC_IMPORT_MESSAGE = "Unused static import ''{0}''";
    private static final String DUPLICATE_IMPORT_MESSAGE = "Duplicate import ''{0}''";
    private static final String IMPORT_FROM_SAME_PACKAGE_MESSAGE = "Unnecessary import from the current package ''{0}''";
    private static final String IMPORT_FROM_JAVA_LANG_MESSAGE = "Unnecessary import from the java.lang package ''{0}''";

    private final Set<ImportWrapper> staticImports = new HashSet<>();
    private final Set<ImportWrapper> allSingleNameImports = new HashSet<>();
    private final Set<ImportWrapper> allImportsOnDemand = new HashSet<>();
    private final Set<ImportWrapper> staticImportsOnDemand = new HashSet<>();
    private final Set<ImportWrapper> unnecessaryJavaLangImports = new HashSet<>();
    private final Set<ImportWrapper> unnecessaryImportsFromSamePackage = new HashSet<>();

    /*
     * Patterns to match the following constructs:
     *
     * @see package.class#member(param, param) label
     * {@linkplain package.class#member(param, param) label}
     * {@link package.class#member(param, param) label}
     * {@link package.class#field}
     * {@value package.class#field}
     *
     * @throws package.class label
     * @exception package.class label
     */

    /* package.class#member(param, param) */
    private static final String TYPE_PART_GROUP = "((?:\\p{Alpha}\\w*\\.)*(?:\\p{Alpha}\\w*))?(?:#\\w*(?:\\(([.\\w\\s,\\[\\]]*)\\))?)?";

    private static final Pattern SEE_PATTERN = Pattern.compile("@see\\s+" + TYPE_PART_GROUP);


    private static final Pattern LINK_PATTERNS = Pattern.compile("\\{@link(?:plain)?\\s+" + TYPE_PART_GROUP + "[\\s\\}]");

    private static final Pattern VALUE_PATTERN = Pattern.compile("\\{@value\\s+(\\p{Alpha}\\w*)[\\s#\\}]");

    private static final Pattern THROWS_PATTERN = Pattern.compile("@throws\\s+(\\p{Alpha}\\w*)");

    private static final Pattern EXCEPTION_PATTERN = Pattern.compile("@exception\\s+(\\p{Alpha}\\w*)");

    /* // @link substring="a" target="package.class#member(param, param)" */
    private static final Pattern LINK_IN_SNIPPET = Pattern
        .compile("//\\s*@link\\s+(?:.*?)?target=[\"']?" + TYPE_PART_GROUP + "[\"']?");

    private static final Pattern[] PATTERNS = { SEE_PATTERN, LINK_PATTERNS, VALUE_PATTERN, THROWS_PATTERN, EXCEPTION_PATTERN, LINK_IN_SNIPPET };

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        this.allSingleNameImports.clear();
        this.staticImports.clear();
        this.staticImportsOnDemand.clear();
        this.allImportsOnDemand.clear();
        this.unnecessaryJavaLangImports.clear();
        this.unnecessaryImportsFromSamePackage.clear();
        String packageName = node.getPackageName();

        for (ASTImportDeclaration importDecl : node.children(ASTImportDeclaration.class)) {
            visitImport(importDecl, data, packageName);
        }

        for (ImportWrapper wrapper : allSingleNameImports) {
            if ("java.lang".equals(wrapper.node.getPackageName())) {
                if (!isJavaLangImportNecessary(node, wrapper)) {
                    // the import is not shadowing something
                    unnecessaryJavaLangImports.add(wrapper);
                }
            }
        }

        super.visit(node, data);
        visitComments(node);

        doReporting(data);

        return data;
    }

    private void doReporting(Object data) {
        for (ImportWrapper wrapper : allSingleNameImports) {
            String message = wrapper.isStatic() ? UNUSED_STATIC_IMPORT_MESSAGE : UNUSED_IMPORT_MESSAGE;
            reportWithMessage(wrapper.node, data, message);
        }
        for (ImportWrapper wrapper : allImportsOnDemand) {
            String message = wrapper.isStatic() ? UNUSED_STATIC_IMPORT_MESSAGE : UNUSED_IMPORT_MESSAGE;
            reportWithMessage(wrapper.node, data, message);
        }

        // remove unused ones, they have already been reported
        unnecessaryJavaLangImports.removeAll(allSingleNameImports);
        unnecessaryJavaLangImports.removeAll(allImportsOnDemand);
        unnecessaryImportsFromSamePackage.removeAll(allSingleNameImports);
        unnecessaryImportsFromSamePackage.removeAll(allImportsOnDemand);
        for (ImportWrapper wrapper : unnecessaryJavaLangImports) {
            reportWithMessage(wrapper.node, data, IMPORT_FROM_JAVA_LANG_MESSAGE);
        }
        for (ImportWrapper wrapper : unnecessaryImportsFromSamePackage) {
            reportWithMessage(wrapper.node, data, IMPORT_FROM_SAME_PACKAGE_MESSAGE);
        }
    }

    private boolean isJavaLangImportNecessary(ASTCompilationUnit node, ImportWrapper wrapper) {
        ShadowChainIterator<JTypeMirror, ScopeInfo> iter =
            node.getSymbolTable().types().iterateResults(wrapper.node.getImportedSimpleName());
        if (iter.hasNext()) {
            iter.next();
            if (iter.getScopeTag() == ScopeInfo.SINGLE_IMPORT) {
                if (iter.hasNext()) {
                    iter.next();
                    // the import is shadowing something else
                    return iter.getScopeTag() != ScopeInfo.JAVA_LANG;
                }
            }
        }
        return false;
    }

    private void visitComments(ASTCompilationUnit node) {
        // todo improve that when we have a javadoc parser
        for (JavaComment comment : node.getComments()) {
            if (!(comment instanceof JavadocComment)) {
                continue;
            }
            for (Pattern p : PATTERNS) {
                Matcher m = p.matcher(comment.getImage());
                while (m.find()) {
                    String fullname = m.group(1);

                    if (fullname != null) { // may be null for "@see #" and "@link #"
                        removeReferenceSingleImport(fullname);
                    }

                    if (m.groupCount() > 1) {
                        fullname = m.group(2);
                        if (fullname != null) {
                            for (String param : fullname.split("\\s*,\\s*")) {
                                removeReferenceSingleImport(param);
                            }
                        }
                    }

                    if (allSingleNameImports.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    private void visitImport(ASTImportDeclaration node, Object data, String thisPackageName) {
        if (thisPackageName.equals(node.getPackageName())) {
            unnecessaryImportsFromSamePackage.add(new ImportWrapper(node));
        }

        Set<ImportWrapper> container =
            node.isImportOnDemand() ? allImportsOnDemand
                                    : allSingleNameImports;


        if (!container.add(new ImportWrapper(node))) {
            // duplicate
            reportWithMessage(node, data, DUPLICATE_IMPORT_MESSAGE);
        }

        if (node.isStatic()) {
            container = node.isImportOnDemand() ? staticImportsOnDemand
                                                : staticImports;
            container.add(new ImportWrapper(node));
        }
    }

    private void reportWithMessage(ASTImportDeclaration node, Object data, String message) {
        addViolationWithMessage(data, node, message, new String[] { PrettyPrintingUtil.prettyImport(node) });
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (node.getQualifier() == null
            && !node.isFullyQualified()
            && node.getTypeMirror().isClassOrInterface()) {

            JClassSymbol symbol = ((JClassType) node.getTypeMirror()).getSymbol();
            ShadowChainIterator<JTypeMirror, ScopeInfo> scopeIter =
                node.getSymbolTable().types().iterateResults(node.getSimpleName());
            checkScopeChain(false, symbol, scopeIter, ts -> true, false);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (node.getQualifier() == null) {
            OverloadSelectionResult overload = node.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null; // todo we're erring towards FPs
            }

            ShadowChainIterator<JMethodSig, ScopeInfo> scopeIter =
                node.getSymbolTable().methods().iterateResults(node.getMethodName());


            JExecutableSymbol symbol = overload.getMethodType().getSymbol();
            checkScopeChain(true,
                            symbol,
                            scopeIter,
                            methods -> CollectionUtil.any(methods, m -> m.getSymbol().equals(symbol)),
                            true);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        JVariableSymbol sym = node.getReferencedSym();
        if (sym != null
            && sym.isField()
            && ((JFieldSymbol) sym).isStatic()) {

            if (node.getParent() instanceof ASTSwitchLabel
                && node.ancestors(ASTSwitchLike.class).take(1).any(ASTSwitchLike::isEnumSwitch)) {
                // special scoping rules, see JSymbolTable#variables doc
                return null;
            }

            ShadowChainIterator<JVariableSig, ScopeInfo> scopeIter = node.getSymbolTable().variables().iterateResults(node.getName());
            checkScopeChain(false, (JFieldSymbol) sym, scopeIter, ts -> true, true);
        }
        return null;
    }

    private <T> void checkScopeChain(boolean recursive,
                                     JAccessibleElementSymbol symbol,
                                     ShadowChainIterator<T, ScopeInfo> scopeIter,
                                     Predicate<List<T>> containsTarget,
                                     boolean onlyStatic) {
        while (scopeIter.hasNext()) {
            scopeIter.next();
            // must be the first result
            // todo make sure new Outer().new Inner() does not mark Inner as used
            if (containsTarget.test(scopeIter.getResults())) {
                // We found the declaration bringing the symbol in scope
                // If it's an import, then it's used. However, maybe it's from java.lang.

                if (scopeIter.getScopeTag() == ScopeInfo.SINGLE_IMPORT) {

                    allSingleNameImports.removeIf(
                        it -> (it.isStatic() || !onlyStatic)
                            && symbol.getSimpleName().equals(it.node.getImportedSimpleName())
                    );

                } else if (scopeIter.getScopeTag() == ScopeInfo.IMPORT_ON_DEMAND) {

                    allImportsOnDemand.removeIf(it -> {
                        if (!it.isStatic() && onlyStatic) {
                            return false;
                        }
                        // This is the class that contains the symbol
                        // we're looking for.
                        // We have to test whether this symbol is contained
                        // by the imported type or package.
                        JClassSymbol symbolOwner = symbol.getEnclosingClass();
                        if (symbolOwner == null) {
                            // package import on demand
                            return it.node.getImportedName().equals(symbol.getPackageName());
                        } else {
                            if (it.node.getImportedName().equals(symbolOwner.getCanonicalName())) {
                                // importing the container directly
                                return it.isStatic() == symbol.isStatic();
                            }
                            // maybe we're importing a subclass of the container.
                            TypeSystem ts = symbolOwner.getTypeSystem();
                            JClassSymbol importedContainer = ts.getClassSymbol(it.node.getImportedName());
                            return importedContainer == null // insufficient classpath, err towards FNs
                                    || TypeTestUtil.isA(ts.rawType(symbolOwner), ts.rawType(importedContainer));
                        }
                    });
                }
                return;
            }
            if (!recursive) {
                break;
            }
        }
        // unknown reference
    }


    /** We found a reference to the type given by the name. */
    private void removeReferenceSingleImport(String referenceName) {
        String expectedImport = StringUtils.substringBefore(referenceName, ".");
        allSingleNameImports.removeIf(it -> expectedImport.equals(it.node.getImportedSimpleName()));
    }

    /** Override the equal behaviour of ASTImportDeclaration to put it into a set. */
    private static final class ImportWrapper {

        private final ASTImportDeclaration node;

        private ImportWrapper(ASTImportDeclaration node) {
            this.node = node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }
            ImportWrapper that = (ImportWrapper) o;
            return node.getImportedName().equals(that.node.getImportedName())
                && node.isImportOnDemand() == that.node.isImportOnDemand()
                && this.isStatic() == that.isStatic();
        }

        @Override
        public int hashCode() {
            return node.getImportedName().hashCode() * 31
                + Boolean.hashCode(node.isStatic())
                + 37 * Boolean.hashCode(node.isImportOnDemand());
        }

        private boolean isStatic() {
            return this.node.isStatic();
        }
    }
}
