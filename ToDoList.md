This page contains required action-items: decisions and implementation tasks

# Decisions #

  1. Decide how the application under test (AUT) is run:
    * Either using SandboxLoader which is a separate Java application _or_
    * As a normal application with a command line argument of using the agent which in turns triggers sandboxing
  1. Define heap usage of untrusted package, so that we can enforce user-defined limit in a reasonably intuitive way.
    * Consider case where package holds reference to large object passed by client.
    * Consider case where package returns an instance of a class defined within the package.  Client may now call methods on this instance, which could result in new allocations, or in the instance keeping a reference to objects passed in by the client.
    * Candidate definition: All memory reachable from:
      * Static fields within classes defined by the package.
      * Instances of classes within the package that are returned to the client.
      * Object references stored on the stack of a thread created by the package.
    * How do we track all these references?  Proposed approach:
      * Keep track of static fields defined within the package.  Let's say the objects referenced in this way are in group A.
      * By instrumenting constructors, track all new instances of classes within the package.  This set of objects is group B.
      * To constrain project scope, ignore the third case (stack references).
    * How do we track when objects are freed?
      * Create WeakReference objects for all items in group B.  At some point after these objects are garbage collected, their weak references will be added to a queue we provide.

# Implementation Tasks #
  1. (Mike) Implement a JNI call that takes in a list of Java object references and calculates the total heap memory used by all objects reachable from that list.  This function needs to consider the possibility that the trees reachable from each object in the list may overlap each other, i.e. they may be non-disjoint.
  1. (Nick) Implement code in the Java layer to track all new instances of classes within the package, and detect which of these instances have not yet been garbage collected.