/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.util.Optional;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * @author Clément Fournier
 */
public class ApexLanguageProperties extends LanguagePropertyBundle {

    public static final PropertyDescriptor<Optional<String>> MULTIFILE_DIRECTORY =
        PropertyFactory.stringProperty("rootDirectory")
                       .desc("The root directory of the Salesforce metadata, where `sfdx-project.json` resides.")
                       .defaultValue("") // is this ok?
                       .toOptional("")
                       .build();

    public ApexLanguageProperties() {
        super(ApexLanguageModule.getInstance());
        definePropertyDescriptor(MULTIFILE_DIRECTORY);
    }


}
