# problem breakdown

# Terminology #
  * Application -
  * Library (Untrusted library) -
  * Trusted library -
  * Sandboxer -
  * Sandboxer Plugin -
  * Application context -
  * Library private context -
  * Memory buildup - memory referenced by the library
  * Reference buildup - references the library hold to object that were not allocated by the library

# Memory Access #

## Requirement ##
A key requirement is putting leaking libraries into quarantine. The challenge is telling when library is leaking memory. In Java there is no meaning to memory ownership: when an object has reference(s) to it, it is kept alive, when all references are gone the object will be scheduled for garbage collection.
The issue now is how to define library memory consumption: the library may hold references to memory that it did not allocate, or conversely allocate memory then return it to the caller and get rid of the reference. Which is considered as library memory?

## Library memory ##

### Memory Buildup ###
Which objects take part in library memory buildup?
  * Any allocation of a class defined in the library.
  * Any allocation that happens within a library method and a reference to it is being stored in a library class.
    * (Is this the same as the above?) Any allocation that happens within a library method and not returned to caller.

### Reference Buildup ###
  * Object allocated in library members but not reachable through library references (either returned from a member call or inserted into caller supplied arguments)

## Memory Model ##
Here we deal with objects referenced by the library. First a list of possible allocation/access models (+ leak, - no leak, ? not sure):
  1. Static library objects - objects allocated by library code, referenced by library internally not exposed to other parties.
  1. Member arguments - arguments passed by the caller (application) to a library member function (callee).
    1. "const" objects - which are used by the member function but not modified or stored in the class.
    1. Modified objects - objects which are modified (i.e. hash-maps get new values)
    1. Stored references - Object references are copied for library future use.
  1. Library stack objects - objects allocated by the library during a method call. These can be subdivided into:
    1. Temporary objects - disposed once member function returns
    1. Returned Objects - are returned to caller
    1. Stored Objects - stored internally by the library, not exposed to caller
  1. Library context objects - object created on a private library context (thread)