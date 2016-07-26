/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Encapsulate the settings needed to access database source code.
 * 
 * 
 * @author sturton
 */
public class DBType {
    private static final String CLASS_NAME = DBType.class.getCanonicalName();

    private static final Logger LOGGER = Logger.getLogger(DBType.class.getPackage().getName());

    private static final String INTERNAL_SETTINGS = "[Internal Settings]";

    /**
     * The names of the properties
     */
    public enum property {
        USER("user", "Name of the connecting database user"), PASSWORD("password",
                "The connecting database user's password"), DRIVER("driver", "JDBC driver classname"), CHARACTERSET(
                "characterset", "Reader character set"), LANGUAGES("languages",
                "Comma-separated list of PMD-supported languages"), SCHEMAS("schemas",
                "SchemaSpy compatible regular expression for schemas to be processed"), SOURCE_TYPES("sourcecodetypes",
                "Comma-separated list of supported source types"), SOURCE_NAMES("sourcecodenames",
                "Default comma-separated list of source code names to validate"), GET_SOURCE_CODE_STATEMENT(
                "getSourceCodeStatement",
                "SQL92 or Oracle embedded SQL statement to retrieve  code source from the database catalogue"), RETURN_TYPE(
                "returnType", "int equivalent of java.sql.Types return type of getSourceCodeStatement");

        private String name;
        private String description;

        private property(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getPropertyName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Where the properties were taken from
     */
    private String propertiesSource;

    /**
     * Parameters from Properties
     */
    private Properties properties;

    // Driver Class
    private String driverClass;

    // Database CharacterSet
    private String characterSet;

    // String to get objects
    private String sourceCodeTypes;

    // Languages to process
    private String languages;

    // Return class for source code
    private int sourceCodeReturnType;

    /**
     * 
     * @param dbType
     */
    public DBType(String dbType) throws Exception {
        properties = loadDBProperties(dbType);
    }

    /**
     * Load the most specific dbType for the protocol
     * 
     * @param subProtocol
     * @param subnamePrefix
     * @throws IOException
     */
    public DBType(String subProtocol, String subnamePrefix) throws IOException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("subProtocol=" + subProtocol + ", subnamePrefix=" + subnamePrefix);
        }

        if (null == subProtocol && null == subnamePrefix) {
            throw new RuntimeException("subProtocol and subnamePrefix cannot both be null");
        } else {

            properties = null;

            // Attempt subnamePrefix before subProtocol
            if (subnamePrefix != null) {
                properties = loadDBProperties(subnamePrefix);
            }
            if (properties == null && subProtocol != null) {
                properties = loadDBProperties(subProtocol);
            }

            if (subnamePrefix != null && properties != null) {
                LOGGER.log(Level.FINE, "DBType found using subnamePrefix={0}", subnamePrefix);
            } else if (subProtocol != null && properties != null) {
                LOGGER.log(Level.FINE, "DBType found using subProtocol={0}", subProtocol);
            } else {
                throw new RuntimeException(String.format(
                        "Could not locate DBType properties using subProtocol=%s and subnamePrefix=%s", subProtocol,
                        subnamePrefix));
            }

        }
    }

    public Properties getProperties() {

        return properties;
    }

    /**
     * Load properties from one or more files or resources.
     * 
     * <p>
     * This method recursively finds property files or JAR resources matching
     * {@matchstring}.
     * </p>
     * .
     * <p>
     * The method is intended to be able to use , so any
     *
     * @param matchString
     * @return "current" set of properties (from one or more resources/property
     *         files)
     */
    private Properties loadDBProperties(String matchString) throws IOException {
        LOGGER.entering(CLASS_NAME, matchString);
        // Locale locale = Control.g;
        ResourceBundle resourceBundle = null;
        InputStream stream = null;

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("class_path+" + System.getProperty("java.class.path"));
        }

