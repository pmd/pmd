package net.sourceforge.pmd.rx.facts;

public class ImportFact {
    private ACUFact acu = null;
    private String importPackage = null;
    private boolean onDemand = true;

    public ImportFact( ACUFact acu, String importPackage,
		       boolean onDemand ) {
	this.acu = acu;
	this.importPackage = importPackage;
	this.onDemand = onDemand;
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
}
