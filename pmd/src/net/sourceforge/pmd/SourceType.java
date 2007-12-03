package net.sourceforge.pmd;
 
 /**
  * Enumeration of the types of source code.
  *
  * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
  */
 public final class SourceType implements Comparable<SourceType> {
     public static final SourceType JAVA_13 = new SourceType("java 1.3");
     public static final SourceType JAVA_14 = new SourceType("java 1.4");
     public static final SourceType JAVA_15 = new SourceType("java 1.5");
     public static final SourceType JAVA_16 = new SourceType("java 1.6");
     public static final SourceType JAVA_17 = new SourceType("java 1.7");
     public static final SourceType JSP = new SourceType("jsp");
     
     private static SourceType[] sourceTypes = new SourceType[]{JAVA_13, JAVA_14, JAVA_15, JAVA_16, JAVA_17, JSP};
 
     private String id;
 
     /**
      * Private constructor.
      */
     private SourceType(String id) {
         this.id = id;
     }
 
     public String getId() {
         return id;
     }
     
     /**
      * Get the SourceType for a certain Id. Case insensitive.
      * 
      * @return null if not found
      */
     public static SourceType getSourceTypeForId(String id) {
         for (SourceType sourceType : sourceTypes) {
             if (sourceType.getId().equalsIgnoreCase(id)) {
                 return sourceType;
             }
         }
         return null;
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
 
     public int compareTo(SourceType other) {
         return getId().compareTo(other.getId());
     }
 
     public String toString() {
         return "SourceType [" + getId() + "]";
     }
 } 
