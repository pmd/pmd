import java.util.*;
public class LooseCoupling1 {
 private Set fooSet = new HashSet(); // OK

 public Set getFoo() {
  return fooSet;
 }
}
