/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sturton
 */
public class DBURI {

private final static String CLASS_NAME = DBURI.class.getCanonicalName();

private final static Logger LOGGER = Logger.getLogger(DBURI.class.getPackage().getName()); 


  /**
   * Provide a single parameter to specify database objects to process.
   * 
   * @see http://docs.oracle.com/javase/7/docs/api/java/net/URI.html
   */

  /**
   * A JDBC URL with an associated query.
   * 
   * Formats:
   * jdbc:oracle:thin:[<user>/<password>]@//<host>[:<port>]/<service> 
   * jdbc:oracle:oci:[<user>/<password>]@//<host>[:<port>]/<service>
   *
   * Example:
   * jdbc:oracle:thin:@//myserver.com/customer_db 
   * jdbc:oracle:oci:scott/tiger@//myserver.com:5521/customer_db
   */

  private URI uri;

  private DBType dbType;

  private String url ;

  /**
   * JDBC subprotocol  
   */
  private String subprotocol ;
  
  /**
   * JDBC subname prefix   
   */
  private String subnamePrefix ;
  
  /**
   * Parameters from URI
   */
  private  Map<String, String> parameters ;

  //Schema List - defaults to connecting user
  private List<String> schemasList ;

  //Object Type List - potentially inferred from the JDBC URL
  private List<String> sourceCodeTypesList ;

  //source Code Type List 
  private List<String> sourceCodeNamesList ;

  //Language List - potentially inferred from the JDBC URL
  private List<String> languagesList ;

  //Driver Class - potentially inferred from the JDBC URL
  private String driverClass ;

  //Database CharacterSet
  private String characterSet;

  //String to get objects - defaults inferred from the JDBC URL
  private String sourceCodeTypes;

  //String to get source code - defaults inferred from the JDBC URL
  private String sourceCodeNames;
  
  //languages to process - defaults inferred from the JDBC URL
  private String languages;

  //Return class for source code, mapped fron java.sql.Types 
  private int sourceCodeType; 
  

  public DBURI(String string) throws URISyntaxException, Exception 
  {
    /*
     * A JDBC URL is an opaque URL and does not have a query.
     * 
     * We pretend that it does,
     * strip off the query,
     * use the real JDBC URL component to infer 
     *   languages
     *   JDBC driver class
     *   supported languages
     *   default source code types
     *   default schemas
     * generate a faux HTTP URI with the query,
     * extract the 
     */

    uri = new URI (string);

    try
    {
      String queryString = null ;
          
      //Split the string between JDBC URL and the query
      String[] splitURI = string.split("\\?");

      if (splitURI.length > 1)
      {
        url = splitURI[0];
      }
      else
      {
        url = string;
      }

      LOGGER.fine("Extracted URL="+url);

      //Explode URL into its separate components
      setFields() ;

      //If the original URI string contained a query component, split it into parameters  
      if (splitURI.length > 1)
      {
        //Generate a fake HTTP URI to allow easy extraction of the query parameters 
        String chimeraString = "http://local?" + string.substring(url.length()+1); 
        LOGGER.finest("chimeraString="+chimeraString);
        URI chimeraURI = new URI(chimeraString) ; 
        dump("chimeraURI",chimeraURI);
        
        parameters = getParameterMap(chimeraURI);

        LOGGER.finest("parameterMap=="+parameters);

        characterSet = parameters.get("characterset");
        sourceCodeTypes = parameters.get("sourcecodetypes");
        sourceCodeNames = parameters.get("sourcecodenames");
        languages = parameters.get("languages");

        //Populate the lists 
        if (null!=sourceCodeNames)
        {
          sourceCodeNamesList = Arrays.asList(sourceCodeNames.split(","));
        }

        if (null!=languages)
        {
          languagesList = Arrays.asList(languages.split(","));
        }

        if (null!=parameters.get("schemas"))
        {
          schemasList = Arrays.asList(parameters.get("schemas").split(","));
        }

        if (null!=sourceCodeTypes)
        {
          sourceCodeTypesList = Arrays.asList(sourceCodeTypes.split(","));
        }

      }

    } catch (URISyntaxException ex) {
      throw new Exception ( String.format("Problem generating DBURI from \"%s\"", string), ex);
    }
  }

  public DBURI(String scheme, String userInfo, String host, int port, String path, String query, String fragment)
  throws URISyntaxException
  {
   uri = new URI(scheme, userInfo, host, port, path, query, fragment);

  }

  private Map<String, String> getParameterMap (URI dburi) throws UnsupportedEncodingException {

    Map<String, String> map = new HashMap<String, String>();  
    String query = dburi.getRawQuery();
    LOGGER.finest("dburi,getQuery()="+query);
    if (null != query && !query.equals(""))
    {
      String[] params = query.split("&");  
      for (String param : params)  
      {  
          String[] splits = param.split("=");  
          String name =splits[0];
          String value = null ;
          if (splits.length > 1 ) 
          {
            value = splits[1] ;
          };  
          map.put(name, (null==value) ? value:  URLDecoder.decode(value,"UTF-8"));  
      }  
    }
    return map;  
  }

