/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 4:56:10 PM
 */
package net.sourceforge.pmd.dcpd;

import net.jini.space.JavaSpace;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.sourceforge.pmd.cpd.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Iterator;

public class DCPD {

    private JavaSpace space;
    private Job job;
    private TokenSetsWrapper tsw;

    public DCPD(String javaSpaceURL) {
        try {
            System.out.println("Connecting to JavaSpace");
            space = Util.findSpace(javaSpaceURL);
            job = new Job("java_lang", new Integer(1));
            System.out.println("Tokenizing");
            tsw = new TokenSetsWrapper(loadTokens("C:\\j2sdk1.4.0_01\\src\\java\\lang\\", true), job.id);
            System.out.println("Writing the Job to the space");
            space.write(job, null, Lease.FOREVER);
            System.out.println("Writing the TokenSetsWrapper to the space");
            space.write(tsw, null, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't connect to the space on " + javaSpaceURL);
        }
    }

    private TokenSets loadTokens(String dir, boolean recurse) throws IOException {
        TokenSets tokenSets = new TokenSets();
        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        for (Iterator i = files.iterator(); i.hasNext();) {
            tokenSets.add(add(files.size(), (File)i.next()));
        }
        return tokenSets;
    }

    private TokenList add(int fileCount, File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        return ts;
    }

    public static void main(String[] args) {
        new DCPD("mordor");
    }
}
