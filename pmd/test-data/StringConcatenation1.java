public class StringConcatenation1 {
 public String foo(Object[] someArray) {
  String list = "" ; 
  for( int i = 0; i < someArray.length; i++ ){ 
   // also try:
   // list += "," someArray[i];
   list = list + "," + someArray[i]; 
  } 
  return list;
 }
}
