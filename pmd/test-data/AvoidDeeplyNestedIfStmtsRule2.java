public class AvoidDeeplyNestedIfStmtsRule2 {
 public void bar() { 
  if (true) {
  } else if (true) {
  } else if (true) {
  } else {
    // this ain't good code, but it shouldn't trigger this rule
  }
 }
}
