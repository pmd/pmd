public class AccessorClassGeneration2 {
 public class InnerClass {
   public InnerClass(){
   }
 }
 void method(){
   new InnerClass(); //OK, due to public constructor
 }
}
