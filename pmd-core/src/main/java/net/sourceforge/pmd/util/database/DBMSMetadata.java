/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.database;

import java.net.MalformedURLException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrap JDBC connection for use by PMD: {@link DBURI} parameters specify the
 * source code to be passed to PMD.
 *
 * @author sturton
 */
public class DBMSMetadata {
    /**
     * Local logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DBMSMetadata.class);

    /**
     * Optional DBType property specifying a query to fetch the Source Objects
     * from the database.
     *
     * <p>
     * If the DBType lacks this property, then the standard
     * DatabaseMetaData.getProcedures method is used.
     * </p>
     */
    private static final String GET_SOURCE_OBJECTS_STATEMENT = "getSourceObjectsStatement";

    /**
     * Essential DBType property specifying a CallableStatement to retrieve the
     * Source Object's code from the database.
     *
     * <p>
     * <b>If the DBType lacks this property, there is no DatabaseMetaData method
     * to fallback to</b>.
     * </p>
     */
    private static final String GET_SOURCE_CODE_STATEMENT = "getSourceCodeStatement";

    /**
     * DBURI
     */
    protected DBURI dburi = null;

    /**
     * Connection management
     */
    protected Connection connection = null;

    /**
     * Procedural statement to return list of source code objects.
     */
    protected String returnSourceCodeObjectsStatement = null;

    /**
     * Procedural statement to return source code.
     */
    protected String returnSourceCodeStatement = null;

    /**
     * CallableStatement to return source code.
     */
    protected CallableStatement callableStatement = null;

    /**
     * {@link java.sql.Types} value representing the type returned by
     * {@link #callableStatement}
     *
     * <b>Currently only java.sql.Types.String and java.sql.Types.Clob are
     * supported</b>
     */
    protected int returnType = java.sql.Types.CLOB;

    /* constructors */
    /**
     * Minimal constructor
     *
     * @param c
     *            JDBC Connection
     * @throws SQLException
     */
    public DBMSMetadata(Connection c) throws SQLException {
        connection = c;
    }

    /**
     * Define database connection and source code to retrieve with explicit
     * database username and password.
     *
     * @param user
     *            Database username
     * @param password
     *            Database password
     * @param dbURI
     *            {@link DBURI } containing JDBC connection plus parameters to
     *            specify source code.
     * @throws SQLException
     *             on failing to create JDBC connection
     * @throws MalformedURLException
     *             on attempting to connect with malformed JDBC URL
     * @throws ClassNotFoundException
     *             on failing to locate the JDBC driver class.
     */
    public DBMSMetadata(String user, String password, DBURI dbURI)
            throws SQLException, MalformedURLException, ClassNotFoundException {
        String urlString = init(dbURI);

        Properties mergedProperties = dbURI.getDbType().getProperties();
        Map<String, String> dbURIParameters = dbURI.getParameters();
        mergedProperties.putAll(dbURIParameters);
        mergedProperties.put("user", user);
        mergedProperties.put("password", password);

        connection = DriverManager.getConnection(urlString, mergedProperties);
        LOG.debug("we have a connection={}", connection);
    }

    /**
     * Define database connection and source code to retrieve with database
     * properties.
     *
     * @param properties
     *            database settings such as database username, password
     * @param dbURI
     *            {@link DBURI } containing JDBC connection plus parameters to
     *            specify source code.
     * @throws SQLException
     *             on failing to create JDBC connection
     * @throws MalformedURLException
     *             on attempting to connect with malformed JDBC URL
     * @throws ClassNotFoundException
     *             on failing to locate the JDBC driver class.
     */
    public DBMSMetadata(Properties properties, DBURI dbURI)
            throws SQLException, MalformedURLException, ClassNotFoundException {
        String urlString = init(dbURI);

        Properties mergedProperties = dbURI.getDbType().getProperties();
        Map<String, String> dbURIParameters = dbURI.getParameters();
        mergedProperties.putAll(dbURIParameters);
        mergedProperties.putAll(properties);

        LOG.debug("Retrieving connection for urlString={}", urlString);
        connection = DriverManager.getConnection(urlString, mergedProperties);
        LOG.debug("Secured Connection for DBURI={}", dbURI);
    }

