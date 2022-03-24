/* $Id$ */
package spoiler.blocker.util

import android.util.Log

/**
 * Created by Muthuraj on 03/03/22.
 */
inline fun Any.log(showLog: Boolean = true, logger: () -> String) {
    if (showLog) {
        Log.d("${this::class.java.simpleName}:${System.identityHashCode(this)}", logger())
    }
}

inline fun Any.logI(showLog: Boolean = true, logger: () -> String) {
    if (showLog) {
        Log.i("${this::class.java.simpleName}:${System.identityHashCode(this)}", logger())
    }
}

inline fun Any.logE(showLog: Boolean = true, logger: () -> String) {
    if (showLog) {
        Log.e("${this::class.java.simpleName}:${System.identityHashCode(this)}", logger())
    }
}

inline fun Any.logV(showLog: Boolean = true, logger: () -> String) {
    if (showLog) {
        Log.v("${this::class.java.simpleName}:${System.identityHashCode(this)}", logger())
    }
}

inline fun Any.logW(showLog: Boolean = true, logger: () -> String) {
    if (showLog) {
        Log.w("${this::class.java.simpleName}:${System.identityHashCode(this)}", logger())
    }
}

fun Throwable.printDebugStackTrace() {
    printStackTrace()
}