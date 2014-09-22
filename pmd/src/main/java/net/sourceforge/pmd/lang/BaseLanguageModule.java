package net.sourceforge.pmd.lang;

import java.util.*;

/**
 * Created by christoferdutz on 21.09.14.
 */
public abstract class BaseLanguageModule implements LanguageModule {

    protected String name;
    protected String shortName;
    protected String terseName;
    protected Class<?> ruleChainVisitorClass;
    protected List<String> extensions;
    protected Map<String, LanguageVersionModule> versions;
    protected LanguageVersionModule defaultVersion;

    public BaseLanguageModule(String name, String shortName, String terseName, Class<?> ruleChainVisitorClass, String... extensions) {
        this.name = name;
        this.shortName = shortName;
        this.terseName = terseName;
        this.ruleChainVisitorClass = ruleChainVisitorClass;
        this.extensions = Arrays.asList(extensions);
    }

    protected void addVersion(String version, LanguageVersionHandler languageVersionHandler, boolean isDefault) {
        if(versions == null) {
            versions = new HashMap<String, LanguageVersionModule>();
        }
        LanguageVersionModule languageVersion = new LanguageVersionModule(this, version, languageVersionHandler);
        versions.put(version, languageVersion);
        if(isDefault) {
            defaultVersion = languageVersion;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortName() {
        return (shortName != null) ? shortName : name;
    }

    @Override
    public String getTerseName() {
        return terseName;
    }

    @Override
    public Class<?> getRuleChainVisitorClass() {
        return ruleChainVisitorClass;
    }

    @Override
    public List<String> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    @Override
    public boolean hasExtension(String extension) {
        return extensions != null && extensions.contains(extension);
    }

    @Override
    public List<LanguageVersionModule> getVersions() {
        return new ArrayList<LanguageVersionModule>(versions.values());
    }

    @Override
    public boolean hasVersion(String version) {
        return versions != null && versions.containsKey(version);
    }

    public LanguageVersionModule getVersion(String versionName) {
        if(versions != null) {
            return versions.get(versionName);
        }
        return null;
    }

    @Override
    public LanguageVersionModule getDefaultVersion() {
        return defaultVersion;
    }

}
