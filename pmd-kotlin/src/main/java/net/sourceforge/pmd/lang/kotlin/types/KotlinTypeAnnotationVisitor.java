/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;

import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.DeclarationKind;
import nl.stokpop.typemapper.model.FileAst;
import nl.stokpop.typemapper.model.TypedAst;

/**
 * Walks a parsed Kotlin AST and sets type/annotation attributes on nodes using
 * pre-analyzed type data from kotlin-type-mapper:
 *
 * <ul>
 *   <li>type data on {@code PropertyDeclaration} nodes (property type)</li>
 *   <li>type data on {@code ClassParameter} nodes -- primary constructor
 *       {@code val}/{@code var} params (e.g. {@code class Foo(val name: String)})</li>
 *   <li>type data on {@code FunctionDeclaration} nodes (return type)</li>
 *   <li>type data on {@code FunctionValueParameter} nodes (parameter type)
 *       -- delegated to {@link AnnotationAttributeAnnotator}</li>
 *   <li>type data on {@code CatchBlock} nodes (caught exception type)</li>
 *   <li>type data on {@code ForStatement} nodes (loop variable type)</li>
 *   <li>type data on {@code UnescapedAnnotation} <em>and</em>
 *       {@code SingleAnnotation} nodes (annotation FQN)
 *       -- delegated to {@link AnnotationAttributeAnnotator}</li>
 *   <li>type data on declaration nodes (comma-joined FQN list)
 *       -- delegated to {@link AnnotationAttributeAnnotator}</li>
 *   <li>type data on {@code DelegationSpecifier} nodes (supertype FQN)
 *       -- delegated to {@link DelegationSpecifierAnnotator}</li>
 * </ul>
 *
 * <p>The visitor is constructed once per analysis run (from the {@link TypedAst}
 * produced by kotlin-type-mapper) and applied to each file's root node during
 * the post-parse step inside {@code KotlinLanguageProcessor}.
 *
 * <p>File matching uses the <em>base filename</em> (e.g. {@code "Foo.kt"}) rather
 * than the full path, so it works regardless of whether the files were written to
 * a temporary directory or analyzed from their original location.
 */
public final class KotlinTypeAnnotationVisitor {

    /** Map from base filename (e.g. "Foo.kt") -> per-line declarations index. */
    private final Map<String, Map<Integer, List<DeclarationAst>>> byFilename;

    public KotlinTypeAnnotationVisitor(TypedAst typedAst) {
        Map<String, Map<Integer, List<DeclarationAst>>> index = new HashMap<>();
        for (FileAst fileAst : typedAst.getFiles()) {
            String name = new File(fileAst.getRelativePath()).getName();
            Map<Integer, List<DeclarationAst>> byLine =
                    index.computeIfAbsent(name, k -> new HashMap<>());
            for (DeclarationAst decl : fileAst.getDeclarations()) {
                byLine.computeIfAbsent(decl.getLine(), k -> new ArrayList<>()).add(decl);
            }
        }
        this.byFilename = index;
    }

    /**
     * Annotates all {@code PropertyDeclaration}, {@code FunctionDeclaration},
     * {@code ClassDeclaration}, {@code CatchBlock}, and {@code ForStatement} nodes in
     * the given AST root, and sets {@code @TypeName} on their annotation children
     * as well as on {@code FunctionValueParameter} children of function declarations.
     *
     * @param root     the root node of the parsed Kotlin file
     * @param absPath  the absolute path of the file (used to extract the base filename)
     */
    public void annotate(KotlinNode root, String absPath) {
        String filename = new File(absPath).getName();
        Map<Integer, List<DeclarationAst>> resolved = byFilename.get(filename);
        if (resolved == null && !filename.endsWith(".kt")) {
            // Fallback: PmdRuleTst uses synthetic file ids without .kt extension (e.g. "file").
            // The temp file written to disk has .kt appended, so try that name.
            resolved = byFilename.get(filename + ".kt");
        }
        if (resolved == null) {
            return;
        }
        final Map<Integer, List<DeclarationAst>> byLine = resolved;

        root.acceptVisitor(new AnnotatingVisitor(byLine), null);
    }

