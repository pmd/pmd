/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * Provider of candidates / conversion support for supported CPD languages.
 *
 * <p>Beware, the help will report this on runtime, and be accurate to available
 * modules in the classpath, but autocomplete will include all available at build time.
 */
public class CpdLanguageTypeSupport extends LanguageTypeSupport {

    public CpdLanguageTypeSupport() {
        super(LanguageRegistry.CPD);
    }

}
