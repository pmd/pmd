/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;

/**
 * Adapter for the JavaParser, using the specified grammar version.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 * @author Andreas Dangel
 */
public class JavaParser extends JjtreeParserAdapter<ASTCompilationUnit> {

    private final LanguageLevelChecker<?> checker;
    private final boolean postProcess;

    public JavaParser(LanguageLevelChecker<?> checker, boolean postProcess) {
        this.checker = checker;
        this.postProcess = postProcess;
    }


    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return JavaTokenDocumentBehavior.INSTANCE;
    }

    @Override
    protected ASTCompilationUnit parseImpl(CharStream cs, ParserTask task) throws ParseException {
        JavaParserImpl parser = new JavaParserImpl(cs);
        parser.setSuppressMarker(task.getCommentMarker());
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        ASTCompilationUnit root = parser.CompilationUnit();
        root.setAstInfo(new AstInfo<>(task, root, parser.getSuppressMap()));
        checker.check(root);

        if (postProcess) {
            JavaAstProcessor processor = JavaAstProcessor.create(task.getAuxclasspathClassLoader(),
                                                                 task.getLanguageVersion(),
                                                                 task.getReporter());
            processor.process(root);
        }

        return root;
    }
}
