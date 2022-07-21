/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.impl.LanguageModuleWithSeveralVersions.LanguageVersionId;

/**
 * @author Clément Fournier
 */
public class LanguageModuleWithSeveralVersions<V extends Enum<V> & LanguageVersionId> extends LanguageModuleBase {

    private final List<LanguageVersion> distinctVersions;
    private final Map<String, LanguageVersion> byName;
    private final Map<LanguageVersion, V> versionToId;
    private final LanguageVersion defaultVersion;

    protected LanguageModuleWithSeveralVersions(LanguageMetadata metadata,
                                                Class<V> versionIdClass) {
        super(metadata);

        List<V> versionIds = EnumUtils.getEnumList(versionIdClass);
        List<LanguageVersion> versions = new ArrayList<>();
        Map<String, LanguageVersion> byName = new HashMap<>();
        Map<LanguageVersion, V> verToId = new HashMap<>();
        LanguageVersion defaultVersion = null;

        for (V versionId : versionIds) {
            String versionStr = versionId.getVersionString();
            LanguageVersion languageVersion = new LanguageVersion(this, versionStr, null);

            versions.add(languageVersion);

            checkNotPresent(versionStr);
            byName.put(versionStr, languageVersion);
            verToId.put(languageVersion, versionId);
            for (String alias : versionId.getAliases()) {
                checkNotPresent(alias);
                byName.put(alias, languageVersion);
            }

            if (versionId.isDefault()) {
                if (defaultVersion != null) {
                    throw new IllegalStateException(
                        "Default version already set to " + defaultVersion + ", cannot set it to " + languageVersion);
                }
                defaultVersion = languageVersion;
            }
        }
        this.byName = Collections.unmodifiableMap(byName);
        this.versionToId = Collections.unmodifiableMap(verToId);
        this.distinctVersions = Collections.unmodifiableList(versions);
        this.defaultVersion = Objects.requireNonNull(defaultVersion, "No default version for " + getId());
    }

    private void checkNotPresent(String alias) {
        if (byName.containsKey(alias)) {
            throw new IllegalArgumentException("Version key '" + alias + "' is duplicated");
        }
    }

    protected V getIdOf(LanguageVersion v) {
        V obj = versionToId.get(v);
        if (obj == null) {
            throw new NullPointerException("no such version " + v + " in " + getVersions());
        }
        return obj;
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

    protected interface LanguageVersionId {

        String getVersionString();

        boolean isDefault();

        default List<String> getAliases() {
            return Collections.emptyList();
        }
    }
}
