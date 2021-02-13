/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.compilation.Compilation;

@InternalApi
public final class ApexParser implements Parser {

    private static final String NOT_A_DIRECTORY = "not_a_directory";
    @InternalApi
    public static final PropertyDescriptor<String> MULTIFILE_DIRECTORY =
        PropertyFactory.stringProperty("apexRootDirectory")
                       .desc("The root directory of the Salesforce metadata, where `sfdx-project.json` resides")
                       .defaultValue(NOT_A_DIRECTORY)
                       .build();

    public ApexParser() {
        ApexJorjeLogging.disableLogging();
        Locations.useIndexFactory();
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        try {
            String sourceCode = task.getSourceText();
            final Compilation astRoot = CompilerService.INSTANCE.parseApex(task.getFileDisplayName(), sourceCode);

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            ApexMultifileAnalysis analysisHandler =
                ApexMultifileAnalysis.getAnalysisInstance(task.getProperties().getProperty(MULTIFILE_DIRECTORY));

            SourceCodePositioner positioner = new SourceCodePositioner(sourceCode);
            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(sourceCode, task.getCommentMarker(), positioner);
            AbstractApexNode<Compilation> treeRoot = treeBuilder.build(astRoot);
            return new ASTApexFile(positioner, task, treeRoot, treeBuilder.getSuppressMap(), analysisHandler);
        } catch (apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e);
        }
    }
}
