package net.sourceforge.pmd;

/**
 * Enumeration of the types of source code.
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
 */
public final class SourceType implements Comparable {
    public static final SourceType JAVA_13 = new SourceType("java 1.3");
    public static final SourceType JAVA_14 = new SourceType("java 1.4");
    public static final SourceType JAVA_15 = new SourceType("java 1.5");
    public static final SourceType JSP = new SourceType("jsp");

    private String id;

    /**
     * Private constructor.
     *
     * @param id
     */
    private SourceType(String id) {
        setId(id);
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public boolean equals(Object other) {
        if (other instanceof SourceType) {
            return ((SourceType) other).getId().equals(getId());
        }

        return false;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public int compareTo(Object other) {
        return getId().compareTo(((SourceType) other).getId());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "SourceType [" + getId() + "]";
    }
}
