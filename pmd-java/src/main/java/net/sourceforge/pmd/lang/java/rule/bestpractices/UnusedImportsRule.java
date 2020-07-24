/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnusedImportsRule extends AbstractJavaRule {

    protected Set<ImportWrapper> imports = new HashSet<>();

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

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
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
            addViolation(data, wrapper.getNode(), wrapper.getFullName());
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
                        imports.remove(new ImportWrapper(fullname, fullname));
                    }

                    if (m.groupCount() > 1) {
                        fullname = m.group(2);
                        if (fullname != null) {
                            String[] params = fullname.split("\\s*,\\s*");
                            for (String param : params) {
                                final int firstDot = param.indexOf('.');
                                final String expectedImportName;
                                if (firstDot == -1) {
                                    expectedImportName = param;
                                } else {
                                    expectedImportName = param.substring(0, firstDot);
                                }
                                imports.remove(new ImportWrapper(param, expectedImportName));
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
        // This was edited during the grammar updating process, because
        // ImportDeclaration is not a TypeNode anymore, and there is no Name anymore.
        // If tests are failing, refer to the history of this file to get the
        // previously working version.
        if (node.isImportOnDemand()) {
            imports.add(new ImportWrapper(node.getImportedName(), null, node, node.isStatic()));
        } else {
            String importedType = node.getImportedName();
            String className;
            if (isQualifiedName(importedType)) {
                int lastDot = importedType.lastIndexOf('.') + 1;
                className = importedType.substring(lastDot);
            } else {
                className = importedType;
            }
            imports.add(new ImportWrapper(importedType, className, node));
        }
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node, node.getTypeImage());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        check(node, node.getImage());
        return data;
    }

    protected void check(Node node, String image) {
        if (imports.isEmpty()) {
            return;
        }
        ImportWrapper candidate = getImportWrapper(node, image);

        // check exact imports
        Iterator<ImportWrapper> it = imports.iterator();
        while (it.hasNext()) {
            ImportWrapper i = it.next();
            if (!i.isStaticOnDemand() && i.matches(candidate)) {
                it.remove();
                return;
            }
        }

        // check static on-demand imports
        it = imports.iterator();
        while (it.hasNext()) {
            ImportWrapper i = it.next();
            if (i.isStaticOnDemand() && i.matches(candidate)) {
                it.remove();
                return;
            }
        }

        if (TypeNode.class.isAssignableFrom(node.getClass()) && ((TypeNode) node).getType() != null) {
            Class<?> c = ((TypeNode) node).getType();
            if (c.getPackage() != null) {
                candidate = new ImportWrapper(c.getPackage().getName(), null);
                if (imports.contains(candidate)) {
                    imports.remove(candidate);
                }
            }
        }
    }

    protected ImportWrapper getImportWrapper(Node node, String image) {
        String fullName = image;

        String name;
        if (!isQualifiedName(image)) {
            name = image;
        } else {
            // ASTName could be: MyClass.MyConstant
            // name -> MyClass
            // fullName -> MyClass.MyConstant
            name = fullName.substring(0, node.getImage().indexOf('.'));
            if (isMethodCall(node)) {
                // ASTName could be: MyClass.MyConstant.method(a, b)
                // name -> MyClass
                // fullName -> MyClass.MyConstant
                fullName = fullName.substring(0, fullName.lastIndexOf('.'));
            }
        }

        return new ImportWrapper(fullName, name);
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
}
