// See https://github.com/pmd/pmd/issues/1568

class MyApexClass {
    void bar(){
         if(!alist.isEmpty() && alist != null) {
            foo();
         }
    }
}
