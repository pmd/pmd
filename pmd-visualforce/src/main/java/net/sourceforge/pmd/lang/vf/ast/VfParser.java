/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.vf.VfLanguageProperties;

/**
 * Parser for the VisualForce language.
 */
public final class VfParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    private VfLanguageProperties vfProperties;

    public VfParser(VfLanguageProperties vfProperties) {
        this.vfProperties = vfProperties;
    }

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(VfTokenKinds.TOKEN_NAMES);

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserTask task) throws ParseException {
        ASTCompilationUnit root = new VfParserImpl(cs).CompilationUnit().makeTaskInfo(task);

        // Add type information to the AST
        VfExpressionTypeVisitor visitor = new VfExpressionTypeVisitor(task, vfProperties);
        visitor.visit(root, null);

        return root;
    }

}
