public class ForLoopsNeedBraces2 {
 public void foo() {	
  for (int i=0; i<42;i++) {
	foo();
  }
 }
}