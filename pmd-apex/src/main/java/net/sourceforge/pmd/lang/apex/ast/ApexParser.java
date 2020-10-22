/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.IOException;
import java.io.Reader;


import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.compilation.Compilation;

@InternalApi
public final class ApexParser extends AbstractParser {

    public ApexParser(ParserOptions parserOptions) {
        super(parserOptions);
        ApexJorjeLogging.disableLogging();
        Locations.useIndexFactory();
    }

    @Override
    public RootNode parse(final String filename, final Reader reader) {
        try {
            final String sourceCode = IOUtils.toString(reader);
            final Compilation astRoot = CompilerService.INSTANCE.parseApex(filename, sourceCode);

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            ApexMultifileAnalysis analysisHandler =
                    ApexMultifileAnalysis.getAnalysisInstance(getParserOptions().getMultiFileAnalysisDirectory());

            SourceCodePositioner positioner = new SourceCodePositioner(sourceCode);
            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(sourceCode, getParserOptions(), positioner);
            AbstractApexNode<Compilation> treeRoot = treeBuilder.build(astRoot);
            ASTApexFile fileNode = new ASTApexFile(positioner, treeRoot);
            fileNode.setNoPmdComments(treeBuilder.getSuppressMap());
            fileNode.setMultifileAnalysis(filename, analysisHandler);
            return fileNode;
        } catch (IOException | apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e);
        }
    }
}
