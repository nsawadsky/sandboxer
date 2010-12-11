/*
 *  SandboxGuardian.cpp
 *
 */

#include <stdlib.h>
#include <memory.h>
#include <jvmti.h>

#include "ca_ubc_cs_sandboxer_core_SandboxGuardian.h"

// some ugly globals
jvmtiEnv *gTiEnv = NULL;

jvmtiEventCallbacks gCallbacks;
jvmtiCapabilities gCapabilities;

// log for debug
FILE *logfile = NULL;

#define LOG_ERROR(f) if (err != JVMTI_ERROR_NONE) fprintf(logfile, "Error %d in %s\n", err, f)

void JNICALL ClassLoad_Callback(jvmtiEnv *jvmti_env,
                                JNIEnv* jni_env,
                                jthread thread,
                                jclass klass)
{
    fprintf(logfile, "ClassLoad\n");
    jvmtiError err = (jvmtiError)0;

    char *className = NULL;
    char *genericName = NULL;
    err = gTiEnv->GetClassSignature(klass, &className, &genericName);
    LOG_ERROR("GetClassSignature");

    fprintf(logfile, "Class-name=%s, generic-name=%s\n", className, genericName);
}

// Tool interface entry point, called before java code loading
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *vm, 
                                    char *options, 
                                    void *reserved)
{
    logfile = fopen("run-log.txt", "wt");
    fprintf(logfile, "Agent_OnLoad\n");
    fprintf(logfile, "agent arguments: %s\n", options);

    jvmtiError err = JVMTI_ERROR_NONE;

    // initialize the Tool-Interface environment to be used bt subsequent calls
    err = (jvmtiError) vm->GetEnv((void **)&gTiEnv, JVMTI_VERSION_1_0);
    
    // set capabilities
    memset(&gCapabilities, 0, sizeof(gCapabilities));
    gCapabilities.can_tag_objects = 1;
    gCapabilities.can_generate_exception_events = 1;
    gCapabilities.can_generate_frame_pop_events = 1;
    gCapabilities.can_generate_all_class_hook_events = 1;
    err = gTiEnv->AddCapabilities(&gCapabilities);

    LOG_ERROR("AddCapabilities");

    // set the callbacks for JVM events
    memset(&gCallbacks, 0, sizeof(gCallbacks));
    gCallbacks.ClassLoad = ClassLoad_Callback;
    err = gTiEnv->SetEventCallbacks(&gCallbacks, sizeof(gCallbacks));

    LOG_ERROR("SetEventCallbacks");

    err = gTiEnv->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_LOAD, NULL);
    LOG_ERROR("SetEventNotificationMode");

    fprintf(logfile, "\n");

    return err;
}

JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm)
{
    fprintf(logfile, "Agent_OnUnload\n");
    if (logfile != NULL) {
        fclose(logfile);
        logfile = NULL;
    }
    gTiEnv = NULL;
}

// not used in our case, may be attached to running vm - looks promising
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, 
                                      char *options, 
                                      void *reserved)
{
    return 0;
}

//////////////// Iteration ////////////////

// we pass a structure to the iterator so that more complex iterations can be added in the future.
typedef struct {
    jlong tag;
    // Total number of bytes for this iteration.
    long count;
    // Total number of distinct objects found in this iteration.
    long objectCount;
} Iteration;

// TODO: we can actually break down the summation by reference_kind/class etc
static jvmtiIterationControl JNICALL 
Iteration_Callback(jvmtiObjectReferenceKind reference_kind, 
                   jlong class_tag, 
                   jlong size, 
                   jlong* tag_ptr, 
                   jlong referrer_tag, 
                   jint referrer_index, 
                   void* user_data)
{
    //TODO: Should we check just for reference_kind of JVMTI_REFERENCE_FIELD and JVMTI_REFERENCE_ARRAY_ELEMENT ?

    // Ignoring references from instances to their corresponding class.
    if (reference_kind == JVMTI_REFERENCE_CLASS) {
        return JVMTI_ITERATION_IGNORE;
    }

    Iteration *iteration = (Iteration*)user_data;
    if (tag_ptr == NULL || *tag_ptr != iteration->tag) {
        iteration->count += size;
        iteration->objectCount++;
        if (tag_ptr != NULL) {
            *tag_ptr = iteration->tag;
        } else { //NULL tag, we do not have the jobject to tag!
            //TODO gTiEnv->SetTag(, iteration->tag);
        }

        return JVMTI_ITERATION_CONTINUE;
    }
    return JVMTI_ITERATION_IGNORE;
}

JNIEXPORT jlong JNICALL 
Java_ca_ubc_cs_sandboxer_core_SandboxGuardian_getReferencedSize(JNIEnv *env, 
                                                                jclass klass, 
                                                                jobjectArray objects)
{
    static jlong iteration_counter = 1;
    jvmtiError err = JVMTI_ERROR_NONE;

    Iteration iteration;
    iteration.count = 0;
    iteration.objectCount = 0;
    iteration.tag = iteration_counter++;

    jsize num_elements = env->GetArrayLength(objects);

    fflush(stdout);

    for (int i=0; i<num_elements; ++i) {
        jobject object = env->GetObjectArrayElement(objects, i);
        jlong tag = 0;
        gTiEnv->GetTag(object, &tag);
        if (tag != iteration.tag) {
            gTiEnv->SetTag(object, iteration.tag);

            jlong size = 0;
            gTiEnv->GetObjectSize(object, &size);
            iteration.count += size;
            iteration.objectCount++;

            err = gTiEnv->IterateOverObjectsReachableFromObject(object,
                                                                Iteration_Callback,
                                                                (const void*)&iteration);
            switch( err ) {
                case JVMTI_ERROR_MUST_POSSESS_CAPABILITY: //this should not happen
                    break;
                case JVMTI_ERROR_INVALID_OBJECT:
                    break;
                case JVMTI_ERROR_NULL_POINTER:
                    break;
            }
        }
    }
    
    printf("Total heap objects = %ld\n", iteration.objectCount);
    fflush(stdout);

    return iteration.count;
}
