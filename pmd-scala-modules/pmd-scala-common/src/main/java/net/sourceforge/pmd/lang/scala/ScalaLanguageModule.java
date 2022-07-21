/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.impl.LanguageModuleWithSeveralVersions;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule.ScalaVersionIds;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

import scala.meta.Dialect;

/**
 * Language Module for Scala.
 */
public class ScalaLanguageModule extends LanguageModuleWithSeveralVersions<ScalaVersionIds> {

    /** The name. */
    public static final String NAME = "Scala";

    /** The terse name. */
    public static final String TERSE_NAME = "scala";

    /**
     * Create a new instance of Scala Language Module.
     */
    public ScalaLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("scala"),
              ScalaVersionIds.class);
    }

    enum ScalaVersionIds implements LanguageVersionId {
        SCALA_210("2.10", scala.meta.dialects.package$.MODULE$.Scala210()),
        SCALA_211("2.11", scala.meta.dialects.package$.MODULE$.Scala211()),
        SCALA_212("2.12", scala.meta.dialects.package$.MODULE$.Scala212()),
        SCALA_213("2.13", scala.meta.dialects.package$.MODULE$.Scala213()),
        ;

        private final String name;
        private final Dialect dialect;

        ScalaVersionIds(String name, Dialect dialect) {
            this.name = name;
            this.dialect = dialect;
        }

        @Override
        public String getVersionString() {
            return name;
        }

        @Override
        public boolean isDefault() {
            return this == SCALA_213;
        }
    }
    @InternalApi
    public static @NonNull Dialect dialectOf(LanguageVersion v) {
        return getInstance().getIdOf(v).dialect;
    }


    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(
            bundle,
            new ScalaLanguageHandler(dialectOf(bundle.getLanguageVersion())));
    }

    public static ScalaLanguageModule getInstance() {
        return (ScalaLanguageModule) LanguageRegistry.PMD.getLanguageByFullName(NAME);
    }
}
