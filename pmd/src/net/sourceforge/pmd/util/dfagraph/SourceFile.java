package net.sourceforge.pmd.util.dfagraph;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: Sep 20, 2004
 * Time: 5:43:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SourceFile {
    private String name;
    private List code = new ArrayList();
    public SourceFile(String name) {
        this.name = name;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(name)));
            String line = null;
            while ((line = br.readLine()) != null) {
                code.add(line.trim());
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getLine(int number) {
        return (String)code.get(number-1);
    }
    public String toString() {
        return name;
    }

}
