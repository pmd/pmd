---
title:  Index of code metrics
tags: [customizing]
summary: "Index of the code metrics available out of the box to Java rule developers."
last_updated: July 20, 2017
sidebar: pmd_sidebar
permalink: pmd_devdocs_metrics_index.html
folder: pmd/devdocs
toc:
  minimumHeaders: 8
---
# Index of code metrics

## Access to Foreign Data (ATFD)

*Operation metric, class metric.*

### Description


## Cyclomatic Complexity (CYCLO)

*Operation metric.* Can be calculated on any non-abstract method.

### Description

McCabe's Cyclomatic Complexity is the number of independent paths through a block of code \[[Lanza05](#Lanza05)\]. 
Formally, given that the control flow graph of the block has n vertices, e edges and p connected components, the 
Cyclomatic complexity of the block is given by `CYCLO = e - n + 2p` \[[McCabe76](#McCabe76)\]. In practice it can be 
calculated by counting control flow statements following the standard rules given below.

The standard version of the metric complies with McCabe's original definition:

* Methods have a base complexity of 1.
* +1 for every control flow statement (`if`, `case`, `catch`, `throw`, `do`, `while`, `for`, `break`, `continue`) and 
conditional expression (`?:`) \[[Sonarqube](#Sonarqube)\]. Notice switch cases count as one, but not the switch itself: the point is that
 a switch should have the same complexity value as the equivalent series of `if` statements.
* `else`, `finally` and `default` don't count;
* +1 for every boolean operator (`&&`, `||`) in the guard condition of a control flow statement. That's because
Java has short-circuit evaluation semantics for boolean operators, which makes every boolean operator kind of a
control flow statement in itself.
 
### Versions

* Version `CycloVersion#IGNORE_BOOLEAN_PATHS`: Boolean operators are not counted, nor are empty
  fall-through cases in `switch` statements.
 
 
## Lines of Code (LoC)

*Operation metric, class metric.* Can be calculated on any of those nodes.

### Description

Simply counts the number of lines of code the operation or class takes up in the source. This metric doesn't discount
 comments or blank lines.


## Non-commenting source statements (NCSS)

*Operation metric, class metric.* Can be calculated on any of those nodes.

### Description

Counts statements of a class or operation. That's roughly equivalent to counting the number of semicolons and 
opening braces in the program. Comments and blank lines are ignored, and so are split statements (e.g. `int\n a;` counts
 a single statement). 

The standard version of the metric is based off JavaNCSS's version  \[[JavaNcss](#JavaNcss)\]:

* +1 for any of the following statements: `if`, `else`, `while`, `do`, `for`, `switch`, `break`, `continue`, `return`, 
`throw`, `synchronized`, `catch`, `finally`.
* +1 for each assignment, variable declaration (except `for` loop initializers) or statement expression. We count 
variables  declared on the same line (e.g. `int a, b, c;`) as a single statement. 
* Contrary to Sonarqube, but as JavaNCSS, we count type declarations (class, interface, enum, annotation),  
and method and field declarations.
* Contrary to JavaNCSS, but as Sonarqube, we do not count package declaration and import declarations as statements.


### Versions

* Version `NcssVersion#JAVANCSS`: Import and package statements are counted as well. This version fully complies with
 JavaNCSS.

# References

<a name="Lanza05">Lanza05:</a> Lanza, Marinescu; Object-Oriented Metrics in Practice, 2005.

<a name="McCabe76">McCabe76:</a> McCabe, A Complexity Measure, in Proceedings of the 2nd ICSE (1976).

<a name="Sonarqube">Sonarqube:</a> [Sonarqube online documentation.](https://docs.sonarqube.org/display/SONAR/Metrics+-+Complexity)

<a name="JavaNcss">JavaNcss:</a> [JavaNCSS online documentation.](http://www.kclee.de/clemens/java/javancss/)