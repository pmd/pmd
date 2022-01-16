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
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
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

public class UnnecessaryImportRule extends AbstractJavaRule {
    // todo: java lang imports may be necessary if they're shadowed by a
    //  member of the same package.

    private static final String UNUSED_IMPORT_MESSAGE = "Unused import ''{0}''";
    private static final String DUPLICATE_IMPORT_MESSAGE = "Duplicate import ''{0}''";
    private static final String IMPORT_FROM_SAME_PACKAGE_MESSAGE = "Unnecessary import from the current package ''{0}''";
    private static final String IMPORT_FROM_JAVA_LANG_MESSAGE = "Unnecessary import from the java.lang package ''{0}''";

    private final Set<ImportWrapper> staticImports = new HashSet<>();
    private final Set<ImportWrapper> singleImports = new HashSet<>();
    private final Set<ImportWrapper> importsOnDemand = new HashSet<>();
    private final Set<ImportWrapper> staticImportsOnDemand = new HashSet<>();
    private final Set<ImportWrapper> unnecessaryJavaLangImports = new HashSet<>();
    private final Set<ImportWrapper> unnecessaryImportsFromSamePackage = new HashSet<>();
    private String thisPackageName;

    /*
     * Patterns to match the following constructs:
     *
     * @see package.class#member(param, param) label {@linkplain
     * package.class#member(param, param) label} {@link
     * package.class#member(param, param) label} {@link package.class#field}
     * {@value package.class#field}
     *
     * @throws package.class label
     * @exception package.class label
     */
    private static final Pattern SEE_PATTERN = Pattern
        .compile("@see\\s+((?:\\p{Alpha}\\w*\\.)*(?:\\p{Alpha}\\w*))?(?:#\\w*(?:\\(([.\\w\\s,\\[\\]]*)\\))?)?");

    private static final Pattern LINK_PATTERNS = Pattern
        .compile("\\{@link(?:plain)?\\s+((?:\\p{Alpha}\\w*\\.)*(?:\\p{Alpha}\\w*))?(?:#\\w*(?:\\(([.\\w\\s,\\[\\]]*)\\))?)?[\\s\\}]");

    private static final Pattern VALUE_PATTERN = Pattern.compile("\\{@value\\s+(\\p{Alpha}\\w*)[\\s#\\}]");

    private static final Pattern THROWS_PATTERN = Pattern.compile("@throws\\s+(\\p{Alpha}\\w*)");

    private static final Pattern EXCEPTION_PATTERN = Pattern.compile("@exception\\s+(\\p{Alpha}\\w*)");

    private static final Pattern[] PATTERNS = { SEE_PATTERN, LINK_PATTERNS, VALUE_PATTERN, THROWS_PATTERN, EXCEPTION_PATTERN };

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        this.singleImports.clear();
        this.staticImports.clear();
        this.staticImportsOnDemand.clear();
        this.importsOnDemand.clear();
        this.unnecessaryJavaLangImports.clear();
        this.unnecessaryImportsFromSamePackage.clear();
        this.thisPackageName = node.getPackageName();

        for (ASTImportDeclaration importDecl : node.children(ASTImportDeclaration.class)) {
            visitImport(importDecl, data);
        }

        for (ImportWrapper wrapper : singleImports) {
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
        for (ImportWrapper wrapper : singleImports) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }

        for (ImportWrapper wrapper : staticImports) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }
        for (ImportWrapper wrapper : staticImportsOnDemand) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }
        for (ImportWrapper wrapper : importsOnDemand) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }

        // remove unused ones, they have already been reported
        unnecessaryJavaLangImports.removeAll(singleImports);
        unnecessaryJavaLangImports.removeAll(importsOnDemand);
        unnecessaryImportsFromSamePackage.removeAll(singleImports);
        unnecessaryImportsFromSamePackage.removeAll(importsOnDemand);
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
        for (Comment comment : node.getComments()) {
            if (!(comment instanceof FormalComment)) {
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

                    if (singleImports.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    private void visitImport(ASTImportDeclaration node, Object data) {
        if (thisPackageName.equals(node.getPackageName())) {
            unnecessaryImportsFromSamePackage.add(new ImportWrapper(node));
        }

        Set<ImportWrapper> container =
              node.isStatic() && node.isImportOnDemand() ? staticImportsOnDemand
            : node.isStatic() ? staticImports
            : node.isImportOnDemand() ? importsOnDemand
            : singleImports;

        if (!container.add(new ImportWrapper(node))) {
            // duplicate
            reportWithMessage(node, data, DUPLICATE_IMPORT_MESSAGE);
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
                                     boolean isStatic) {
        while (scopeIter.hasNext()) {
            scopeIter.next();
            // must be the first result
            // todo make sure new Outer().new Inner() does not mark Inner as used
            if (containsTarget.test(scopeIter.getResults())) {
                // We found the declaration bringing the symbol in scope
                // If it's an import, then it's used. However, maybe it's from java.lang.

                if (scopeIter.getScopeTag() == ScopeInfo.SINGLE_IMPORT) {
                    Set<ImportWrapper> container = isStatic ? staticImports : singleImports;
                    container.removeIf(it -> symbol.getSimpleName().equals(it.node.getImportedSimpleName()));

                } else if (scopeIter.getScopeTag() == ScopeInfo.IMPORT_ON_DEMAND) {

                    Set<ImportWrapper> container = isStatic ? staticImportsOnDemand : importsOnDemand;
                    container.removeIf(it -> {
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
                                return true;
                            }
                            // maybe we're importing a subclass of the container.
                            TypeSystem ts = symbolOwner.getTypeSystem();
                            JClassSymbol importedContainer = ts.getClassSymbol(it.node.getImportedName());
                            if (importedContainer != null) {
                                return TypeTestUtil.isA(ts.rawType(symbolOwner), ts.rawType(importedContainer));
                            } else {
                                // insufficient classpath, err towards FNs
                                return true;
                            }
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
        singleImports.removeIf(it -> expectedImport.equals(it.node.getImportedSimpleName()));
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
                && node.isImportOnDemand() == that.node.isImportOnDemand();
        }

        @Override
        public int hashCode() {
            return node.getImportedName().hashCode() * 31 + Boolean.hashCode(node.isImportOnDemand());
        }
    }
}
