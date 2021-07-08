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

    @InternalApi // todo change that to optional<file> when properties are updated
    public static final PropertyDescriptor<String> MULTIFILE_DIRECTORY =
        PropertyFactory.stringProperty("rootDirectory")
                       .desc("The root directory of the Salesforce metadata, where `sfdx-project.json` resides. "
                                 + "Set environment variable PMD_APEX_ROOTDIRECTORY to use this.")
                       .defaultValue("") // is this ok?
                       .build();

    public ApexParser() {
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

            final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task);
            return treeBuilder.buildTree(astRoot);
        } catch (apex.jorje.services.exception.ParseException e) {
            throw new ParseException(e);
        }
    }
}
