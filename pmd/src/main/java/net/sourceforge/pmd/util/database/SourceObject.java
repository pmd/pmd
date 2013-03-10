package net.sourceforge.pmd.util.database;

/**
 * Instantiate the fields required to retrieve {@link SourceCode}.
 *
 * @author sturton
 */
public class SourceObject {

  /**
   * Schema
   * 
   */

  String schema;

  /**
   * Name
   * 
   */

  String name;

  /**
   * Type
   * 
   */

  String type;

  /**
   * Revision
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

}
