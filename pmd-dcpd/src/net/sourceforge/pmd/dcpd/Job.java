/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 3:06:21 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;

public class Job implements Entry {
    public String name;

    public Job() {}

    public Job(String name) {
        this.name = name;
    }
}
