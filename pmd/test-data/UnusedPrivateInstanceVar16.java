public class UnusedPrivateInstanceVar16 {

 // this field is NOT used. 
 private int value = 0; 

 // but the param with the same name is. 
 public int doSomething(int value) { 
  return value + 1; 
 }
}