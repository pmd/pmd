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

public class DCPDManager {

    private JavaSpace space;
    private Job job;
    private TokenSetsWrapper tokenSetWrapper;

    public DCPDManager(String javaSpaceURL, String codeDirectory, int minimumTileSize) {
        try {
            long start = System.currentTimeMillis();
            System.out.println("Connecting to JavaSpace");
            space = Util.getInstance().findSpace(javaSpaceURL);

            System.out.println("Tokenizing");
            job = new Job("java_lang", new Integer((int)System.currentTimeMillis()));
            tokenSetWrapper = new TokenSetsWrapper(loadTokens(codeDirectory, true), job);
            System.out.println("Tokenizing complete, " + (System.currentTimeMillis()-start) + " elapsed ms");

            System.out.println("Writing the TokenSetsWrapper to the space");
            space.write(tokenSetWrapper, null, Lease.FOREVER);
            System.out.println("Writing complete, " + (System.currentTimeMillis()-start) + " elapsed ms");

            System.out.println("Crunching");
            DGST dgst = new DGST(space, job, tokenSetWrapper.tokenSets, minimumTileSize);
            Results results = dgst.crunch(new CPDListenerImpl());
            System.out.println("Crunching complete, " + (System.currentTimeMillis()-start) + " elapsed ms");

            System.out.println("Cleaning up");
            space.take(tokenSetWrapper, null, 200);
            space.take(job, null, 200);

            System.out.println(render(results));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't connect to the space on " + javaSpaceURL);
        }
    }

    public String getImage(Tile tile, Results results) {
        try {
            Iterator i = results.getOccurrences(tile);
            TokenEntry firstToken = (TokenEntry)i.next();
            TokenList tl = tokenSetWrapper.tokenSets.getTokenList(firstToken);
            int endLine = firstToken.getBeginLine()+ results.getTileLineCount(tile, tokenSetWrapper.tokenSets);
            return tl.getSlice(firstToken.getBeginLine()-1, endLine-1);
        } catch (Exception ex) {ex.printStackTrace(); }
        return "";
    }

    protected String EOL = System.getProperty("line.separator", "\n");

    private String render(Results results) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = results.getTiles(); i.hasNext();) {
            Tile tile = (Tile)i.next();
            sb.append("=============================================================");
            sb.append(EOL);
            sb.append("A " + results.getTileLineCount(tile, tokenSetWrapper.tokenSets) + " line (" + tile.getTokenCount() + " tokens) duplication:");
            sb.append(EOL);
            for (Iterator j = results.getOccurrences(tile); j.hasNext();) {
                TokenEntry tok = (TokenEntry)j.next();
                sb.append("Starting at line " + tok.getBeginLine() + " in " + tok.getTokenSrcID());
                sb.append(EOL);
            }
            sb.append(getImage(tile, results));
            sb.append(EOL);
        }
        return sb.toString();
    }

    private TokenSets loadTokens(String dir, boolean recurse) throws IOException {
        TokenSets tokenSets = new TokenSets();
        FileFinder finder = new FileFinder();
        List files = finder.findFilesFrom(dir, new JavaFileOrDirectoryFilter(), recurse);
        for (Iterator i = files.iterator(); i.hasNext();) {
            tokenSets.add(tokenizeFile((File)i.next()));
        }
        return tokenSets;
    }

    private TokenList tokenizeFile(File file) throws IOException {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList ts = new TokenList(file.getAbsolutePath());
        FileReader fr = new FileReader(file);
        t.tokenize(ts, fr);
        fr.close();
        return ts;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println();
            System.out.println("Usage: java net.sourceforge.pmd.dcpd.DCPDManager <path> <size>");
            System.out.println("<path> : your source code directory");
            System.out.println("<size> : the minimum tile size.  70 is a good place to start, then try lower numbers to find smaller duplicate chunks");
            System.out.println();
            System.out.println("Example: java net.sourceforge.pmd.dcpd.DCPDManager /home/tom/myproject/src 75");
            System.out.println("Example (using the go.bat script): go /home/tom/myproject/src 75");
            return;
        }
        new DCPDManager(Util.getInstance().getSpaceServer(), args[0], Integer.parseInt(args[1]));
    }
}
