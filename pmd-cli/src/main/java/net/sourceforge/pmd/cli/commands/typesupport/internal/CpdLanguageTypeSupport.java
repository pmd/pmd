/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * Provider of candidates / conversion support for supported CPD languages.
 */
public class CpdLanguageTypeSupport extends LanguageTypeSupport {

    public CpdLanguageTypeSupport() {
        super(LanguageRegistry.CPD);
    }

}
