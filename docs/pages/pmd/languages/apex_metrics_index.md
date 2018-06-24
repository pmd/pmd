---
title:  Index of Apex code metrics
tags: [extending, metrics]
summary: "Index of the code metrics available out of the box to Apex rule developers."
last_updated: July 20, 2017
permalink: pmd_apex_metrics_index.html
toc:
  minimumHeaders: 8
---
# Index of code metrics

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
* +1 for every control flow statement (`if`, `catch`, `throw`, `do`,
  `while`, `for`, `break`, `continue`) and conditional expression (`?:`).
* `else`, `finally` and `default` don't count;
* +1 for every boolean operator (`&&`, `||`) in the guard condition of a control 
  flow statement.

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

## Weighted Method Count (WMC)

*Class metric.* Can be computed on classes and enums.

# References

<a name="Lanza05">Lanza05:</a> Lanza, Marinescu; Object-Oriented Metrics in Practice, 2005.

<a name="McCabe76">McCabe76:</a> McCabe, A Complexity Measure, in Proceedings of the 2nd ICSE (1976).
