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

    private JavaSpace space;

    public DCPDWorker() {
        try {
            space = Util.findSpace("mordor");
            space.notify(new Job("test"), null, new JobAddedListener(space, this), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jobAdded(Job job) {
        System.out.println("GOT A JOB NAMED " + job.name);
    }

    public static void main(String[] args) {
        new DCPDWorker();
    }
}
