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

    public class MyListener implements RemoteEventListener {

        public MyListener() {}

        public void notify(RemoteEvent event) throws UnknownEventException, RemoteException {
            System.out.println("HOWDY!");
            try {
                Job job = (Job)space.take(new Job("test"), null, 1000);
                if (job == null) {
                    System.out.println("No job found");
                } else {
                    System.out.println("job = " + job.name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JavaSpace space;

    public DCPDWorker() {
        try {
            space = Util.findSpace("mordor");
            space.notify(new Job("test"), null, new MyListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DCPDWorker();
    }
}
