public class UnusedLocal9 {
 public void foo() {
  final String x = "baf";
   new Runnable() {
    public void run() {
     System.out.println(x);
    }
   };	
 }
}
