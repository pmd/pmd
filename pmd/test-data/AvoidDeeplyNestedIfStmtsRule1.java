public class AvoidDeeplyNestedIfStmtsRule1 {
 public void bar() { 
  int x=2; 
  int y=3; 
  int z=4; 
  if (x>y) { 
   if (y>z) { 
    if (z==x) { 
     // this is officially out of control now 
    } 
   } 
  }
 }
}
