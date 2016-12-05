/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Language Module for Scala
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
        super(NAME, null, TERSE_NAME, null, "scala");
        addVersion("", null, true);
    }
}
