 public class UnnecessaryTemporary {
     void method (int x) {
        new Integer(x).toString(); 
        new Long(x).toString(); 
        new Float(x).toString(); 
        new Byte((byte)x).toString(); 
        new Double(x).toString(); 
        new Short((short)x).toString(); 
     }
 }