/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.internal.ImportWrapper;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.bestpractices.UnusedImportsRule;

public class UnnecessaryImportRule extends AbstractJavaRule {
    // todo: java lang imports may be necessary if they're shadowed by a
    //  member of the same package.

    private static final String UNUSED_IMPORT_MESSAGE = "Unused import ''{0}''";
    private static final String DUPLICATE_IMPORT_MESSAGE = "Duplicate import ''{0}''";
    private static final String IMPORT_FROM_SAME_PACKAGE_MESSAGE = "Unnecessary import from the current package ''{0}''";
    private static final String IMPORT_FROM_JAVA_LANG_MESSAGE = "Unnecessary import from the java.lang package ''{0}''";

    private final Set<ImportWrapper> imports = new HashSet<>();
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
     */
    private static final Pattern SEE_PATTERN = Pattern
        .compile("@see\\s+((?:\\p{Alpha}\\w*\\.)*(?:\\p{Alpha}\\w*))?(?:#\\w*(?:\\(([.\\w\\s,\\[\\]]*)\\))?)?");

    private static final Pattern LINK_PATTERNS = Pattern
        .compile("\\{@link(?:plain)?\\s+((?:\\p{Alpha}\\w*\\.)*(?:\\p{Alpha}\\w*))?(?:#\\w*(?:\\(([.\\w\\s,\\[\\]]*)\\))?)?[\\s\\}]");

    private static final Pattern VALUE_PATTERN = Pattern.compile("\\{@value\\s+(\\p{Alpha}\\w*)[\\s#\\}]");

    private static final Pattern THROWS_PATTERN = Pattern.compile("@throws\\s+(\\p{Alpha}\\w*)");

    private static final Pattern[] PATTERNS = { SEE_PATTERN, LINK_PATTERNS, VALUE_PATTERN, THROWS_PATTERN };

    /**
     * The deprecated rule {@link UnusedImportsRule} extends this class
     * and overrides this.
     */
    protected boolean justReportUnusedImports() {
        return false;
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
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
        for (ImportWrapper wrapper : imports) {
            reportWithMessage(wrapper.getNode(), data, UNUSED_IMPORT_MESSAGE);
        }
        return data;
    }

    private void visitComments(ASTCompilationUnit node) {
        if (imports.isEmpty()) {
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

                    if (imports.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (thisPackageName.equals(node.getPackageName()) && !justReportUnusedImports()) {
            // import for the same package
            reportWithMessage(node, data, IMPORT_FROM_SAME_PACKAGE_MESSAGE);
        } else if (!imports.add(new ImportWrapper(node))) {
            if (!justReportUnusedImports()) {
                // duplicate
                reportWithMessage(node, data, DUPLICATE_IMPORT_MESSAGE);
            }
        }
        return data;
    }

    private void reportWithMessage(ASTImportDeclaration node, Object data, String message) {
        addViolationWithMessage(data, node, message, new String[] { PrettyPrintingUtil.prettyImport(node) });
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node, (RuleContext) data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        check(node, (RuleContext) data);
        return data;
    }

    /**
     * Remove the import wrapper that imports the name referenced by the
     * given node.
     */
    protected void check(Node referenceNode, RuleContext ruleCtx) {
        if (imports.isEmpty()) {
            return;
        }
        Pair<String, String> candidate = splitName(referenceNode);
        String candFullName = candidate.getLeft();
        String candName = candidate.getRight();

        // check exact imports
        Iterator<ImportWrapper> it = imports.iterator();
        while (it.hasNext()) {
            ImportWrapper i = it.next();
            if (!i.isStaticOnDemand() && i.matches(candFullName, candName)) {
                it.remove();

                if ("java.lang".equals(i.getPackageName()) && !justReportUnusedImports()) {
                    // import for the same package
                    reportWithMessage(i.getNode(), ruleCtx, IMPORT_FROM_JAVA_LANG_MESSAGE);
                }
                return;
            }
        }

        // check static on-demand imports
        it = imports.iterator();
        while (it.hasNext()) {
            ImportWrapper i = it.next();
            if (i.isStaticOnDemand() && i.matches(candFullName, candName)) {
                it.remove();
                return;
            }
        }

        if (referenceNode instanceof TypeNode && ((TypeNode) referenceNode).getType() != null) {
            Class<?> c = ((TypeNode) referenceNode).getType();
            if (c.getPackage() != null) {
                removeOnDemandForPackageName(c.getPackage().getName());
            }
        }
    }


    protected Pair<String, String> splitName(Node node) {
        String fullName = node.getImage();
        String name;
        int firstDot = node.getImage().indexOf('.');
        if (firstDot == -1) {
            name = node.getImage();
        } else {
            // ASTName could be: MyClass.MyConstant
            // name -> MyClass
            // fullName -> MyClass.MyConstant
            name = node.getImage().substring(0, firstDot);
            if (isMethodCall(node)) {
                // ASTName could be: MyClass.MyConstant.method(a, b)
                // name -> MyClass
                // fullName -> MyClass.MyConstant
                fullName = node.getImage().substring(0, node.getImage().lastIndexOf('.'));
            }
        }

        return Pair.of(fullName, name);
    }

    private boolean isMethodCall(Node node) {
        // PrimaryExpression
        //     PrimaryPrefix
        //         Name
        //     PrimarySuffix

        if (node.getParent() instanceof ASTPrimaryPrefix && node.getNthParent(2) instanceof ASTPrimaryExpression) {
            Node primaryPrefix = node.getParent();
            Node expression = primaryPrefix.getParent();

            boolean hasNextSibling = expression.getNumChildren() > primaryPrefix.getIndexInParent() + 1;
            if (hasNextSibling) {
                Node nextSibling = expression.getChild(primaryPrefix.getIndexInParent() + 1);
                if (nextSibling instanceof ASTPrimarySuffix) {
                    return true;
                }
            }
        }
        return false;
    }

    /** We found a reference to the type given by the name. */
    private void removeReferenceSingleImport(String referenceName) {
        int firstDot = referenceName.indexOf('.');
        String expectedImport = firstDot < 0 ? referenceName : referenceName.substring(0, firstDot);
        Iterator<ImportWrapper> iterator = imports.iterator();
        while (iterator.hasNext()) {
            ImportWrapper anImport = iterator.next();
            if (!anImport.isOnDemand() && anImport.getName().equals(expectedImport)) {
                iterator.remove();
            }
        }
    }

    private void removeOnDemandForPackageName(String fullName) {
        Iterator<ImportWrapper> iterator = imports.iterator();
        while (iterator.hasNext()) {
            ImportWrapper anImport = iterator.next();
            if (anImport.isOnDemand() && anImport.getFullName().equals(fullName)) {
                iterator.remove();
                break;
            }
        }
    }
}
