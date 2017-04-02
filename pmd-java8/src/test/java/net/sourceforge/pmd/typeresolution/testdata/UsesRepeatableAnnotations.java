/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.sourceforge.pmd.typeresolution.testdata.UsesRepeatableAnnotations.Multitude;

@Multitude("1")
@Multitude("2")
@Multitude("3")
@Multitude("4")
public class UsesRepeatableAnnotations {

    @Repeatable(Multitudes.class)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Multitude {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Multitudes {
        Multitude[] value();
    }

}
