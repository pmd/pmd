/*
 * User: tom
 * Date: Aug 23, 2002
 * Time: 5:19:47 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JobAddedListener extends UnicastRemoteObject implements RemoteEventListener {

    protected JavaSpace space;
    protected DCPDWorker worker;

    public JobAddedListener() throws RemoteException {}

    public JobAddedListener(JavaSpace space, DCPDWorker worker)  throws RemoteException  {
        this.space = space;
        this.worker = worker;
    }

    public void notify(RemoteEvent event) throws UnknownEventException, RemoteException {
        try {
            Job job = (Job)space.take(new Job("test"), null, 1000);
            worker.jobAdded(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
