public class PositionalIterator1 {
 public void foo(Iterator i) {
  while(i.hasNext()) {
   Object one = i.next();
   
   // 2 calls to next() inside the loop == bad!
   Object two = i.next(); 
  }
 }
}
