/*
 * Created on 12.07.2004
 */
package net.sourceforge.pmd.dfa;

/**
 * @author raik
 */
public class LinkerException extends Exception {

    public LinkerException() {
        super("An error occured by computing the data flow paths"); //TODO redefinition | accurate?
    }

    public LinkerException(String message) {
        super(message);
    }

}