        /*
         * Attempt to match properties files in this order:- File path with
         * properties suffix File path without properties suffix Resource
         * without class prefix Resource with class prefix
         */
        try {
            File propertiesFile = new File(matchString);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("Attempting File no file suffix: " + matchString);
            }
            stream = new FileInputStream(propertiesFile);
            resourceBundle = new PropertyResourceBundle(stream);
            propertiesSource = propertiesFile.getAbsolutePath();
            LOGGER.finest("FileSystemWithoutExtension");
        } catch (FileNotFoundException notFoundOnFilesystemWithoutExtension) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("notFoundOnFilesystemWithoutExtension");
                LOGGER.finest("Attempting File with added file suffix: " + matchString + ".properties");
            }
            try {
                File propertiesFile = new File(matchString + ".properties");
                stream = new FileInputStream(propertiesFile);
                resourceBundle = new PropertyResourceBundle(stream);
                propertiesSource = propertiesFile.getAbsolutePath();
                LOGGER.finest("FileSystemWithExtension");
            } catch (FileNotFoundException notFoundOnFilesystemWithExtensionTackedOn) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.finest("Attempting JARWithoutClassPrefix: " + matchString);
                }
                try {
                    resourceBundle = ResourceBundle.getBundle(matchString);
                    propertiesSource = "[" + INTERNAL_SETTINGS + "]" + File.separator + matchString + ".properties";
                    LOGGER.finest("InJarWithoutPath");
                } catch (Exception notInJarWithoutPath) {
                    if (LOGGER.isLoggable(Level.FINEST)) {
                        LOGGER.finest("Attempting JARWithClass prefix: " + DBType.CLASS_NAME + "." + matchString);
                    }
                    try {
                        resourceBundle = ResourceBundle.getBundle(DBType.CLASS_NAME + "." + matchString);
                        propertiesSource = "[" + INTERNAL_SETTINGS + "]" + File.separator + matchString + ".properties";
                        LOGGER.finest("found InJarWithPath");
                    } catch (Exception notInJarWithPath) {
                        notInJarWithPath.printStackTrace();
                        notFoundOnFilesystemWithExtensionTackedOn.printStackTrace();
                        throw new RuntimeException(" Could not locate DBTYpe settings : " + matchString,
                                notInJarWithPath);
                    }
                }
            }
        } finally {
            IOUtils.closeQuietly(stream);
        }

        // Properties in this matched resource
        Properties matchedProperties = getResourceBundleAsProperties(resourceBundle);
        resourceBundle = null;
        String saveLoadedFrom = getPropertiesSource();

        /*
         * If the matched properties contain the "extends" key, use the value as
         * a matchstring, to recursively set the properties before overwriting
         * any previous properties with the matched properties.
         */
        String extendedPropertyFile = (String) matchedProperties.remove("extends");
        if (null != extendedPropertyFile && !"".equals(extendedPropertyFile.trim())) {
            Properties extendedProperties = loadDBProperties(extendedPropertyFile.trim());

            // Overwrite extended properties with properties in the matched
            // resource
            extendedProperties.putAll(matchedProperties);
            matchedProperties = extendedProperties;
        }

        /*
         * Record the location of the original matched resource/property file,
         * and the current set of properties secured.
         */
        propertiesSource = saveLoadedFrom;
        setProperties(matchedProperties);

        return matchedProperties;
    }

    /**
     * Options that are specific to a type of database. E.g. things like
     * <code>host</code>, <code>port</code> or <code>db</code>, but <b>don't</b>
     * have a setter in this class.
     *
     * @param dbSpecificOptions
     */

    /**
     * Convert <code>resourceBundle</code> to usable {@Properties}.
     *
     * @param resourceBundle ResourceBundle
     * @return Properties
     */
    public static Properties getResourceBundleAsProperties(ResourceBundle resourceBundle) {
        Properties properties = new Properties();
        for (String key : resourceBundle.keySet()) {
            properties.put(key, resourceBundle.getObject(key));
        }

        return properties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((characterSet == null) ? 0 : characterSet.hashCode());
        result = prime * result + ((driverClass == null) ? 0 : driverClass.hashCode());
        result = prime * result + ((languages == null) ? 0 : languages.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((propertiesSource == null) ? 0 : propertiesSource.hashCode());
        result = prime * result + sourceCodeReturnType;
        result = prime * result + ((sourceCodeTypes == null) ? 0 : sourceCodeTypes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DBType other = (DBType) obj;
        if (characterSet == null) {
            if (other.characterSet != null) {
                return false;
            }
        } else if (!characterSet.equals(other.characterSet)) {
            return false;
        }
        if (driverClass == null) {
            if (other.driverClass != null) {
                return false;
            }
        } else if (!driverClass.equals(other.driverClass)) {
            return false;
        }
        if (languages == null) {
            if (other.languages != null) {
                return false;
            }
        } else if (!languages.equals(other.languages)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        if (propertiesSource == null) {
            if (other.propertiesSource != null) {
                return false;
            }
        } else if (!propertiesSource.equals(other.propertiesSource)) {
            return false;
        }
        if (sourceCodeReturnType != other.sourceCodeReturnType) {
            return false;
        }
        if (sourceCodeTypes == null) {
            if (other.sourceCodeTypes != null) {
                return false;
            }
        } else if (!sourceCodeTypes.equals(other.sourceCodeTypes)) {
            return false;
        }
        return true;
    }

    /**
     * @return the driverClass
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * @return the characterSet
     */
    public String getCharacterSet() {
        return characterSet;
    }

    /**
     * @return the sourceCodeTypes
     */
    public String getSourceCodeTypes() {
        return sourceCodeTypes;
    }

    /**
     * @return the languages
     */
    public String getLanguages() {
        return languages;
    }

    /**
     * @return the sourceCodeReturnType
     */
    public int getSourceCodeReturnType() {
        return sourceCodeReturnType;
    }

    /**
     * @return the propertiesSource
     */
    public String getPropertiesSource() {
        return propertiesSource;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;

        // Driver Class
        if (null != this.properties.getProperty("driver")) {
            this.driverClass = this.properties.getProperty("driver");
        }

        // Database CharacterSet
        if (null != this.properties.getProperty("characterset")) {
            this.characterSet = this.properties.getProperty("characterset");
        }

        // String to get objects
        if (null != this.properties.getProperty("sourcecodetypes")) {
            this.sourceCodeTypes = this.properties.getProperty("sourcecodetypes");
        }

        // Languages to process
        if (null != this.properties.getProperty("languages")) {
            this.languages = this.properties.getProperty("languages");
        }

        // Return class for source code
        if (null != this.properties.getProperty("returnType")) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest("returnType" + this.properties.getProperty("returnType"));
            }
            this.sourceCodeReturnType = Integer.parseInt(this.properties.getProperty("returnType"));
        }

    }

    @Override
    public String toString() {
        return CLASS_NAME + "@" + propertiesSource;
    }
}
