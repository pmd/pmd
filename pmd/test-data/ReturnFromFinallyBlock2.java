public class ReturnFromFinallyBlock2 {
 public String getBar() {
  try {
   return "buz";
  } catch (Exception e) {
   return "biz";
  } finally {
   return "fiddle!"; // bad!
  }
 }
} 
