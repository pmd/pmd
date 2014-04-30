/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.imports;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.FormalComment;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.ImportWrapper;

public class UnusedImportsRule extends AbstractJavaRule {

    protected Set<ImportWrapper> imports = new HashSet<ImportWrapper>();

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
        super.visit(node, data);
        visitComments(node);

        /* special handling for Bug 2606609 : False "UnusedImports" positive in package-info.java
         * package annotations are processed before the import clauses so they need to be examined
         * again later on.
         */
        if (node.jjtGetNumChildren()>0 && node.jjtGetChild(0) instanceof ASTPackageDeclaration) {
            visit((ASTPackageDeclaration)node.jjtGetChild(0), data);
        }
        for (ImportWrapper wrapper : imports) {
            addViolation(data, wrapper.getNode(), wrapper.getFullName());
        }
        return data;
    }

    /*
     * Patterns to match the following constructs:
     *
     * @see  package.class#member(param, param)  label
     * {@linkplain  package.class#member(param, param)  label}
     * {@link  package.class#member(param, param)  label}
     * {@value  package.class#field}
     * @throws package.class label
     */
    private static final Pattern SEE_PATTERN = Pattern.compile(
            "@see\\s+(\\p{Alpha}\\p{Alnum}*)(?:#\\p{Alnum}*\\(([\\w\\s,]*)\\))?");

    private static final Pattern LINK_PATTERNS = Pattern.compile(
            "\\{@link(?:plain)?\\s+(\\p{Alpha}\\p{Alnum}*)(?:#\\p{Alnum}*\\(([\\w\\s,]*)\\))?[\\s\\}]");

    private static final Pattern VALUE_PATTERN = Pattern.compile(
            "\\{@value\\s+(\\p{Alpha}\\p{Alnum}*)[\\s#\\}]");

    private static final Pattern THROWS_PATTERN = Pattern.compile(
            "@throws\\s+(\\p{Alpha}\\p{Alnum}*)");

    private static final Pattern[] PATTERNS = { SEE_PATTERN, LINK_PATTERNS, VALUE_PATTERN, THROWS_PATTERN };

    private void visitComments(ASTCompilationUnit node) {
        if (imports.isEmpty()) {
            return;
        }
        for (Comment comment: node.getComments()) {
            if (!(comment instanceof FormalComment)) {
                continue;
            }
            for (Pattern p: PATTERNS) {
                Matcher m = p.matcher(comment.getImage());
                while (m.find()) {
                    String s = m.group(1);
                    imports.remove(new ImportWrapper(s, s, new DummyJavaNode(-1)));

                    if (m.groupCount() > 1) {
                        s = m.group(2);
                        if (s != null) {
                            String[] params = s.split("\\s*,\\s*");
                            for (String param : params) {
                                imports.remove(new ImportWrapper(param, param, new DummyJavaNode(-1)));
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
        if (!node.isImportOnDemand()) {
            ASTName importedType = (ASTName) node.jjtGetChild(0);
            String className;
            if (isQualifiedName(importedType)) {
                int lastDot = importedType.getImage().lastIndexOf('.') + 1;
                className = importedType.getImage().substring(lastDot);
            } else {
                className = importedType.getImage();
            }
            imports.add(new ImportWrapper(importedType.getImage(), className, node));
        }

        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        check(node);
        return data;
    }

    protected void check(Node node) {
        if (imports.isEmpty()) {
            return;
        }
        ImportWrapper candidate = getImportWrapper(node);
        if (imports.contains(candidate)) {
            imports.remove(candidate);
        }
    }

    protected ImportWrapper getImportWrapper(Node node) {
        String name;
        if (!isQualifiedName(node)) {
            name = node.getImage();
        } else {
            name = node.getImage().substring(0, node.getImage().indexOf('.'));
        }
        ImportWrapper candidate = new ImportWrapper(node.getImage(), name, new DummyJavaNode(-1));
        return candidate;
    }
}
