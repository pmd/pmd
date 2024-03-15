/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import java.nio.file.Paths;
import java.util.List;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author Clément Fournier
 */
public class VfLanguageProperties extends LanguagePropertyBundle {

    /**
     * Directory that contains Apex classes that may be referenced from a Visualforce page.
     *
     * <p>Env variable is {@code PMD_VF_APEX_DIRECTORIES}.
     */
    public static final PropertyDescriptor<List<String>> APEX_DIRECTORIES_DESCRIPTOR =
        PropertyFactory.stringListProperty("apexDirectories")
                       .desc("Location of Apex Class directories. Absolute or relative to the Visualforce directory.")
                       .defaultValues(Paths.get("..", "classes").toString())
                       .build();

    /**
     * Directory that contains Object definitions that may be referenced from a Visualforce page.
     *
     * <p>Env variable is {@code PMD_VF_OBJECTS_DIRECTORIES}.
     */
    public static final PropertyDescriptor<List<String>> OBJECTS_DIRECTORIES_DESCRIPTOR =
        PropertyFactory.stringListProperty("objectsDirectories")
                       .desc("Location of Custom Object directories. Absolute or relative to the Visualforce directory.")
                       .defaultValues(Paths.get("..", "objects").toString())
                       .build();

    public VfLanguageProperties() {
        super(VfLanguageModule.getInstance());
        definePropertyDescriptor(APEX_DIRECTORIES_DESCRIPTOR);
        definePropertyDescriptor(OBJECTS_DIRECTORIES_DESCRIPTOR);
    }
}
