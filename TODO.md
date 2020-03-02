


Overload specificity:

At:   /home/clifrr/Bureau/jdk13-src/java.base/jdk/nashorn/internal/runtime/Context.java:1792 :24
Expr: Stream.of(addModules.split(\",\")).map(String::trim)
[WARNING] CTDecl resolution failed. Summary of failures:
    STRICT:
        Incompatible formals: java.lang.String[] is not convertible to java.lang.String		map(java.util.function.Function<? super java.lang.String[], ? extends R>) -> java.util.stream.Stream<R>
    
    LOOSE:
        Incompatible formals: java.lang.String[] is not convertible to java.lang.String		map(java.util.function.Function<? super java.lang.String[], ? extends R>) -> java.util.stream.Stream<R>
  


Raw type mistake:

At:   /home/clifrr/Bureau/jdk13-src/java.base/java/util/Collections.java:710 :23
Expr: max((Collection) coll)
[WARNING] CTDecl resolution failed. Summary of failures:
    STRICT:
        Incompatible bounds: π291 = java.lang.Object and π291 <: java.lang.Comparable<? super π291>		max(java.util.Collection<? extends T>) -> T
    
    LOOSE:
        Incompatible bounds: ρ291 = java.lang.Object and ρ291 <: java.lang.Comparable<? super ρ291>		max(java.util.Collection<? extends T>) -> T
    

 public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
        if (comp==null)
            return (T)max((Collection) coll);