    /**
     * Visitor that annotates PMD AST nodes with type/annotation attributes from
     * the kotlin-type-mapper data indexed by line number.
     *
     * <p>Delegation-specifier and annotation-attribute logic is delegated to
     * {@link DelegationSpecifierAnnotator} and {@link AnnotationAttributeAnnotator}
     * respectively.
     */
    private static final class AnnotatingVisitor extends KotlinVisitorBase<Void, Void> {

        private final Map<Integer, List<DeclarationAst>> byLine;

        AnnotatingVisitor(Map<Integer, List<DeclarationAst>> byLine) {
            this.byLine = byLine;
        }

        @Override
        public Void visitPropertyDeclaration(KotlinParser.KtPropertyDeclaration node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    AnnotationAttributeAnnotator.setAnnotationAttributes(node, decl.getAnnotations());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        // Primary constructor val/var parameters (e.g. "class Foo(val name: String)")
        // are KtClassParameter nodes in the AST, not KtPropertyDeclaration.
        // kotlin-type-mapper emits them as kind="property" with a type field.
        @Override
        public Void visitClassParameter(KotlinParser.KtClassParameter node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.PROPERTY && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    AnnotationAttributeAnnotator.setAnnotationAttributes(node, decl.getAnnotations());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitFunctionDeclaration(KotlinParser.KtFunctionDeclaration node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getReturnType() != null) {
                    KotlinNodeTypeData.setReturnTypeName(node, decl.getReturnType());
                    AnnotationAttributeAnnotator.setAnnotationAttributes(node, decl.getAnnotations());
                    AnnotationAttributeAnnotator.setFunctionParameterTypes(node, decl.getParameters());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitCatchBlock(KotlinParser.KtCatchBlock node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.CATCH_VARIABLE && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitForStatement(KotlinParser.KtForStatement node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.FOR_LOOP_VARIABLE && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitClassDeclaration(KotlinParser.KtClassDeclaration node, Void data) {
            List<DeclarationAst> decls = lookupWithFallback(byLine, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.CLASS
                        || decl.getKind() == DeclarationKind.DATA_CLASS
                        || decl.getKind() == DeclarationKind.SEALED_CLASS
                        || decl.getKind() == DeclarationKind.INTERFACE
                        || decl.getKind() == DeclarationKind.ENUM) {
                    // Set @TypeName to the class's own FQN (useful in Designer + XPath)
                    KotlinNodeTypeData.setTypeName(node, decl.getFqName());
                    AnnotationAttributeAnnotator.setAnnotationAttributes(node, decl.getAnnotations());
                    DelegationSpecifierAnnotator.setDelegationSpecifierTypes(node, decl.getSuperTypes());
                    break;
                }
            }
            return visitChildren(node, data);
        }
    }

    /** Extracts the simple (unqualified) name from a fully-qualified name. */
    static String simpleNameOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }

    /**
     * Finds the first {@code KtUserType} directly inside a {@code KtConstructorInvocation}.
     * Shared helper used by both {@link DelegationSpecifierAnnotator} and
     * {@link AnnotationAttributeAnnotator}.
     */
    static KotlinParser.KtUserType findUserTypeInConstructorInvocation(
            KotlinParser.KtConstructorInvocation ctorInvocation) {
        for (int j = 0; j < ctorInvocation.getNumChildren(); j++) {
            if (ctorInvocation.getChild(j) instanceof KotlinParser.KtUserType) {
                return (KotlinParser.KtUserType) ctorInvocation.getChild(j);
            }
        }
        return null;
    }

    static List<DeclarationAst> lookupWithFallback(
            Map<Integer, List<DeclarationAst>> byLine, int line) {
        List<DeclarationAst> exact = byLine.get(line);
        if (exact != null && !exact.isEmpty()) {
            return exact;
        }
        // +/-1 fallback: when annotations are on a separate line from the 'fun'/'val' keyword,
        // ktm reports the annotation line but PMD's ANTLR parser may report the keyword line,
        // causing a 1-line difference. See DESIGN.md section 9 on declarationsAt line tolerance.
        List<DeclarationAst> result = new ArrayList<>();
        List<DeclarationAst> prev = byLine.get(line - 1);
        List<DeclarationAst> next = byLine.get(line + 1);
        if (prev != null) {
            result.addAll(prev);
        }
        if (next != null) {
            result.addAll(next);
        }
        return result.isEmpty() ? Collections.emptyList() : result;
    }
}
