/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

/**
 * @author  Philippe T'Seyen
 */
public class ReportException extends Exception {

    private Throwable cause;

  public ReportException(Throwable cause) {
    super();
      this.cause = cause;
  }
}
