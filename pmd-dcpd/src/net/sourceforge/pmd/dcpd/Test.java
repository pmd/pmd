package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.sourceforge.pmd.cpd.*;

import java.rmi.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Test {


    public static class Message implements Entry {
        public String content;
        public Message() {}
        public Message(String in) {
            content = in;
        }
        public String toString() {
            return "Message:"+content;
        }
    }


    public Test() {
        try {
            JavaSpace space = Util.findSpace(Util.SPACE_SERVER);

            long start = System.currentTimeMillis();
            System.out.println("WRITING");
            space.write(new Message("howdy"), null, Lease.FOREVER);
            long stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");

            start = System.currentTimeMillis();
            System.out.println("TAKING");
            Message result = (Message)space.take(new Message(), null, Long.MAX_VALUE);
            System.out.println("result = " + result.toString());
            stop = System.currentTimeMillis();
            System.out.println("that took " + (stop - start) + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Test();
    }
}