    /**
     * Define database connection and source code to retrieve.
     *
     * <p>
     * This constructor is reliant on database username and password embedded in
     * the JDBC URL or defaulted from the {@link DBURI}'s <code>DriverType</code>.
     * </p>
     *
     * @param dbURI
     *            {@link DBURI } containing JDBC connection plus parameters to
     *            specify source code.
     * @throws SQLException
     *             on failing to create JDBC connection
     * @throws ClassNotFoundException
     *             on failing to locate the JDBC driver class.
     */
    public DBMSMetadata(DBURI dbURI) throws SQLException, ClassNotFoundException {
        String urlString = init(dbURI);

        Properties dbURIProperties = dbURI.getDbType().getProperties();
        Map<String, String> dbURIParameters = dbURI.getParameters();

        /*
         * Overwrite any DBType properties with DBURI parameters allowing JDBC
         * connection properties to be inherited from DBType or passed as DBURI
         * parameters
         */
        dbURIProperties.putAll(dbURIParameters);

        connection = DriverManager.getConnection(urlString, dbURIProperties);
    }

    /**
     * Return JDBC Connection for direct JDBC access to the specified database.
     *
     * @return I=JDBC Connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return connection;
    }

    private String init(DBURI dbURI) throws ClassNotFoundException {
        this.dburi = dbURI;
        this.returnSourceCodeObjectsStatement = dbURI.getDbType().getProperties()
                .getProperty(GET_SOURCE_OBJECTS_STATEMENT);
        this.returnSourceCodeStatement = dbURI.getDbType().getProperties().getProperty(GET_SOURCE_CODE_STATEMENT);
        this.returnType = dbURI.getSourceCodeType();
        LOG.debug("returnSourceCodeStatement={}, returnType={}", returnSourceCodeStatement, returnType);

        String driverClass = dbURI.getDriverClass();
        String urlString = dbURI.getURL().toString();
        LOG.debug("driverClass={}, urlString={}", driverClass, urlString);
        Class.forName(driverClass);
        LOG.debug("Located class for driverClass={}", driverClass);
        return urlString;
    }

    /**
     * Return source code text from the database.
     *
     * @param sourceObject object
     * @return source code
     * @throws SQLException
     */
    public java.io.Reader getSourceCode(SourceObject sourceObject) throws SQLException {
        return getSourceCode(sourceObject.getType(), sourceObject.getName(), sourceObject.getSchema());

    }

    /**
     * return source code text
     *
     * @param objectType
     * @param name
     *            Source Code name
     * @param schema
     *            Owner of the code
     * @return Source code text.
     * @throws SQLException
     *             on failing to retrieve the source Code text
     */
    public java.io.Reader getSourceCode(String objectType, String name, String schema) throws SQLException {
        Object result;

        /*
         * Only define callableStatement once and reuse it for subsequent calls
         * to getSourceCode()
         */
        if (null == callableStatement) {
            LOG.trace("getSourceCode: returnSourceCodeStatement=\"{}\"", returnSourceCodeStatement);
            LOG.trace("getSourceCode: returnType=\"{}\"", returnType);
            callableStatement = getConnection().prepareCall(returnSourceCodeStatement);
            callableStatement.registerOutParameter(1, returnType);
        }

        // set IN parameters
        callableStatement.setString(2, objectType);
        callableStatement.setString(3, name);
        callableStatement.setString(4, schema);
        //
        // execute statement
        callableStatement.executeUpdate();
        // retrieve OUT parameters
        result = callableStatement.getObject(1);

        return (java.sql.Types.CLOB == returnType) ? ((Clob) result).getCharacterStream()
                : new java.io.StringReader(result.toString());
    }

    /**
     * Return all source code objects associated with any associated DBURI.
     *
     * @return
     */
    public List<SourceObject> getSourceObjectList() {

        if (null == dburi) {
            LOG.warn("No dbUri defined - no further action possible");
            return Collections.emptyList();
        } else {
            return getSourceObjectList(dburi.getLanguagesList(), dburi.getSchemasList(), dburi.getSourceCodeTypesList(),
                    dburi.getSourceCodeNamesList());
        }

    }

