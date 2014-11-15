/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;

import java.util.logging.Logger;

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
   * Source Code Type - FUNCTION,PROCEDURE,TRIGGER,PACKAGE,PACKAGE_BODY,TYPE,TYPE_BODY,JAVA_SOURCE.
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
  public String toString()
  {
     return String.format("schema=\"%s\",type=\"%s\",name=\"%s\",revision=\"%s\""
                            , this.getSchema(), this.getType(), this.getName(), this.getRevision());
  }

  /**
   * @return the schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * @param schema the schema to set
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
   * @param name the name to set
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
   * @param type the type to set
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
   * @param revision the revision to set
   */
  public void setRevision(String revision) {
    this.revision = revision;
  }

  /**
   * Map the type to a file suffix associated with a {@link Language}
   * 
   * @return inferred suffix
   */
  public String getSuffixFromType()
  {
    LOG.entering(CLASS_NAME, "getSuffixFromType", this);//.entering("type="+type.toUpperCase());
    if (null == type || type.isEmpty()) {
      return "";
    } else if (type.toUpperCase().indexOf("JAVA") >= 0) {
      return ".java";
    } else if (type.toUpperCase().indexOf("TRIGGER") >= 0) {
      return ".trg";
    } else if (type.toUpperCase().indexOf("FUNCTION") >= 0) {
      return ".fnc";
    } else if (type.toUpperCase().indexOf("PROCEDURE") >= 0) {
      return ".prc";
    } else if (type.toUpperCase().indexOf("PACKAGE_BODY") >= 0) {
      return ".pkb";
    } else if (type.toUpperCase().indexOf("PACKAGE") >= 0) {
      return ".pks";
    } else if (type.toUpperCase().indexOf("TYPE_BODY") >= 0) {
      return ".tpb";
    } else if (type.toUpperCase().indexOf("TYPE") >= 0) {
      return ".tps";
    } else { 
      return "";
    }
  }

    /**
     * Gets the data source as a pseudo file name (faux-file).
     * Adding a suffix matching the source object type ensures that the appropriate
     * language parser is used.
     */
    public String getPseudoFileName() {
        String falseFilePath = String.format("/Database/%s/%s/%s%s", getSchema(), getType(), getName(),
                getSuffixFromType());
        return falseFilePath;
    }
}
