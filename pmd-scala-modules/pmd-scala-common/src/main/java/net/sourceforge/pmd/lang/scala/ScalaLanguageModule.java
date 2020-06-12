/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.scala.rule.ScalaRuleChainVisitor;

/**
 * Language Module for Scala.
 */
public class ScalaLanguageModule extends BaseLanguageModule {

    /** The name. */
    public static final String NAME = "Scala";

    /** The terse name. */
    public static final String TERSE_NAME = "scala";

    /**
     * Create a new instance of Scala Language Module.
     */
    public ScalaLanguageModule() {
        super(NAME, null, TERSE_NAME, ScalaRuleChainVisitor.class, "scala");
        addVersion("2.13", new ScalaLanguageHandler(scala.meta.dialects.package$.MODULE$.Scala213()), true);
        addVersion("2.12", new ScalaLanguageHandler(scala.meta.dialects.package$.MODULE$.Scala212()), false);
        addVersion("2.11", new ScalaLanguageHandler(scala.meta.dialects.package$.MODULE$.Scala211()), false);
        addVersion("2.10", new ScalaLanguageHandler(scala.meta.dialects.package$.MODULE$.Scala210()), false);
    }
}
