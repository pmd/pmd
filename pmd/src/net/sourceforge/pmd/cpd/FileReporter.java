/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author  Philippe T'Seyen
 */
public class FileReporter
{
  private File reportFile;

  public FileReporter(File reportFile) {
    if (reportFile == null) throw new NullPointerException("reportFile can not be null");
    this.reportFile = reportFile;
  }

  public void report(String content) throws ReportException {
    try {
      Writer writer = null;
      try {
        writer = new BufferedWriter(new FileWriter(reportFile));
        writer.write(content);
      } finally {
        if (writer != null) writer.close();
      }
    } catch (IOException ioe) {
      throw new ReportException(ioe);
    }
  }
}
