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
