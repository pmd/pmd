package net.sourceforge.pmd.lang;

import java.util.Objects;

import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;

public class DummyLanguageDialectModule extends SimpleDialectLanguageModuleBase {

    public static final String NAME = "DummyDialect";
    public static final String TERSE_NAME = "dummydialect";

    public DummyLanguageDialectModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                .extensions("txt", "dummydlc")
                .addDefaultVersion("1.0").asDialectOf(DummyLanguageModule.TERSE_NAME));
    }

    public static DummyLanguageDialectModule getInstance() {
        return (DummyLanguageDialectModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
    }
}
