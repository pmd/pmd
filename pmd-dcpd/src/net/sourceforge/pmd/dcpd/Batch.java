/*
 * User: tom
 * Date: Sep 10, 2002
 * Time: 2:35:32 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;

import java.util.List;

public class Batch implements Entry {

    // routing information
    public static final Integer DONE = new Integer( 1 );
    public static final Integer NOT_DONE = new Integer( 0 );
    public Integer isDone;

    // payload
    public List tileWrappers;
    public Job job;
    public Integer sequenceID;

    public Batch() {}

    public Batch(Job job, List tileWrappers, Integer isDone, Integer sequenceID) {
        this.job = job;
        this.tileWrappers = tileWrappers;
        this.isDone = isDone;
        this.sequenceID = sequenceID;
    }

}
