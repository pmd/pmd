/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.imports;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.Token;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.ImportWrapper;

public class UnusedImportsRule extends AbstractJavaRule {

    protected Set<ImportWrapper> imports = new HashSet<ImportWrapper>();

    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
        super.visit(node, data);
        visitFormalComments(node);
        for (ImportWrapper wrapper : imports) {
            addViolation(data, wrapper.getNode(), wrapper.getFullName());
        }
        return data;
    }

    /*
     * Patterns to match the following constructs:
     *
     * @see  package.class#member  label
     * {@linkplain  package.class#member  label}
     * {@link  package.class#member  label}
     * {@value  package.class#field}
     */
    private static final Pattern SEE_PATTERN = Pattern.compile(
            "@see\\s+(\\p{Alpha}\\p{Alnum}*)[\\s#]");

    private static final Pattern LINK_PATTERNS = Pattern.compile(
            "\\{@link(?:plain)?\\s+(\\p{Alpha}\\p{Alnum}*)[\\s#\\}]");

    private static final Pattern VALUE_PATTERN = Pattern.compile(
            "\\{@value\\s+(\\p{Alpha}\\p{Alnum}*)[\\s#\\}]");

    private static final Pattern[] PATTERNS = { SEE_PATTERN, LINK_PATTERNS, VALUE_PATTERN };

    private void visitFormalComments(ASTCompilationUnit node) {
        if (imports.isEmpty()) {
            return;
        }
        List<Token> formals = node.getFormalComments();
        for (Token formal: formals) {
            for (Pattern p: PATTERNS) {
                Matcher m = p.matcher(formal.image);
                while (m.find()) {
                    String s = m.group(1);
                    ImportWrapper candidate = new ImportWrapper(s, s, new DummyJavaNode(-1));

                    if (imports.contains(candidate)) {
                        imports.remove(candidate);
                        if (imports.isEmpty()) {
                            return;
                        }
                    }
                }
            }
        }
    }

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

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node);
        return super.visit(node, data);
    }

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
