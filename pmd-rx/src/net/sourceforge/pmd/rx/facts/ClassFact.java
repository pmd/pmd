package net.sourceforge.pmd.rx.facts;

public class ClassFact
{
    private ACUFact acu = null;
    private ClassFact outerClass = null;
    private String className = null;

    private boolean bPublic = false;
    private boolean bAbstract = false;
    private boolean bStrict = false;
    private boolean bFinal = false;
    private boolean bStatic = false;
    private boolean bProtected = false;
    private boolean bPrivate = false;

    public ClassFact( ACUFact acu,
		      ClassFact outerClass,
		      String className ) {
	this.acu = acu;
	this.outerClass = outerClass;
	this.className = className;
    }

    public ACUFact getACU() {
	return acu;
    }

    public ClassFact getOuterClass() {
	return outerClass;
    }

    public String getClassName() {
	return className;
    }

    public void setPublic(boolean bPublic) {
	this.bPublic = bPublic;
    }

    public boolean isPublic() {
	return bPublic;
    }

    public void setAbstract(boolean bAbstract) {
	this.bAbstract = bAbstract;
    }

    public boolean isAbstract() {
	return bAbstract;
    }

    public void setFinal(boolean bFinal) {
	this.bFinal = bFinal;
    }

    public boolean isFinal() {
	return bFinal;
    }

    public void setStrict(boolean bStrict) {
	this.bStrict = bStrict;
    }

    public boolean isStrict() {
	return bStrict;
    }

    public void setStatic(boolean bStatic) {
	this.bStatic = bStatic;
    }

    public boolean isStatic() {
	return bStatic;
    }

    public void setProtected(boolean bProtected) {
	this.bProtected = bProtected;
    }

    public boolean isProtected() {
	return bProtected;
    }

    public void setPrivate(boolean bPrivate) {
	this.bPrivate = bPrivate;
    }

    public boolean isPrivate() {
	return bPrivate;
    }
}
