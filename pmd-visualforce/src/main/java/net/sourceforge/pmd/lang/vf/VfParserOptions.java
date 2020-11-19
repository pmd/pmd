/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class VfParserOptions extends ParserOptions {
    static final List<String> DEFAULT_APEX_DIRECTORIES = Collections.singletonList(".." + File.separator + "classes");
    static final List<String> DEFAULT_OBJECT_DIRECTORIES = Collections.singletonList(".." + File.separator + "objects");

    /**
     * Directory that contains Apex classes that may be referenced from a Visualforce page.
     */
    public static final PropertyDescriptor<List<String>> APEX_DIRECTORIES_DESCRIPTOR =
            PropertyFactory.stringListProperty("apexDirectories")
                    .desc("Location of Apex Class directories. Absolute or relative to the Visualforce directory.")
                    .defaultValue(DEFAULT_APEX_DIRECTORIES)
                    .delim(',')
                    .build();

    /**
     * Directory that contains Object definitions that may be referenced from a Visualforce page.
     */
    public static final PropertyDescriptor<List<String>> OBJECTS_DIRECTORIES_DESCRIPTOR =
            PropertyFactory.stringListProperty("objectsDirectories")
                    .desc("Location of CustomObject directories. Absolute or relative to the Visualforce directory.")
                    .defaultValue(DEFAULT_OBJECT_DIRECTORIES)
                    .delim(',')
                    .build();

    public VfParserOptions() {
        super(new VfLanguageModule());
        getProperties().definePropertyDescriptor(APEX_DIRECTORIES_DESCRIPTOR);
        getProperties().definePropertyDescriptor(OBJECTS_DIRECTORIES_DESCRIPTOR);
        overridePropertiesFromEnv();
    }
}
