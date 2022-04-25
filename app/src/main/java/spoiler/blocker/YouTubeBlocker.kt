/* $Id$ */
package spoiler.blocker

import android.graphics.Rect
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import spoiler.blocker.SpoilerBlockerService.Companion.getBlockedTextIfFound
import spoiler.blocker.util.children

/**
 * Created by Muthuraj on 25/03/22.
 */
class YouTubeBlocker(
    private val windowManager: WindowManager,
    private val overlayView: OverlayView
) {

    private val rect = Rect()
    private val innerRect = Rect()

    fun checkAndBlock(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        val dy =
            windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top

        val recyclerView =
            nodeInfo.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/results")
                ?.firstOrNull() ?: return

        val subtitles =
            nodeInfo.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/subtitle_window_identifier")
                .orEmpty()
        subtitles.forEach {subtitle->
            val blockedText = subtitle.getBlockedTextIfFound()
            if (blockedText != null) {
                subtitle.getBoundsInScreen(rect)
            }
            recyclerView.children.forEach {
                it.getBoundsInScreen(innerRect)
                if (innerRect.contains(rect)) {
                    innerRect.offset(0, -dy)
                    overlayView.addRect(innerRect, blockedText)
                }
            }
        }

        recyclerView.children.forEach {  childNode ->
            //child node 1st child content-desc
            val blockedText =
                childNode.children.firstOrNull()?.getBlockedTextIfFound(checkForContentDesc = true)
            if (blockedText != null) {
                childNode.getBoundsInScreen(rect)
                rect.offset(0, -dy)
                overlayView.addRect(rect, blockedText)
            }
        }
    }
}