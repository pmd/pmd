/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageModuleBase.LanguageMetadata.LangVersionMetadata;

/**
 * Base class for language modules.
 *
 * @author Clément Fournier
 */
public abstract class LanguageModuleBase implements Language {

    private final LanguageMetadata meta;

    private final List<LanguageVersion> distinctVersions;
    private final Map<String, LanguageVersion> byName;
    private final LanguageVersion defaultVersion;
    private final Set<String> dependencies;


    protected LanguageModuleBase(LanguageMetadata metadata) {
        this.meta = metadata;
        metadata.validate();
        this.dependencies = Collections.unmodifiableSet(metadata.dependencies);
        List<LanguageVersion> versions = new ArrayList<>();
        Map<String, LanguageVersion> byName = new HashMap<>();
        LanguageVersion defaultVersion = null;

        if (metadata.versionMetadata.isEmpty()) {
            // Many languages have just one version, which is implicitly
            // created here.
            metadata.versionMetadata.add(new LangVersionMetadata("", Collections.emptyList(), true));
        }

        int i = 0;
        for (LanguageMetadata.LangVersionMetadata versionId : metadata.versionMetadata) {
            String versionStr = versionId.name;
            LanguageVersion languageVersion = new LanguageVersion(this, versionStr, i++);

            versions.add(languageVersion);

            checkNotPresent(byName, versionStr);
            byName.put(versionStr, languageVersion);
            for (String alias : versionId.aliases) {
                checkNotPresent(byName, alias);
                byName.put(alias, languageVersion);
            }

            if (versionId.isDefault) {
                if (defaultVersion != null) {
                    throw new IllegalStateException(
                        "Default version already set to " + defaultVersion + ", cannot set it to " + languageVersion);
                }
                defaultVersion = languageVersion;
            }
        }

        this.byName = Collections.unmodifiableMap(byName);
        this.distinctVersions = Collections.unmodifiableList(versions);
        this.defaultVersion = Objects.requireNonNull(defaultVersion, "No default version for " + getId());

    }


    private static void checkNotPresent(Map<String, ?> map, String alias) {
        if (map.containsKey(alias)) {
            throw new IllegalArgumentException("Version key '" + alias + "' is duplicated");
        }
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
    public LanguageVersion getVersion(String version) {
        return byName.get(version);
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
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

    /**
     * Metadata about a language, basically a builder pattern for the
     * language instance.
     */
    protected static final class LanguageMetadata {

        public Set<String> dependencies = new HashSet<>();
        private String name;
        private String shortName;
        private final String id;
        private List<String> extensions;
        private final List<LangVersionMetadata> versionMetadata = new ArrayList<>();

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
            this.extensions = listOf(e1, others);
            return this;
        }

        public LanguageMetadata addVersion(String name, String... aliases) {
            versionMetadata.add(new LangVersionMetadata(name, Arrays.asList(aliases), false));
            return this;
        }

        public LanguageMetadata addDefaultVersion(String name, String... aliases) {
            versionMetadata.add(new LangVersionMetadata(name, Arrays.asList(aliases), true));
            return this;
        }

        public LanguageMetadata dependsOnLanguage(String id) {
            dependencies.add(id);
            return this;
        }

        static final class LangVersionMetadata {

            final String name;
            final List<String> aliases;
            final boolean isDefault;

            private LangVersionMetadata(String name, List<String> aliases, boolean isDefault) {
                this.name = name;
                this.aliases = aliases;
                this.isDefault = isDefault;
            }
        }
    }
}
