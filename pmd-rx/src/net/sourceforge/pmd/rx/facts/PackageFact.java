package net.sourceforge.pmd.rx.facts;

public class PackageFact {
    private ACUFact acu = null;
    private String packageName = null;

    public PackageFact( ACUFact acu, String packageName ) {
	this.acu = acu;
	this.packageName = packageName;
    }

    public ACUFact getACU() {
	return acu;
    }

    public String getPackageName() {
	return packageName;
    }
}
