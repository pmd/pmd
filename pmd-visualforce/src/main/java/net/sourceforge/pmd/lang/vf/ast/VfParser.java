/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.vf.VfParserOptions;

/**
 * Parser for the VisualForce language.
 */
public final class VfParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    public VfParser(ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new JavaccTokenDocument(fullText) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return VfTokenKinds.describe(kind);
            }
        };
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserOptions options, String fileName) throws ParseException {
        ASTCompilationUnit root = new VfParserImpl(cs).CompilationUnit();

        // Add type information to the AST
        VfExpressionTypeVisitor visitor = new VfExpressionTypeVisitor(fileName, (VfParserOptions) options);
        visitor.visit(root, null);

        return root;
    }

}
