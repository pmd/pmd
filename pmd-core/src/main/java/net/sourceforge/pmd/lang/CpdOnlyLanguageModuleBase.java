/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

/**
 * Base class for language modules that only support CPD and not PMD.
 *
 * @author Clément Fournier
 */
public abstract class CpdOnlyLanguageModuleBase extends LanguageModuleBase {

    /**
     * Construct a module instance using the given metadata. The metadata must
     * be properly constructed.
     *
     * @throws IllegalStateException If the metadata is invalid (eg missing extensions or name)
     */
    protected CpdOnlyLanguageModuleBase(LanguageMetadata metadata) {
        super(metadata);
    }

    @Override
    public boolean supportsParsing() {
        return false;
    }
}