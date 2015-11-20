/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

/**
 * Created by christoferdutz on 21.09.14.
 */
public class LanguageVersion implements Comparable<LanguageVersion> {

    private final Language language;
    private final String version;
    private final LanguageVersionHandler languageVersionHandler;

    public LanguageVersion(Language language, String version, LanguageVersionHandler languageVersionHandler) {
        this.language = language;
        this.version = version;
        this.languageVersionHandler = languageVersionHandler;
    }

    public Language getLanguage() {
        return language;
    }

    public String getVersion() {
        return version;
    }

    public LanguageVersionHandler getLanguageVersionHandler() {
        return languageVersionHandler;
    }

    /**
     * Get the name of this LanguageVersion.  This is Language name
     * appended with the LanguageVersion version if not an empty String.
     * @return The name of this LanguageVersion.
     */
    public String getName() {
        return version.length() > 0 ? language.getName() + ' ' + version : language.getName();
    }

    /**
     * Get the short name of this LanguageVersion.  This is Language short name
     * appended with the LanguageVersion version if not an empty String.
     * @return The short name of this LanguageVersion.
     */
    public String getShortName() {
        return version.length() > 0 ? language.getShortName() + ' ' + version : language.getShortName();
    }

    /**
     * Get the terse name of this LanguageVersion.  This is Language terse name
     * appended with the LanguageVersion version if not an empty String.
     * @return The terse name of this LanguageVersion.
     */
    public String getTerseName() {
        return version.length() > 0 ? language.getTerseName() + ' ' + version : language.getTerseName();
    }

    @Override
    public int compareTo(LanguageVersion o) {
        if(o == null) {
            return 1;
        }

        int comp = getName().compareTo(o.getName());
        if(comp != 0) {
            return comp;
        }

        String[] vals1 = getName().split("\\.");
        String[] vals2 = o.getName().split("\\.");
        int i = 0;
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        } else {
            return Integer.signum(vals1.length - vals2.length);
        }
    }

    @Override
    public String toString() {
        return language.toString() + "+version:" + version;
    }
}
