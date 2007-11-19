/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @author Romain PELISSE belaran@gmail.com
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class FortranLanguage implements Language
{
    public static class FortranFileOrDirectoryFilter implements FilenameFilter
    {
        public boolean accept(File dir, String filename)
        {
        	// FIXME: This sucks ! extension should be regroup in somekind of constant parameter..
        	//		and it should be case insensitive.
            return filename.endsWith(".for") || filename.endsWith(".FOR") ||
                    (new File(dir.getAbsolutePath() + fileSeparator + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new FortranTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new FortranFileOrDirectoryFilter();
    }
}
