/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 11:06:40 AM
 */
package test.net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.lease.Lease;
import net.sourceforge.pmd.cpd.TokenSets;
import net.sourceforge.pmd.dcpd.TokenSetsWrapper;

import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

public class MockJavaSpace implements JavaSpace {

    private List tileWrappers;
    private List writtenEntries = new ArrayList();

    public void setTileWrappers(List tileWrappers) {
        this.tileWrappers = tileWrappers;
    }

    public List getWrittenEntries() {
        return this.writtenEntries;
    }

    // JavaSpace
    public EventRegistration notify(Entry entry, Transaction transaction, RemoteEventListener listener, long l, MarshalledObject object) throws TransactionException, RemoteException {
        return null;
    }

    public Entry read(Entry entry, Transaction transaction, long l) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        return null;
    }

    public Entry readIfExists(Entry entry, Transaction transaction, long l) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        return null;
    }

    public Entry snapshot(Entry entry) throws RemoteException {
        return null;
    }

    public Entry take(Entry entry, Transaction transaction, long l) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        Entry result = null;
        if (!tileWrappers.isEmpty()) {
            result = (Entry)tileWrappers.get(0);
            tileWrappers = tileWrappers.subList(1, tileWrappers.size());
        }
        return result;
    }

    public Entry takeIfExists(Entry entry, Transaction transaction, long l) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        return null;
    }

    public Lease write(Entry entry, Transaction transaction, long l) throws TransactionException, RemoteException {
        writtenEntries.add(entry);
        return null;
    }
    // JavaSpace
}
