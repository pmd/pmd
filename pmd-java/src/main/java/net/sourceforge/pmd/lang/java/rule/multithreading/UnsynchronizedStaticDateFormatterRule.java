/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.text.DateFormat;

/**
 * Using a DateFormatter (SimpleDateFormatter) which is static can cause
 * unexpected results when used in a multi-threaded environment. This rule will
 * find static (Simple)DateFormatters which are used in an unsynchronized
 * manner.
 *
 * <p>Refer to these Bug Parade issues:
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4093418.html">4093418</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4228335.html">4228335</a>
 * <a href="http://developer.java.sun.com/developer/bugParade/bugs/4261469.html">4261469</a></p>
 *
 * @author Allan Caplan
 * @see <a href="https://sourceforge.net/p/pmd/feature-requests/226/">feature #226 Check for SimpleDateFormat as singleton?</a>
 * @deprecated This rule is being replaced by {@link UnsynchronizedStaticFormatterRule}. The rule will be removed with PMD 7.0.0.
 */
@Deprecated
public class UnsynchronizedStaticDateFormatterRule extends UnsynchronizedStaticFormatterRule {

    public UnsynchronizedStaticDateFormatterRule() {
        super(DateFormat.class);
    }
}
