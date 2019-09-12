/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.properties.builders.SingleValuePropertyBuilder;


/**
 * Property taking a File object as its value.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Will be removed with 7.0.0 with no scheduled replacement
 */
@Deprecated
public final class FileProperty extends AbstractSingleValueProperty<File> {


    /**
     * Constructor for file property.
     *
     * @param theName        Name of the property
     * @param theDescription Description
     * @param theDefault     Default value
     * @param theUIOrder     UI order
     */
    public FileProperty(String theName, String theDescription, File theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder, false);
    }


    /** Master constructor. */
    private FileProperty(String theName, String theDescription, File theDefault, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);
    }


    @Override
    public Class<File> type() {
        return File.class;
    }


    @Override
    public File createFrom(String propertyString) {
        return StringUtils.isBlank(propertyString) ? null : new File(propertyString);
    }


    public static FilePBuilder named(String name) {
        return new FilePBuilder(name);
    }


    public static final class FilePBuilder extends SingleValuePropertyBuilder<File, FilePBuilder> {
        private FilePBuilder(String name) {
            super(name);
        }


        @Override
        public FileProperty build() {
            return new FileProperty(name, description, defaultValue, uiOrder, isDefinedInXML);
        }
    }


}
