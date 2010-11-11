/*
 * JVMTIPlugin.c
 *
 *  Created on: Nov 8, 2010
 *      Author: Nick
 */

#include <windows.h>
#include <jvmti.h>
#include <stdio.h>

#include "ca_ubc_cs_sandboxer_SandboxAppLoader.h"

static int GBL_agentLoaded = 0;

/*
 * Initialization function.
 */
JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    GBL_agentLoaded = 1;
    return 0;
}

JNIEXPORT void JNICALL
Java_ca_ubc_cs_sandboxer_SandboxAppLoader_printMessage(JNIEnv *env, jclass class, jstring javaMsg) {
    const char* msg = (*env)->GetStringUTFChars(env, javaMsg, NULL);
    if (msg == NULL) {
        return;
    }
    printf("msg: %s, agent loaded: %d", msg, GBL_agentLoaded);
    (*env)->ReleaseStringUTFChars(env, javaMsg, NULL);
}

