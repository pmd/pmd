/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 3:06:21 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;

public class Job implements Entry {
    public String name;
    public Integer id;

    public Job() {}

    public Job(String name, Integer id) {
        this.id = id;
        this.name = name;
    }

    public boolean equals(Object o) {
        Job other = (Job)o;
        return other.id.equals(id);
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return id + ":" + name;
    }
}
