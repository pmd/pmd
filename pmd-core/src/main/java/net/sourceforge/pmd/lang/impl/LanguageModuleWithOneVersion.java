/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * The simplest implementation of a language with only a few versions,
 * and a single handler for all of them.
 *
 * @author Clément Fournier
 */
public class LanguageModuleWithOneVersion extends LanguageModuleBase {

    private final List<LanguageVersion> distinctVersions;
    private final LanguageVersion defaultVersion;


    public LanguageModuleWithOneVersion(LanguageMetadata metadata) {
        super(metadata);
        this.defaultVersion = new LanguageVersion(this, "", null);
        this.distinctVersions = listOf(defaultVersion);
    }

    @Override
    public List<LanguageVersion> getVersions() {
        return distinctVersions;
    }

    @Override
    public LanguageVersion getDefaultVersion() {
        return defaultVersion;
    }


}
