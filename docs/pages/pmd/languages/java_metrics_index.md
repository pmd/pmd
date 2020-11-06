---
title:  Index of Java code metrics
tags: [extending, metrics]
summary: "Index of the code metrics available out of the box to Java rule developers."
last_updated: July 20, 2017
permalink: pmd_java_metrics_index.html
toc:
  minimumHeaders: 8
---
# Index of code metrics

## Access to Foreign Data (ATFD)

*Operation metric, class metric.* Can be computed on classes, enums and 
concrete operations.

### Description

Number of usages of foreign attributes, both directly and through accessors. 
High values of ATFD (> 3 for an operation) may suggest that the class or operation
breaks encapsulation by relying on the internal representation of the classes
it uses instead of the services they provide.
 
ATFD can be used to detect God Classes and Feature Envy. \[[Lanza05](#Lanza05)\]

## Class Fan Out Complexity (CLASS_FAN_OUT)

*Operation metric, class metric.* Can be computed on classes, enums and 
concrete operations.

### Description
This counts the number of other classes a given class or operation relies on.
Classes from the package `java.lang` are ignored by default (can be changed via options).
Also primitives are not included into the count.

### Code example

```java
import java.util.*;
import java.io.IOException;

public class Foo { // total 8
    public Set set = new HashSet(); // +2
    public Map map = new HashMap(); // +2
    public String string = ""; // from java.lang -> does not count by default
    public Double number = 0.0; // from java.lang -> does not count by default
    public int[] intArray = new int[3]; // primitive -> does not count

    @Deprecated // from java.lang -> does not count by default
    @Override // from java.lang -> does not count by default
    public void foo(List list) throws Exception { // +1 (Exception is from java.lang)
        throw new IOException(); // +1
    }

    public int getMapSize() {
        return map.size(); // +1 because it uses the Class from the 'map' field
    }   
}
```

### Options

* Option `includeJavaLang`: Also include classes from the package `java.lang`

## Cyclomatic Complexity (CYCLO) 

*Operation metric.* Can be calculated on any non-abstract operation.

### Description

Number of independent paths through a block of code \[[Lanza05](#Lanza05)\]. 
Formally, given that the control flow graph of the block has `n` vertices, `e` 
edges and `p` connected components, the cyclomatic complexity of the block is 
given by `CYCLO = e - n + 2p` \[[McCabe76](#McCabe76)\]. In practice it can be 
calculated by counting control flow statements following the standard rules given 
below.

The standard version of the metric complies with McCabe's original definition:

* Methods have a base complexity of 1.
* +1 for every control flow statement (`if`, `case`, `catch`, `throw`, `do`, 
  `while`, `for`, `break`, `continue`) and conditional expression (`?:`) 
  \[[Sonarqube](#Sonarqube)\]. Notice switch cases count as one, but not the 
  switch itself: the point is that a switch should have the same complexity 
  value as the equivalent series of `if` statements. 
* `else`, `finally` and `default` don't count;
* +1 for every boolean operator (`&&`, `||`) in the guard condition of a control 
  flow statement. That's because Java has short-circuit evaluation semantics for 
  boolean operators, which makes every boolean operator kind of a control flow 
  statement in itself.

### Code examples

```java
class Foo {
  void baseCyclo() {                // Cyclo = 1
    highCyclo();
  }
  
  void highCyclo() {                // Cyclo = 10
    int x = 0, y = 2;
    boolean a = false, b = true;
    
    if (a && (y == 1 ? b : true)) { // +3
      if (y == x) {                 // +1
        while (true) {              // +1
          if (x++ < 20) {           // +1
            break;                  // +1
          }
        }
      } else if (y == t && !d) {    // +2
        x = a ? y : x;              // +1
      } else {
        x = 2;
      }
    }  
  }     
}
```
### Options

* Option `CycloVersion#IGNORE_BOOLEAN_PATHS`: Boolean operators are not counted,
  nor are empty fall-through cases in `switch` statements. You can use this 
  option to get results similar to those of the old `StdCyclomaticComplexityRule`, 
  which is to be replaced.
* Option `CycloVersion#CONSIDER_ASSERTS`: Assert statements are counted as if 
  they were `if (..) throw new AssertionError(..)`. Compatible with 
  `IGNORE_BOOLEAN_PATHS`.
 
## Lines of Code (LoC)

*Operation metric, class metric.* Can be calculated on any of those nodes.

### Description

Simply counts the number of lines of code the operation or class takes up in 
the source. This metric doesn't discount comments or blank lines. See also 
[NCSS](#non-commenting-source-statements-ncss).


## Non-commenting source statements (NCSS)

*Operation metric, class metric.* Can be calculated on any of those nodes.

### Description

Number of statements in a class or operation. That's roughly equivalent to 
counting the number of semicolons and opening braces in the program. Comments 
and blank lines are ignored, and statements spread on multiple lines count as 
only one (e.g. `int\n a;` counts a single statement). 

The standard version of the metric is based off JavaNCSS's version  
\[[JavaNcss](#JavaNcss)\]:

* +1 for any of the following statements: `if`, `else`, `while`, `do`, `for`, 
  `switch`, `break`, `continue`, `return`, `throw`, `synchronized`, `catch`, 
  `finally`.
* +1 for each assignment, variable declaration (except `for` loop initializers) 
  or statement expression. We count variables  declared on the same line (e.g. 
  `int a, b, c;`) as a single statement. 
* Contrary to Sonarqube, but as JavaNCSS, we count type declarations (class, 
  interface, enum, annotation), and method and field declarations 
  \[[Sonarqube](#Sonarqube)\].
* Contrary to JavaNCSS, but as Sonarqube, we do not count package declaration 
  and import declarations as statements. This makes it easier to compare nested 
  classes to outer classes. Besides, it makes for class metric results that 
  actually represent the size of the class and not of the file. If you don't 
  like that behaviour, use the `COUNT_IMPORTS` option.

### Code example
```java
import java.util.Collections;       // +0
import java.io.IOException;         // +0

class Foo {                         // +1, total Ncss = 12
  
  public void bigMethod()           // +1
      throws IOException {     
    int x = 0, y = 2;               // +1
    boolean a = false, b = true;    // +1
    
    if (a || b) {                   // +1
      try {                         // +1
        do {                        // +1
          x += 2;                   // +1
        } while (x < 12);
          
        System.exit(0);             // +1
      } catch (IOException ioe) {   // +1
        throw new PatheticFailException(ioe); // +1
      }
    } else {
      assert false;                 // +1
    }
  }     
}
```


### Options

* Option `NcssVersion#COUNT_IMPORTS`: Import and package statements are counted 
  as well. This version fully complies with JavaNCSS.

## NPath complexity (NPath)

*Operation metric.* Can be computed on any non-abstract operation.

### Description

Number of acyclic execution paths through a piece of code. This is related to 
cyclomatic complexity, but the two metrics don't count the same thing: NPath 
counts the number of distinct *full* paths from the beginning to the end of the 
method, while Cyclo only counts the number of decision points. NPath is not 
computed as simply as Cyclo. With NPath, two decision points appearing sequentially 
have their complexity multiplied. 

The fact that NPath multiplies the complexity of statements makes it grow 
exponentially: 10 `if` - `else` statements in a row would give an NPath of 1024, 
while Cyclo would evaluate to 20. Methods with an NPath complexity over 200 are 
generally considered too complex.

We compute NPath recursively, with the following set of rules:
* An empty block has a complexity of 1.
* The complexity of a block is the product of the NPath complexity of its 
  statements, calculated as follows:
  * The complexity of `for`, `do` and `while` statements is 1, plus the 
    complexity of the block, plus the complexity of the guard condition.
  * The complexity of a cascading `if` statement (`if .. else if ..`) is the 
    number of `if` statements in the chain, plus the complexity of their guard 
    condition, plus the complexity of the unguarded `else` block (or 1 if there 
    is none).
  * The complexity of a `switch` statement is the number of cases, plus the 
    complexity of each `case` block. It's equivalent to the complexity of the 
    equivalent cascade of `if` statements.
  * The complexity of a ternary expression (`?:`) is the complexity of the guard
    condition, plus the complexity of both expressions. It's equivalent to the 
    complexity of the equivalent `if .. else` construct.
  * The complexity of a `try .. catch` statement is the complexity of the `try` 
    block, plus the complexity of each catch block.
  * The complexity of a `return` statement is the complexity of the expression 
    (or 1 if there is none).
  * All other statements have a complexity of 1 and are discarded from the product.
   
### Code example

```java
void fun(boolean a, boolean b, boolean c) { // NPath = 6
    
  // block #0
  
  if (a) {
    // block #1
  } else {
    // block #2
  }
  
  // block #3
  
  if (b) {
    // block #4
  } else if (c) {
    // block #5  
  }
  
  // block #6
}
```
After block 0, the control flow can either execute block 1 or 2 before jumping 
to block 3. From block three, the control flow will again have the choice 
between blocks 4 and 5 before jumping to block 6. The first `if` offers 2 
choices, the second offers 3, so the cyclomatic complexity of this method is 
2 + 3 = 5. NPath, however, sees 2 * 3 = 6 full paths from the beginning to the end.
 

## Number Of Public Attributes (NOPA)
*Class metric.* Can be computed on classes.

## Number Of Accessor Methods (NOAM)
*Class metric.* Can be computed on classes.

## Tight Class Cohesion (TCC)

*Class metric.* Can be computed on classes and enums.

### Description

The relative number of method pairs of a class that access in common at
least one attribute of the measured class. TCC only counts
direct attribute accesses, that is, only those attributes that are accessed in
the body of the method \[[BK95](#BK95)\].

TCC is taken to be a reliable cohesion metric for a class. High values (>70%)
indicate a class with one basic function, which is hard to break into subcomponents.
On the other hand, low values (<50%) may indicate that the class tries to do too much and
defines several unrelated services, which is undesirable.

TCC can be used to detect God Classes and Brain Classes \[[Lanza05](#Lanza05)\].
 
## Weighted Method Count (WMC)

*Class metric.* Can be computed on classes and enums.

### Description

Sum of the statistical complexity of the operations in the class. We use 
[CYCLO](#cyclomatic-complexity-cyclo) to quantify the complexity of an operation 
\[[Lanza05](#Lanza05)\].

### Options

WMC uses the same options as CYCLO, which are provided to CYCLO when 
computing it.

## Weight Of Class (WOC)

*Class metric.* Can be computed on classes.

### Description

Number of "functional" public methods divided by the total number of
public methods. Our definition of "functional method" excludes
constructors, getters, and setters.

This metric tries to quantify whether the measured class' interface reveals
more data than behaviour. Low values (less than 30%) indicate that the class
reveals much more data than behaviour, which is a sign of poor encapsulation.

This metric is used to detect Data Classes, in conjunction with [WMC](#weighted-method-count-wmc),
[NOPA](#number-of-public-attributes-nopa) and [NOAM](#number-of-accessor-methods-noam).



# References


<a name="BK95">BK95:</a> Bieman, Kang; Cohesion and reuse in an object-oriented system. 
In Proceedings ACM Symposium on Software Reusability, 1995.

<a name="Lanza05">Lanza05:</a> Lanza, Marinescu; Object-Oriented Metrics in Practice, 2005.

<a name="McCabe76">McCabe76:</a> McCabe, A Complexity Measure, in Proceedings of the 2nd ICSE (1976).

<a name="Sonarqube">Sonarqube:</a> [Sonarqube online documentation.](https://docs.sonarqube.org/display/SONAR/Metric+Definitions)

<a name="JavaNcss">JavaNcss:</a> [JavaNCSS online documentation.](http://www.kclee.de/clemens/java/javancss/)
