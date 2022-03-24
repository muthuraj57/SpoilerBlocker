/* $Id$ */
package spoiler.blocker.util

import android.view.View
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Created by Muthuraj on 04/03/22.
 */
operator fun AccessibilityNodeInfo.iterator(): Iterator<AccessibilityNodeInfo?> =
    object : Iterator<AccessibilityNodeInfo> {
        private var index = 0
        override fun hasNext() = index < childCount
        override fun next() = getChild(index++)
    }

val AccessibilityNodeInfo.children: Sequence<AccessibilityNodeInfo>
    get() = object : Sequence<AccessibilityNodeInfo?> {
        override fun iterator() = this@children.iterator()
    }.filterNotNull()

fun View.makeVisible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.makeInVisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

fun View.makeGone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}