    /**
     * Return all source code objects associated with the specified languages,
     * schemas, source code types and source code names.
     *
     * <p>
     * Each parameter may be null and the appropriate field from any related
     * DBURI is assigned, defaulting to the normal SQL wildcard expression
     * ("%").
     * </p>
     *
     * @param languages
     *            Optional list of languages to search for
     * @param schemas
     *            Optional list of schemas to search for
     * @param sourceCodeTypes
     *            Optional list of source code types to search for
     * @param sourceCodeNames
     *            Optional list of source code names to search for
     */
    public List<SourceObject> getSourceObjectList(List<String> languages, List<String> schemas,
            List<String> sourceCodeTypes, List<String> sourceCodeNames) {

        List<SourceObject> sourceObjectsList = new ArrayList<>();

        List<String> searchLanguages = languages;
        List<String> searchSchemas = schemas;
        List<String> searchSourceCodeTypes = sourceCodeTypes;
        List<String> searchSourceCodeNames = sourceCodeNames;
        List<String> wildcardList = Arrays.asList("%");

        /*
         * Assign each search list to the first
         *
         * explicit parameter dburi field wildcard list
         *
         */
        if (null == searchLanguages) {
            List<String> dbURIList = (null == dburi) ? null : dburi.getLanguagesList();
            if (null == dbURIList || dbURIList.isEmpty()) {
                searchLanguages = wildcardList;
            } else {
                searchLanguages = dbURIList;
            }
        }

        if (null == searchSchemas) {
            List<String> dbURIList = (null == dburi) ? null : dburi.getSchemasList();
            if (null == dbURIList || dbURIList.isEmpty()) {
                searchSchemas = wildcardList;
            } else {
                searchSchemas = dbURIList;
            }
        }

        if (null == searchSourceCodeTypes) {
            List<String> dbURIList = (null == dburi) ? null : dburi.getSourceCodeTypesList();
            if (null == dbURIList || dbURIList.isEmpty()) {
                searchSourceCodeTypes = wildcardList;
            } else {
                searchSourceCodeTypes = dbURIList;
            }
        }

        if (null == searchSourceCodeNames) {
            List<String> dbURIList = (null == dburi) ? null : dburi.getSourceCodeNamesList();
            if (null == dbURIList || dbURIList.isEmpty()) {
                searchSourceCodeNames = wildcardList;
            } else {
                searchSourceCodeNames = dbURIList;
            }
        }

        try {

            if (null != returnSourceCodeObjectsStatement) {
                LOG.debug("Have bespoke returnSourceCodeObjectsStatement from DBURI: \"{}\"",
                        returnSourceCodeObjectsStatement);
                try (PreparedStatement sourceCodeObjectsStatement = getConnection()
                        .prepareStatement(returnSourceCodeObjectsStatement)) {
                    for (String language : searchLanguages) {
                        for (String schema : searchSchemas) {
                            for (String sourceCodeType : searchSourceCodeTypes) {
                                for (String sourceCodeName : searchSourceCodeNames) {
                                    sourceObjectsList.addAll(findSourceObjects(sourceCodeObjectsStatement, language, schema,
                                            sourceCodeType, sourceCodeName));
                                }
                            }
                        }
                    }
                }
            } else {
                // Use standard DatabaseMetaData interface
                LOG.debug(
                        "Have dbUri - no returnSourceCodeObjectsStatement, reverting to DatabaseMetaData.getProcedures(...)");

                DatabaseMetaData metadata = connection.getMetaData();
                List<String> schemasList = dburi.getSchemasList();
                for (String schema : schemasList) {
                    for (String sourceCodeName : dburi.getSourceCodeNamesList()) {
                        sourceObjectsList.addAll(findSourceObjectFromMetaData(metadata, schema, sourceCodeName));
                    }
                }
            }

            LOG.trace("Identfied={} sourceObjects", sourceObjectsList.size());

            return sourceObjectsList;
        } catch (SQLException sqle) {
            throw new RuntimeException("Problem collecting list of source code objects", sqle);
        }
    }

