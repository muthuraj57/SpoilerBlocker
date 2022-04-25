/* $Id$ */
package spoiler.blocker.apps

import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import spoiler.blocker.OverlayView

/**
 * Created by Muthuraj on 26/04/22.
 */
class WhatsAppBlocker(
    windowManager: WindowManager,
    overlayView: OverlayView
) : Blocker(windowManager, overlayView, "com.whatsapp") {

    override fun checkAndBlock(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        //Full screen text-status.
        nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/message_text")
            .orEmpty()
            .forEach { childNode ->
                childNode.blockIfNeeded()
            }

        //Full screen image/video status with caption. We are checking blocked words in caption and
        //blocking whole status if blocked word is found.
        nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/status_container")
            .orEmpty()
            .forEach { statusContainer ->
                nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/caption")
                    .orEmpty()
                    .forEach {
                        statusContainer.blockIfNeeded(it)
                    }
            }
    }
}