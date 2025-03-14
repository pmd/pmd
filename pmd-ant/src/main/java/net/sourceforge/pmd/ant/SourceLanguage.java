/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.lang.Language;

/**
 * Part of PMD Ant task configuration. Setters of this class are interpreted by Ant as properties
 * settable in the XML. This is therefore published API.
 *
 * <p>This class is used to configure the language and version to use.
 * It might look like this:
 *
 * <pre>{@code
 * <pmd>
 *   <sourceLanguage name="java" version="21"/>
 * </pmd>
 * }</pre>
 *
 * @see PMDTask#addConfiguredSourceLanguage(SourceLanguage)
 */
public class SourceLanguage {

    private String name;
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /** This actually corresponds to {@link Language#getId()}. */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "<sourceLanguage name=\"" + this.name + "\" version=\"" + this.version + "\" />";
    }
}
