/*
 * User: tom
 * Date: Sep 10, 2002
 * Time: 2:35:32 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.entry.Entry;

import java.util.List;

public class Chunk implements Entry {

    // routing information
    public static final Integer DONE = new Integer( 1 );
    public static final Integer NOT_DONE = new Integer( 0 );
    public Integer isDone;

    // payload
    public List tileWrappers;
    public Integer jobID;
    public Integer sequenceID;

    public Chunk() {}

    public Chunk(Integer jobID, List tileWrappers, Integer isDone, Integer sequenceID) {
        this.jobID = jobID;
        this.tileWrappers = tileWrappers;
        this.isDone = isDone;
        this.sequenceID = sequenceID;
    }

}
