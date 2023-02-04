/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexJorjeLogging;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.compilation.Compilation;

@InternalApi
public final class ApexParser implements Parser {

    public ApexParser() {
        ApexJorjeLogging.disableLogging();
        Locations.useIndexFactory();
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        try {

            final Compilation astRoot = CompilerService.INSTANCE.parseApex(task.getTextDocument());

            assert astRoot != null : "Normally replaced by Compilation.INVALID";

            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task, (ApexLanguageProcessor) task.getLanguageProcessor());
            return treeBuilder.buildTree(astRoot);
        } catch (apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e).setFileName(task.getFileDisplayName());
        }
    }
}
