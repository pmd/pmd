package net.sourceforge.pmd.rx.facts;

public class ImportFact {
    private ACUFact acu = null;
    private String importPackage = null;
    private boolean onDemand = true;
    private int lineNumber = -1;

    public ImportFact( ACUFact acu, String importPackage,
		       boolean onDemand, int lineNumber ) {
	this.acu = acu;
	this.importPackage = importPackage;
	this.onDemand = onDemand;
	this.lineNumber = lineNumber;
    }

    public ACUFact getACU() {
	return acu;
    }

    public String getImportPackage() {
	return importPackage;
    }

    public boolean isOnDemand() {
	return onDemand;
    }

    public int getLineNumber() {
	return lineNumber;
    }

    public String toString() {
	if (onDemand) {
	    return Integer.toString(lineNumber) + ": " + importPackage + ".*";
	} else {
	    return Integer.toString(lineNumber) + ": " + importPackage;
	}
    }
}
