/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 5:13:14 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;

import java.rmi.RemoteException;
import java.rmi.MarshalledObject;

public class DCPDWorker {

    private Job currentJob;
    private TokenSetsWrapper tsw;
    private JavaSpace space;

    public DCPDWorker() {
        try {
            space = Util.findSpace("mordor");
            // register for future jobs
            space.notify(new Job(), null, new JobAddedListener(space, this), Lease.FOREVER, null);
            // get a job if there are any out there
            Job job = (Job)space.readIfExists(new Job(), null, 200);
            if (job != null) {
                jobAdded(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jobAdded(Job job) {
        try {
            currentJob = job;
            System.out.println("Got a job " + job.name + " , id is " + job.id.intValue());
            tsw = (TokenSetsWrapper)space.read(new TokenSetsWrapper(null, job.id), null, 200);
            System.out.println("Got a tokens sets with " + tsw.tss.size() + " token lists");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DCPDWorker();
    }
}
