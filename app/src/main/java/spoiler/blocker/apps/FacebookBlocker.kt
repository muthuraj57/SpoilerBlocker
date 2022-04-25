/* $Id$ */
package spoiler.blocker.apps

import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import spoiler.blocker.OverlayView
import spoiler.blocker.util.children

/**
 * Created by Muthuraj on 26/04/22.
 */
class FacebookBlocker(windowManager: WindowManager, overlayView: OverlayView) :
    Blocker(windowManager, overlayView, "com.facebook.katana") {

    override fun checkAndBlock(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        /**
         * Do DFS and if a node text is found, block the parent node to block the post.
         * */
        nodeInfo.children.forEach { child ->
            val isBlocked = nodeInfo.blockIfNeeded(child)
            if (isBlocked) {
                return@forEach
            }
            checkAndBlock(child)
        }
    }
}