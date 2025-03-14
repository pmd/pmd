/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.LanguageModuleBase.LanguageMetadata.LangVersionMetadata;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.StringUtil;

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


    /**
     * Construct a module instance using the given metadata. The metadata must
     * be properly constructed.
     *
     * @throws IllegalStateException If the metadata is invalid (eg missing extensions or name or no versions)
     */
    protected LanguageModuleBase(LanguageMetadata metadata) {
        this.meta = metadata;
        metadata.validate();
        this.dependencies = Collections.unmodifiableSet(metadata.dependencies);
        List<LanguageVersion> versions = new ArrayList<>();
        Map<String, LanguageVersion> byName = new HashMap<>();
        LanguageVersion defaultVersion = null;

        if (metadata.versionMetadata.isEmpty()) {
            if (this instanceof PmdCapableLanguage) {
                // pmd languages need to have versions
                throw new IllegalStateException("No versions for '" + getId() + "'");
            } else {
                // for others, a version is declared implicitly
                metadata.versionMetadata.add(new LangVersionMetadata());
            }
        }

        int i = 0;
        for (LanguageMetadata.LangVersionMetadata versionId : metadata.versionMetadata) {
            String versionStr = versionId.name;
            LanguageVersion languageVersion = new LanguageVersion(this, versionStr, i++, versionId.aliases);

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
    public @NonNull LanguageVersion getDefaultVersion() {
        return defaultVersion;
    }

    @Override
    public LanguageVersion getVersion(String version) {
        return byName.get(version);
    }

    @Override
    public Set<String> getVersionNamesAndAliases() {
        return Collections.unmodifiableSet(byName.keySet());
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
    public String getId() {
        return meta.id;
    }

    @Override
    public @NonNull List<String> getExtensions() {
        return Collections.unmodifiableList(meta.extensions);
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public int compareTo(Language o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
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
     *
     * <p>Some of the metadata are mandatory:
     * <ul>
     * <li>The id ({@link #withId(String)})
     * <li>The display name ({@link #name(String)})
     * <li>The file extensions ({@link #extensions(String, String...)}
     * </ul>
     *
     */
    public static final class LanguageMetadata {

        /** Language IDs should be conventional Java package names. */
        private static final Pattern VALID_LANG_ID = Pattern.compile("[a-z][_a-z0-9]*");
        private static final Pattern SPACE_PAT = Pattern.compile("\\s");

        private final Set<String> dependencies = new HashSet<>();
        private String name;
        private @Nullable String shortName;
        private final @NonNull String id;
        private List<String> extensions;
        private final List<LangVersionMetadata> versionMetadata = new ArrayList<>();

        private LanguageMetadata(@NonNull String id) {
            this.id = id;
            if (!VALID_LANG_ID.matcher(id).matches()) {
                throw new IllegalArgumentException(
                    "ID '" + id + "' is not a valid language ID (should match " + VALID_LANG_ID + ").");
            }
        }

        void validate() {
            AssertionUtil.validateState(name != null, "Language " + id + " should have a name");
            AssertionUtil.validateState(
                extensions != null, "Language " + id + " has not registered any file extensions");
        }

        String getShortName() {
            return shortName == null ? name : shortName;
        }

        /**
         * Factory method to create an ID.
         *
         * @param id The language id. Must be usable as a Java package name segment,
         *           ie be lowercase, alphanumeric, starting with a letter.
         *
         * @return A builder for language metadata
         *
         * @throws IllegalArgumentException If the parameter is not a valid ID
         * @throws NullPointerException     If the parameter is null
         */
        public static LanguageMetadata withId(@NonNull String id) {
            return new LanguageMetadata(id);
        }

        /**
         * Record the {@linkplain Language#getName() display name} of
         * the language. This also serves as the {@linkplain Language#getShortName() short name}
         * if {@link #shortName(String)} is not called.
         *
         * @param name Display name of the language
         *
         * @throws NullPointerException     If the parameter is null
         * @throws IllegalArgumentException If the parameter is not a valid language name
         */
        public LanguageMetadata name(@NonNull String name) {
            AssertionUtil.requireParamNotNull("name", name);
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("Not a valid language name: " + StringUtil.inSingleQuotes(name));
            }
            this.name = name.trim();
            return this;
        }

        /**
         * Record the {@linkplain Language#getShortName() short name} of the language.
         *
         * @param shortName Short name of the language
         *
         * @throws NullPointerException     If the parameter is null
         * @throws IllegalArgumentException If the parameter is not a valid language name
         */

        public LanguageMetadata shortName(@NonNull String shortName) {
            AssertionUtil.requireParamNotNull("short name", shortName);
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("Not a valid language name: " + StringUtil.inSingleQuotes(name));
            }
            this.shortName = shortName.trim();
            return this;
        }

        /**
         * Record the {@linkplain Language#getExtensions() extensions}
         * assigned to the language. Extensions should not start with a period
         * {@code .}.
         *
         * @param extensionWithoutPeriod First extensions
         * @param others                 Other extensions (optional)
         *
         * @throws NullPointerException If any extension is null
         */
        public LanguageMetadata extensions(String extensionWithoutPeriod, String... others) {
            this.extensions = new ArrayList<>(setOf(extensionWithoutPeriod, others));
            AssertionUtil.requireContainsNoNullValue("extensions", this.extensions);
            return this;
        }

        /**
         * Record the {@linkplain Language#getExtensions() extensions}
         * assigned to the language. Extensions should not start with a period
         * {@code .}. At least one extension must be provided.
         *
         * @param extensions the extensions
         *
         * @throws NullPointerException     If any extension is null
         * @throws IllegalArgumentException If no extensions are provided
         */
        public LanguageMetadata extensions(Collection<String> extensions) {
            this.extensions = new ArrayList<>(new HashSet<>(extensions));
            AssertionUtil.requireContainsNoNullValue("extensions", this.extensions);
            if (this.extensions.isEmpty()) {
                throw new IllegalArgumentException("At least one extension is required.");
            }
            return this;
        }

        /**
         * Add a new version by its name.
         *
         * @param name    Version name. Must contain no spaces.
         * @param aliases Additional names that are mapped to this version. Must contain no spaces.
         *
         * @throws NullPointerException     If any parameter is null
         * @throws IllegalArgumentException If the name or aliases are empty or contain spaces
         */

        public LanguageMetadata addVersion(String name, String... aliases) {
            versionMetadata.add(new LangVersionMetadata(name, Arrays.asList(aliases), false));
            return this;
        }

        /**
         * Add a new version by its name and make it the default version.
         *
         * @param name    Version name. Must contain no spaces.
         * @param aliases Additional names that are mapped to this version. Must contain no spaces.
         *
         * @throws NullPointerException     If any parameter is null
         * @throws IllegalArgumentException If the name or aliases are empty or contain spaces
         */
        public LanguageMetadata addDefaultVersion(String name, String... aliases) {
            versionMetadata.add(new LangVersionMetadata(name, Arrays.asList(aliases), true));
            return this;
        }


        /**
         * Add all the versions of the given language, including the
         * default version.
         *
         * @param language Other language
         *
         * @throws NullPointerException     If any parameter is null
         * @throws IllegalArgumentException If the name or aliases are empty or contain spaces
         */
        public LanguageMetadata addAllVersionsOf(Language language) {
            for (LanguageVersion version : language.getVersions()) {
                versionMetadata.add(new LangVersionMetadata(version.getVersion(),
                                                            version.getAliases(),
                                                            version.equals(language.getDefaultVersion())));
            }
            return this;
        }

        /**
         * Record that this language depends on another language, identified
         * by its id. This means any {@link LanguageProcessorRegistry} that
         * contains a processor for this language is asserted upon construction
         * to also contain a processor for the language depended on.
         *
         * @param id ID of the language to depend on.
         *
         * @throws NullPointerException     If any parameter is null
         * @throws IllegalArgumentException If the name is not a valid language Id
         */

        public LanguageMetadata dependsOnLanguage(String id) {
            if (!VALID_LANG_ID.matcher(id).matches()) {
                throw new IllegalArgumentException(
                    "ID '" + id + "' is not a valid language ID (should match " + VALID_LANG_ID + ").");
            }
            dependencies.add(id);
            return this;
        }

        static final class LangVersionMetadata {

            final String name;
            final List<String> aliases;
            final boolean isDefault;

            private LangVersionMetadata() {
                this.name = "";
                this.aliases = emptyList();
                this.isDefault = true;
            }

            private LangVersionMetadata(String name, List<String> aliases, boolean isDefault) {
                checkVersionName(name);
                for (String alias : aliases) {
                    checkVersionName(alias);
                }

                this.name = name;
                this.aliases = aliases;
                this.isDefault = isDefault;
            }

            private static void checkVersionName(String name) {
                if (StringUtils.isBlank(name) || SPACE_PAT.matcher(name).find()) {
                    throw new IllegalArgumentException("Invalid version name: " + StringUtil.inSingleQuotes(name));
                }
            }
        }
    }
}
