/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * The simplest implementation of a language with only a few versions,
 * and a single handler for all of them.
 *
 * @author Clément Fournier
 */
public abstract class LanguageModuleBase implements Language {

    private final LanguageMetadata meta;
    private final List<LanguageVersion> distinctVersions;
    private final LanguageVersion defaultVersion;

    protected LanguageModuleBase(LanguageMetadata metadata) {
        this.meta = metadata;
        this.defaultVersion = new LanguageVersion(this, "", null);
        this.distinctVersions = listOf(defaultVersion);
    }

    @Override
    public String getName() {
        return meta.name;
    }

    @Override
    public String getShortName() {
        return meta.getShortName();
    }

    @Override
    public String getTerseName() {
        return meta.id;
    }

    @Override
    public @NonNull List<String> getExtensions() {
        return Collections.unmodifiableList(meta.extensions);
    }

    @Override
    public List<LanguageVersion> getVersions() {
        return distinctVersions;
    }

    @Override
    public LanguageVersion getDefaultVersion() {
        return defaultVersion;
    }

    @Override
    public String toString() {
        return getTerseName();
    }

    @Override
    public int compareTo(Language o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LanguageModuleBase other = (LanguageModuleBase) obj;
        return Objects.equals(getId(), other.getId());
    }

    protected static final class LanguageMetadata {
        private String name;
        private String shortName;
        private final String id;
        private List<String> extensions;

        public LanguageMetadata(String id) {
            this.id = id;
        }

        void validate() {
            Objects.requireNonNull(name);
            Objects.requireNonNull(id);
            Objects.requireNonNull(extensions);
        }

        String getShortName() {
            return shortName == null ? name : shortName;
        }

        public static LanguageMetadata withId(String id) {
            return new LanguageMetadata(id);
        }

        public LanguageMetadata name(String name) {
            this.name = name;
            return this;
        }

        public LanguageMetadata shortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public LanguageMetadata extensions(String e1, String... others) {
            this.extensions = listOf(e1,others);
            return this;
        }

    }
}
