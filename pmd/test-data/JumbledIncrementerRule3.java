public class JumbledIncrementerRule3 {
 public void foo() { 
  for (int i=0; i<5; ) {
   i++;
  }
  for (int i=0;;) {
   if (i<5) {
    break;
   }
   i++;
  }
  for (;;) {
   int x =5;
  }
  for (int i=0; i<5;i++) ;
  for (int i=0; i<5;i++) 
   foo();
 } 
}