public class PositionalIterator3 {
 public void foo() {
  Iterator i = (new List()).iterator();
  while(i.hasNext()) {
   Object one = i.next();
   Iterator j = (new List()).iterator();
   while (j.hasNext()) {
    j.next();
   }
  }
 }
}
