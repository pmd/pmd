/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;

import java.util.Locale;
import java.util.logging.Logger;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.Language;

/**
 * Instantiate the fields required to retrieve {@link SourceCode}.
 *
 * @author sturton
 */
public class SourceObject {

    private static final String CLASS_NAME = SourceObject.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS_NAME);

    /**
     * Database Schema/Owner - SYS,SYSTEM,SCOTT
     *
     */

    String schema;

    /**
     * Source Code Name - DBMS_METADATA
     *
     */

    String name;

    /**
     * Source Code Type -
     * FUNCTION,PROCEDURE,TRIGGER,PACKAGE,PACKAGE_BODY,TYPE,TYPE_BODY,JAVA_SOURCE.
     *
     */

    String type;

    /**
     * Source Code Revision - Optional revision/version
     *
     */

    String revision;

    SourceObject(String schema, String type, String name, String revision) {
        this.schema = schema;
        this.type = type;
        this.name = name;
        this.revision = revision;
    }

    @Override
    public String toString() {
        return String.format("schema=\"%s\",type=\"%s\",name=\"%s\",revision=\"%s\"", this.getSchema(), this.getType(),
                this.getName(), this.getRevision());
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema
     *            the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision
     *            the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * Map the type to a file suffix associated with a {@link Language}
     *
     * @return inferred suffix
     */
    public String getSuffixFromType() {
        LOG.entering(CLASS_NAME, "getSuffixFromType", this);
        if (null == type || type.isEmpty()) {
            return "";
        } else if (type.toUpperCase(Locale.ROOT).contains("JAVA")) {
            return ".java";
        } else if (type.toUpperCase(Locale.ROOT).contains("TRIGGER")) {
            return ".trg";
        } else if (type.toUpperCase(Locale.ROOT).contains("FUNCTION")) {
            return ".fnc";
        } else if (type.toUpperCase(Locale.ROOT).contains("PROCEDURE")) {
            return ".prc";
        } else if (type.toUpperCase(Locale.ROOT).contains("PACKAGE_BODY")) {
            return ".pkb";
        } else if (type.toUpperCase(Locale.ROOT).contains("PACKAGE")) {
            return ".pks";
        } else if (type.toUpperCase(Locale.ROOT).contains("TYPE_BODY")) {
            return ".tpb";
        } else if (type.toUpperCase(Locale.ROOT).contains("TYPE")) {
            return ".tps";
        } else {
            return "";
        }
    }

    /**
     * Gets the data source as a pseudo file name (faux-file). Adding a suffix
     * matching the source object type ensures that the appropriate language
     * parser is used.
     */
    public String getPseudoFileName() {
        return String.format("/Database/%s/%s/%s%s", getSchema(), getType(), getName(),
                getSuffixFromType());
    }
}
