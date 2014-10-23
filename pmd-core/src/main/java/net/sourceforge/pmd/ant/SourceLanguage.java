/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

/**
 * Stores LanguageVersion terse name value.
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

	public String getName() {
		return name;
	}
    
	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "<sourceLanguage name=\"" + this.name + "\" version=\"" + this.version + "\" />";
	}
}
