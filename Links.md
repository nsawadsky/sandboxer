# Online Articles #

## Memory Leaks ##
  * Troubleshooting memory leaks: http://www.oracle.com/technetwork/java/javase/memleaks-137499.html
  * Solving common Java EE Performance problems: http://www.javaworld.com/javaworld/jw-06-2006/jw-0619-tuning.html
  * How to fix memory leaks in Java: http://olex.openlogic.com/wazi/2009/how-to-fix-memory-leaks-in-java/
  * References:
    * Weak: http://www.ibm.com/developerworks/java/library/j-jtp11225/index.html
    * Soft: http://www.ibm.com/developerworks/java/library/j-jtp01246.html

### Common Leaks ###
  * Collection classes
  * Static classes
  * "Event handlers"

## BCI (Byte Code Instrumentation) ##
  * Manual (non ASM): http://weblogs.java.net/blog/kellyohair/archive/2005/05/bytecode_instru.html

## JVMTI ##
  * Latest JVMTI reference: http://download.oracle.com/javase/6/docs/platform/jvmti/jvmti.html
  * Source code examples: http://localdoc.scusa.lsu.edu/java/5-jdk/demo/jvmti/
  * Using Agents (code examples): http://blogs.sun.com/kto/entry/using_vm_agents

# Papers #

## Interesting ##
  * Object ownership profiling: a technique for finding and fixing memory leaks: http://portal.acm.org/citation.cfm?id=1321631.1321661, http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.70.4145&rep=rep1&type=pdf
  * Precise memory leak detection for java software using container profiling: http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?arnumber=4814126, http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.116.8020&rep=rep1&type=pdf
    * done by tracking operations on containers
  * Visualizing Reference Patterns for Solving Memory Leaks in Java: http://www.springerlink.com/content/cfvp0ccf6j9dtnf6/, http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.15.2953&rep=rep1&type=pdf
    * Programmer identified periods of time for temporary objects
    * Snapshots

## Somewhat related ##
  * Automatic Removal of Array Memory Leaks in Java: http://www.springerlink.com/content/4nq7ydp9y8rkrqh5/
  * LeakBot: An Automated and Lightweight Tool for Diagnosing Memory Leaks in Large Java Applications: http://www.springerlink.com/content/3aubgw98fla82dyb/
  * Cork: Dynamic memory leak detection for garbage collected languages: http://portal.acm.org/citation.cfm?id=1190215.1190224