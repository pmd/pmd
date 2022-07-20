/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.compilation.Compilation;

@InternalApi
public final class ApexParser implements Parser {

    private final ApexLanguageProperties apexProperties;

    public ApexParser(ApexLanguageProperties apexProperties) {
        this.apexProperties = apexProperties;
        ApexJorjeLogging.disableLogging();
        Locations.useIndexFactory();
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        try {

            final Compilation astRoot = CompilerService.INSTANCE.parseApex(task.getTextDocument());

            if (astRoot == null) {
                throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
            }

            String property = apexProperties.getProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY);
            ApexMultifileAnalysis analysisHandler = ApexMultifileAnalysis.getAnalysisInstance(property);


            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task, apexProperties);
            return treeBuilder.buildTree(astRoot, analysisHandler);
        } catch (apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e);
        }
    }
}