  static void dump(String description , URI dburi) {

    String dumpString = String.format(
                     "dump (%s)\n: isOpaque=%s, isAbsolute=%s Scheme=%s,\n SchemeSpecificPart=%s,\n Host=%s,\n Port=%s,\n Path=%s,\n Fragment=%s,\n Query=%s\n"
                     , description
                     , dburi.isOpaque()
                     , dburi.isAbsolute()
                     , dburi.getScheme()
                     , dburi.getSchemeSpecificPart()
                     , dburi.getHost()
                     , dburi.getPort()
                     , dburi.getPath()
                     , dburi.getFragment()
                     , dburi.getQuery()
                     );

    LOGGER.fine(dumpString);

    String query = dburi.getQuery();
    if (null != query && !query.equals(""))
    {
      String[] params = query.split("&");  
      Map<String, String> map = new HashMap<String, String>();  
      for (String param : params)  
      {  
          String[] splits = param.split("=");  
          String name =splits[0];
          String value = null ;
          if (splits.length > 1 ) 
          {
            value = splits[1] ;
          };  
          map.put(name, value);  
          LOGGER.fine(String.format("name=%s,value=%s\n",name,value));
      }  
    }
    //return map;  
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public DBType getDbType() {
    return dbType;
  }

  public void setDbType(DBType dbType) {
    this.dbType = dbType;
  }

  public List<String> getSchemasList() {
    return schemasList;
  }

  public void setSchemasList(List<String> schemasList) {
    this.schemasList = schemasList;
  }

  public List<String> getSourceCodeTypesList() {
    return sourceCodeTypesList;
  }

  public void setSourceCodeTypesList(List<String> sourceCodeTypesList) {
    this.sourceCodeTypesList = sourceCodeTypesList;
  }

  public List<String> getSourceCodeNamesList() {
    return sourceCodeNamesList;
  }

  public void setSourceCodeNamesList(List<String> sourceCodeNamesList) {
    this.sourceCodeNamesList = sourceCodeNamesList;
  }

  public List<String> getLanguagesList() {
    return languagesList;
  }

  public void setLanguagesList(List<String> languagesList) {
    this.languagesList = languagesList;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  public String getCharacterSet() {
    return characterSet;
  }

  public void setCharacterSet(String characterSet) {
    this.characterSet = characterSet;
  }

  public int getSourceCodeType() {
    return sourceCodeType;
  }

  public void setSourceCodeType(int sourceCodeType) {
    this.sourceCodeType = sourceCodeType;
  }

  public String getSubprotocol() {
    return subprotocol;
  }

  public void setSubprotocol(String subprotocol) {
    this.subprotocol = subprotocol;
  }

  public String getSubnamePrefix() {
    return subnamePrefix;
  }

  public void setSubnamePrefix(String subnamePrefix) {
    this.subnamePrefix = subnamePrefix;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  /**
   * @return the url
   */
  public String getURL() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setURL(String jdbcURL) {
    this.url = jdbcURL;
  }

  private void setFields() throws Exception, URISyntaxException {
    if (url.startsWith("jdbc:"))
    {
      //java.net.URI is intended for "normal" URLs
      URI jdbcURI = new URI(getURL().substring(5)) ; 

      LOGGER.fine("setFields - substr(jdbcURL,5):"+getURL().substring(5)) ; 
      dump("substr(jdbcURL,5)", jdbcURI);

      // jdbc:subprotocol:subname
      String[] uriParts = url.split(":"); 
      for ( String part : uriParts)
      {
        LOGGER.finest("JDBCpart="+part);
      }

      /* Expect jdbc : subprotocol  [ : subname ] : connection details  
       *  uriParts.length < 3 Error
       *  uriParts.length = 3 Driver information may be inferred from part[1] - the subprotocol
       *  uriParts.length >= 4 Driver information may be inferred from part[2]- the first part of the subname
       */ 
      if ( 3 == uriParts.length )
      {
        subprotocol = uriParts[1];
      }
      else if ( 4 <= uriParts.length )
      {
        subprotocol = uriParts[1];
        subnamePrefix = uriParts[2];
      }
      else 
      {
        throw new URISyntaxException(getURL(), "Could not understand JDBC URL",1);
      }

      LOGGER.fine("subprotocol="+subprotocol+"' subnamePrefix="+subnamePrefix);

      //Set values from DBType defaults 
      this.dbType = new DBType(subprotocol, subnamePrefix) ;

      LOGGER.finer("DBType properties found at "+dbType.getPropertiesSource() 
                   + " with " + dbType.getProperties().size() 
                   + " properties."  );

      LOGGER.finest("DBType properties are:- "+dbType.getProperties() );
                   

      if (null!= dbType.getDriverClass())
      {
        this.driverClass = dbType.getDriverClass() ;
      }

      if (null!= dbType.getCharacterSet()   )
      {
        this.characterSet = dbType.getCharacterSet() ;
      }

      if (null!= dbType.getLanguages())
      {
        this.languages = dbType.getLanguages() ;
      }

      if (null!= dbType.getSourceCodeTypes())
      {
        sourceCodeTypes = dbType.getSourceCodeTypes();
      }

      LOGGER.finer("DBType other properties follow  ...") ;

      if (null!=dbType.getProperties().getProperty("schemas") ) 
      {
        schemasList = Arrays.asList(dbType.getProperties().getProperty("schemas").split(",") ); 
      }

      sourceCodeNames = dbType.getProperties().getProperty("sourcecodenames") ;

      String returnType = dbType.getProperties().getProperty("returnType") ;
      if (null != returnType) 
      {
        sourceCodeType = Integer.parseInt(returnType);
      }

      LOGGER.finer("DBType populating lists ") ;
      //Populate the lists 
      if (null!=sourceCodeNames)
      {
        sourceCodeNamesList = Arrays.asList(sourceCodeNames.split(","));
      }

      if (null!=languages)
      {
        languagesList = Arrays.asList(languages.split(","));
      }

      if (null!=sourceCodeTypes)
      {
        sourceCodeTypesList = Arrays.asList(sourceCodeTypes.split(","));
      }

      LOGGER.finer("DBType lists generated") ;
    }

  }
  
  @Override
  public String toString()
  {
    return uri.toASCIIString();
  }
}