    private List<SourceObject> findSourceObjectFromMetaData(DatabaseMetaData metadata,
            String schema, String sourceCodeName) throws SQLException {
        List<SourceObject> sourceObjectsList = new ArrayList<>();
        /*
         * public ResultSet getProcedures(String catalog ,
         * String schemaPattern , String procedureNamePattern)
         * throws SQLException
         */
        try (ResultSet sourceCodeObjects = metadata.getProcedures(null, schema, sourceCodeName)) {
            /*
             * From Javadoc .... Each procedure description has the
             * the following columns: PROCEDURE_CAT String =>
             * procedure catalog (may be null) PROCEDURE_SCHEM
             * String => procedure schema (may be null)
             * PROCEDURE_NAME String => procedure name reserved for
             * future use reserved for future use reserved for
             * future use REMARKS String => explanatory comment on
             * the procedure PROCEDURE_TYPE short => kind of
             * procedure: procedureResultUnknown - Cannot determine
             * if a return value will be returned procedureNoResult
             * - Does not return a return value
             * procedureReturnsResult - Returns a return value
             * SPECIFIC_NAME String => The name which uniquely
             * identifies this procedure within its schema.
             *
             * Oracle getProcedures actually returns these 8
             * columns:- ResultSet "Matched Procedures" has 8
             * columns and contains ...
             * [PROCEDURE_CAT,PROCEDURE_SCHEM,PROCEDURE_NAME,NULL,
             * NULL,NULL,REMARKS,PROCEDURE_TYPE
             * ,null,PHPDEMO,ADD_JOB_HISTORY,null,null,null,
             * Standalone procedure or function,1
             * ,FETCHPERFPKG,PHPDEMO,BULKSELECTPRC,null,null,null,
             * Packaged function,2
             * ,FETCHPERFPKG,PHPDEMO,BULKSELECTPRC,null,null,null,
             * Packaged procedure,1
             * ,null,PHPDEMO,CITY_LIST,null,null,null,Standalone
             * procedure or function,1
             * ,null,PHPDEMO,EDDISCOUNT,null,null,null,Standalone
             * procedure or function,2
             * ,SELPKG_BA,PHPDEMO,EMPSELBULK,null,null,null,Packaged
             * function,2
             * ,SELPKG_BA,PHPDEMO,EMPSELBULK,null,null,null,Packaged
             * procedure,1
             * ,INSPKG,PHPDEMO,INSFORALL,null,null,null,Packaged
             * procedure,1
             * ,null,PHPDEMO,MYDOFETCH,null,null,null,Standalone
             * procedure or function,2
             * ,null,PHPDEMO,MYPROC1,null,null,null,Standalone
             * procedure or function,1
             * ,null,PHPDEMO,MYPROC2,null,null,null,Standalone
             * procedure or function,1
             * ,null,PHPDEMO,MYXAQUERY,null,null,null,Standalone
             * procedure or function,1
             * ,null,PHPDEMO,POLICY_VPDPARTS,null,null,null,
             * Standalone procedure or function,2
             * ,FETCHPERFPKG,PHPDEMO,REFCURPRC,null,null,null,
             * Packaged procedure,1
             * ,null,PHPDEMO,SECURE_DML,null,null,null,Standalone
             * procedure or function,1 ... ]
             */
            while (sourceCodeObjects.next()) {
                LOG.trace("Located schema={},object_type={},object_name={}",
                        sourceCodeObjects.getString("PROCEDURE_SCHEM"),
                        sourceCodeObjects.getString("PROCEDURE_TYPE"),
                        sourceCodeObjects.getString("PROCEDURE_NAME"));

                sourceObjectsList.add(new SourceObject(sourceCodeObjects.getString("PROCEDURE_SCHEM"),
                        sourceCodeObjects.getString("PROCEDURE_TYPE"),
                        sourceCodeObjects.getString("PROCEDURE_NAME"), null));
            }
        }
        return sourceObjectsList;
    }

    private List<SourceObject> findSourceObjects(PreparedStatement sourceCodeObjectsStatement,
            String language, String schema, String sourceCodeType, String sourceCodeName) throws SQLException {
        List<SourceObject> sourceObjectsList = new ArrayList<>();
        sourceCodeObjectsStatement.setString(1, language);
        sourceCodeObjectsStatement.setString(2, schema);
        sourceCodeObjectsStatement.setString(3, sourceCodeType);
        sourceCodeObjectsStatement.setString(4, sourceCodeName);
        LOG.debug(
                "searching for language=\"{}\", schema=\"{}\", sourceCodeType=\"{}\", sourceCodeNames=\"{}\" ",
                language, schema, sourceCodeType, sourceCodeName);

        /*
         * public ResultSet getProcedures(String catalog
         * , String schemaPattern , String
         * procedureNamePattern) throws SQLException
         */
        try (ResultSet sourceCodeObjects = sourceCodeObjectsStatement.executeQuery()) {

            /*
             * From Javadoc .... Each procedure description
             * has the the following columns: PROCEDURE_CAT
             * String => procedure catalog (may be null)
             * PROCEDURE_SCHEM String => procedure schema
             * (may be null) PROCEDURE_NAME String =>
             * procedure name reserved for future use
             * reserved for future use reserved for future
             * use REMARKS String => explanatory comment on
             * the procedure PROCEDURE_TYPE short => kind of
             * procedure: procedureResultUnknown - Cannot
             * determine if a return value will be returned
             * procedureNoResult - Does not return a return
             * value procedureReturnsResult - Returns a
             * return value SPECIFIC_NAME String => The name
             * which uniquely identifies this procedure
             * within its schema.
             */
            while (sourceCodeObjects.next()) {
                LOG.trace("Found schema={},object_type={},object_name={}",
                        sourceCodeObjects.getString("PROCEDURE_SCHEM"),
                        sourceCodeObjects.getString("PROCEDURE_TYPE"),
                        sourceCodeObjects.getString("PROCEDURE_NAME"));

                sourceObjectsList
                        .add(new SourceObject(sourceCodeObjects.getString("PROCEDURE_SCHEM"),
                                sourceCodeObjects.getString("PROCEDURE_TYPE"),
                                sourceCodeObjects.getString("PROCEDURE_NAME"), null));
            }
        }
        return sourceObjectsList;
    }
}
