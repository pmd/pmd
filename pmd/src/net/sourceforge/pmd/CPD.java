/*
 * User: tom
 * Date: Jul 26, 2002
 * Time: 8:43:19 PM
 */
package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

// this class will go away as soon
// as this spike is over
// i didn't want to create a new directory though
// since i'm not sure if this will work out

public class CPD {

    public static class TokenPtr {

        private int location;
        private File file;
        private StringBuffer fileContents;

        public TokenPtr(File file, StringBuffer fileContents, int location) {
            this.file = file;
            this.location = location;
            this.fileContents = fileContents;
        }

        public boolean nextTokenAvailable(int imageLength) {
            return (location+imageLength+1) < fileContents.length();
        }

        public String getNextCharacter(int imageLength) {
            return String.valueOf(fileContents.charAt(location + imageLength));
        }

    }

    public static class TokenTable {

        // String->List (TokenPtr, TokenPtr, TokenPtr)
        private Map tokens = new HashMap();
        private int minimumTokenSize;
        private Set tokensFound = new HashSet();

        public TokenTable(int minimumTokenSize) {
            this.minimumTokenSize = minimumTokenSize;
        }

        public void add(File file, StringBuffer code) {
            for (int i=0; i<code.length(); i++) {
                String image = String.valueOf(code.charAt(i));
                TokenPtr thisToken = new TokenPtr(file, code, i);
                if (tokens.containsKey(image)) {
                    List tokenPtrs = (List)tokens.get(image);
                    tokenPtrs.add(thisToken);
                } else {
                    List tokenPtrs = new ArrayList();
                    tokenPtrs.add(thisToken);
                    tokens.put(image, tokenPtrs);
                }
            }
        }

        public void frobnicate() {
            while (true) {
                deleteSoloTokens();
                combineTokens();
            }
        }

        private void combineTokens() {
            Map newTokenPtrs = new HashMap();
            for (Iterator i=tokens.keySet().iterator(); i.hasNext();) {
                String image = (String)i.next();
                List tokenPtrs = (List)tokens.get(image);
                for (Iterator j = tokenPtrs.iterator(); j.hasNext();) {
                    TokenPtr tokenPtr = (TokenPtr)j.next();
                    if (tokenPtr.nextTokenAvailable(image.length())) {
                        String newImage = image + tokenPtr.getNextCharacter(image.length());
                        if (newTokenPtrs.containsKey(newImage)) {
                            List list = (List)newTokenPtrs.get(newImage);
                            list.add(tokenPtr);
                        } else {
                            List list = new ArrayList();
                            list.add(tokenPtr);
                            newTokenPtrs.put(newImage, list);
                        }
                        if (newImage.length() > minimumTokenSize) {
                            List list = (List)newTokenPtrs.get(newImage);
                            //if (list.size() > 1) {

                                boolean already = false;
                                for (Iterator p = this.tokensFound.iterator(); p.hasNext();) {
                                    String alreadyIn = (String)p.next();
                                    if (newImage.indexOf(alreadyIn) != -1) {
                                        already = true;
                                    }
                                }
                                if (!already) {
                                    tokensFound.add(newImage);
                                    System.out.println("newImage = " + newImage);
                                }
                            //}
                        }
                    }
                }
            }
            tokens = newTokenPtrs;
        }

        private void deleteSoloTokens() {
            for (Iterator i=tokens.keySet().iterator(); i.hasNext();) {
                String image = (String)i.next();
                List tokenPtrs = (List)tokens.get(image);
                if (tokenPtrs.size() == 1) {
                    i.remove();
                }
            }
        }

        public int getTokenCount() {
            return tokens.size();
        }

        public Set getTokensFound() {
            return this.tokensFound;
        }
    }

    private Map files = new HashMap();
    private TokenTable tokenTable;

    public CPD(int minimumFragmentSize) {
        tokenTable = new TokenTable(minimumFragmentSize);
    }

    public void addFile(File file) {
        files.put(file, null);
    }

    public void loadFiles() throws IOException {
        for (Iterator i = files.keySet().iterator(); i.hasNext();) {
            File file = (File)i.next();
            files.put(file, load(file));
        }
    }

    public void buildTokenTable() {
        for (Iterator i = files.keySet().iterator(); i.hasNext();) {
            File file = (File)i.next();
            StringBuffer contents = (StringBuffer)files.get(file);
            System.out.println(contents);
            tokenTable.add(file, contents);
        }
    }

    public void frobnicate() {
        tokenTable.frobnicate();
        for (Iterator i = tokenTable.getTokensFound().iterator(); i.hasNext();) {
            System.out.println("found dupe = " + i.next());

        }
    }

    private StringBuffer load(File file) throws IOException {
        FileReader fr = new FileReader(file);
        StringBuffer sb = new StringBuffer();
        int c = 0;
        while ((c = fr.read()) != -1) {
            sb.append((char)c);
        }
        fr.close();
        return sb;
    }

    public static void main(String[] args) {
        CPD cpd = new CPD(15);
        cpd.addFile(new File("C:\\data\\pmd\\pmd\\test-data\\Unused1.java"));
        cpd.addFile(new File("C:\\data\\pmd\\pmd\\test-data\\Unused2.java"));
        try {
            cpd.loadFiles();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        cpd.buildTokenTable();
        cpd.frobnicate();
    }
}
