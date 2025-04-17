package net.sourceforge.pmd.lang;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.impl.BasePmdDialectLanguageVersionHandler;
import net.sourceforge.pmd.lang.impl.SimpleDialectLanguageModuleBase;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class DummyLanguageDialectModule extends SimpleDialectLanguageModuleBase {

    public static final String NAME = "DummyDialect";
    public static final String TERSE_NAME = "dummydialect";

    public static final PropertyDescriptor<Boolean> DUMMY_DIALECT_PROP =
            PropertyFactory.booleanProperty("dummyDialectProperty")
                    .defaultValue(false)
                    .desc("Some dummy boolean without purpose")
                    .build();

    public DummyLanguageDialectModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                .extensions("txt", "dummydlc")
                .addDefaultVersion("1.0").asDialectOf(DummyLanguageModule.TERSE_NAME), new Handler());
    }

    public static DummyLanguageDialectModule getInstance() {
        return (DummyLanguageDialectModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
    }

    @Override
    protected @NonNull LanguagePropertyBundle newDialectPropertyBundle() {
        LanguagePropertyBundle bundle = super.newDialectPropertyBundle();
        bundle.definePropertyDescriptor(DUMMY_DIALECT_PROP);
        return bundle;
    }

    public static class Handler extends BasePmdDialectLanguageVersionHandler {

        @Override
        public XPathHandler getXPathHandler() {
            return XPathHandler.getHandlerForFunctionDefs(dummyDialectFunction());
        }
    }

    @NonNull
    public static XPathFunctionDefinition dummyDialectFunction() {
        return new XPathFunctionDefinition("dummyDialectFn", DummyLanguageDialectModule.getInstance()) {
            @Override
            public Type[] getArgumentTypes() {
                return new Type[] {Type.SINGLE_STRING};
            }

            @Override
            public Type getResultType() {
                return Type.SINGLE_BOOLEAN;
            }

            @Override
            public FunctionCall makeCallExpression() {
                return (contextNode, arguments) -> StringUtils.equals(arguments[0].toString(), contextNode.getImage());
            }
        };
    }
}
