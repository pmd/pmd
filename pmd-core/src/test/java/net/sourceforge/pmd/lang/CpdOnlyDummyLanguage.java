/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import net.sourceforge.pmd.cpd.AnyCpdLexer;
import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.reporting.ViolationDecorator;

/**
 * Dummy language used for testing PMD.
 */
public class CpdOnlyDummyLanguage extends CpdOnlyLanguageModuleBase {

    public static final String NAME = "DummyCpdOnly";
    public static final String TERSE_NAME = "dummy_cpd_only";

    public CpdOnlyDummyLanguage() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("dummy", "txt")
                              .addDefaultVersion("1.7", "7"));
    }

    public static CpdOnlyDummyLanguage getInstance() {
        return (CpdOnlyDummyLanguage) Objects.requireNonNull(LanguageRegistry.CPD.getLanguageByFullName(NAME));
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new AnyCpdLexer();
    }
}
