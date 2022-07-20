/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaCharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProcessor;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    private final LanguageLevelChecker<?> checker;
    private final String suppressMarker;
    private final JavaLanguageProcessor javaProcessor;
    private final boolean postProcess;

    public JavaParser(LanguageLevelChecker<?> checker,
                      String suppressMarker,
                      JavaLanguageProcessor javaProcessor,
                      boolean postProcess) {
        this.checker = checker;
        this.suppressMarker = suppressMarker;
        this.javaProcessor = javaProcessor;
        this.postProcess = postProcess;
    }


    @Override
    protected JavaccTokenDocument newDocumentImpl(TextDocument textDocument) {
        return new JavaTokenDocument(textDocument);
    }

    @Override
    protected CharStream newCharStream(JavaccTokenDocument tokenDocument) {
        return new JavaCharStream(tokenDocument);
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserTask task) throws ParseException {
        JavaParserImpl parser = new JavaParserImpl(cs);
        parser.setSuppressMarker(suppressMarker);
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        ASTCompilationUnit root = parser.CompilationUnit();
        root.setAstInfo(new AstInfo<>(task, root, parser.getSuppressMap()));
        checker.check(root);

        if (postProcess) {
            JavaAstProcessor processor = JavaAstProcessor.create(javaProcessor, task.getReporter());
            processor.process(root);
        }

        return root;
    }
}
