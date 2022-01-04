/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainIterator;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

public class UnnecessaryImportRule extends AbstractJavaRule {
    // todo: java lang imports may be necessary if they're shadowed by a
    //  member of the same package.

    private static final String UNUSED_IMPORT_MESSAGE = "Unused import ''{0}''";
    private static final String DUPLICATE_IMPORT_MESSAGE = "Duplicate import ''{0}''";
    private static final String IMPORT_FROM_SAME_PACKAGE_MESSAGE = "Unnecessary import from the current package ''{0}''";
    private static final String IMPORT_FROM_JAVA_LANG_MESSAGE = "Unnecessary import from the java.lang package ''{0}''";

    private final Set<ImportWrapper> singleImports = new HashSet<>();
    private final Set<ImportWrapper> importsOnDemand = new HashSet<>();
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
        this.thisPackageName = node.getPackageName();
        super.visit(node, data);
        visitComments(node);

        /*
         * special handling for Bug 2606609 : False "UnusedImports" positive in
         * package-info.java package annotations are processed before the import
         * clauses so they need to be examined again later on.
         */
        if (node.getNumChildren() > 0 && node.getChild(0) instanceof ASTPackageDeclaration) {
            visit((ASTPackageDeclaration) node.getChild(0), data);
        }
        for (ImportWrapper wrapper : singleImports) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }
        for (ImportWrapper wrapper : importsOnDemand) {
            reportWithMessage(wrapper.node, data, UNUSED_IMPORT_MESSAGE);
        }
        return data;
    }

    private void visitComments(ASTCompilationUnit node) {
        if (singleImports.isEmpty()) {
            return;
        }
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

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (thisPackageName.equals(node.getPackageName())) {
            // import for the same package
            reportWithMessage(node, data, IMPORT_FROM_SAME_PACKAGE_MESSAGE);
        }

        Set<ImportWrapper> container = node.isImportOnDemand() ? importsOnDemand : singleImports;
        if (!container.add(new ImportWrapper(node))) {
            // duplicate
            reportWithMessage(node, data, DUPLICATE_IMPORT_MESSAGE);
        }
        return data;
    }

    private void reportWithMessage(ASTImportDeclaration node, Object data, String message) {
        addViolationWithMessage(data, node, message, new String[] { PrettyPrintingUtil.prettyImport(node) });
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        if (node.getQualifier() == null && !node.isFullyQualified()) {
            checkType(node);
        }
        return super.visit(node, data);
    }

    /**
     * Remove the import wrapper that imports the name referenced by the
     * given node.
     */
    protected void checkType(ASTClassOrInterfaceType referenceNode) {

        if (!referenceNode.getTypeMirror().isClassOrInterface()) {
            return;
        }

        String simpleName = referenceNode.getSimpleName();
        JClassSymbol symbol = ((JClassType) referenceNode.getTypeMirror()).getSymbol();
        ShadowChainIterator<JTypeMirror, ScopeInfo> scopeIter = referenceNode.getSymbolTable().types().iterateResults(simpleName);
        if (scopeIter.hasNext()) {
            scopeIter.next();
            // must be the first result
            // todo make sure new Outer().new Inner() does not mark Inner as used
            List<JTypeMirror> results = scopeIter.getResults();
            if (results.contains(((JClassType) referenceNode.getTypeMirror()).getGenericTypeDeclaration())) {
                if (scopeIter.getScopeTag() == ScopeInfo.SINGLE_IMPORT) {
                    singleImports.removeIf(it -> simpleName.equals(it.node.getImportedSimpleName()));
                } else if (scopeIter.getScopeTag() == ScopeInfo.IMPORT_ON_DEMAND) {
                    importsOnDemand.removeIf(it -> {
                        JClassSymbol enclosing = symbol.getEnclosingClass();
                        if (enclosing == null) {
                            return symbol.getPackageName().equals(it.node.getImportedName());
                        } else {
                            return enclosing.getCanonicalName().equals(it.node.getImportedName());
                        }
                    });
                }
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
