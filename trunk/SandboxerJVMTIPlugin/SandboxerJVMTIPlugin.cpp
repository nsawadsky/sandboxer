/*
 * JVMTIPlugin.c
 *
 *  Created on: Nov 8, 2010
 *      Author: Nick
 */

#include <jvmti.h>
#include <stdio.h>

#include "ca_ubc_cs_sandboxer_core_SandboxAppLoader.h"

static int GBL_agentLoaded = 0;

JNIEXPORT void JNICALL
Java_ca_ubc_cs_sandboxer_core_SandboxAppLoader_printMessage(JNIEnv *env, jclass cls, jstring javaMsg) {
    const char* msg = env->GetStringUTFChars(javaMsg, NULL);
    if (msg == NULL) {
        return;
    }
    printf("msg: %s, agent loaded: %d\n", msg, GBL_agentLoaded);
    fflush(stdout);
    env->ReleaseStringUTFChars(javaMsg, NULL);
}

