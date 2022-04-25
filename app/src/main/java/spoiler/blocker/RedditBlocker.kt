/* $Id$ */
package spoiler.blocker

import android.graphics.Rect
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import spoiler.blocker.SpoilerBlockerService.Companion.getBlockedTextIfFound
import spoiler.blocker.util.children
import spoiler.blocker.util.log
import spoiler.blocker.util.logE

/**
 * Created by Muthuraj on 25/03/22.
 */
class RedditBlocker(
    private val windowManager: WindowManager,
    private val overlayView: OverlayView
) {
    fun checkAndBlock(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        val dy =
            windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val rect = Rect()
        var blockedText: String? = null
        nodeInfo.children.forEachIndexed { index, childNode ->
            log { "child id: ${childNode.viewIdResourceName}, text: ${childNode.text}" }
            when (childNode.viewIdResourceName) {
                "reddit.news:id/title", "free.reddit.news:id/title" -> {
                    if (blockedText == null) {
                        blockedText = childNode.getBlockedTextIfFound()
                    }
                    if (blockedText != null) {
                        logE { "Blocking title with text: \"${childNode.text}\" at index: $index" }

                        childNode.getBoundsInScreen(rect)
                        rect.offset(0, -dy)
                        overlayView.addRect(rect, blockedText)
                    } else {
                        log { "Found title with text: \"${childNode.text}\" at index: $index" }
                    }

                    nodeInfo.children.forEachIndexed { childIndex, sibling ->
                        if (blockedText != null) {
                            if (childIndex > index && (sibling.viewIdResourceName == "reddit.news:id/imagePreview" || sibling.viewIdResourceName == "free.reddit.news:id/imagePreview")) {
                                sibling.getBoundsInScreen(rect)
                                logE { "Found imagePreview with text: \"${sibling.text}\" at index: $index for text: \"${childNode.text}\", rect: $rect" }
                                rect.offset(0, -dy)
                                overlayView.addRect(rect, blockedText)
                                logE { "adding overlay view width: ${rect.width()}, height: ${rect.height()}" }
                            }
                        }
                    }
                }
                "reddit.news:id/selftext", "reddit.news:id/imagePreview", "reddit.news:id/video", "reddit.news:id/linkFlair", "free.reddit.news:id/selftext", "free.reddit.news:id/imagePreview", "free.reddit.news:id/video", "free.reddit.news:id/linkFlair" -> {
                    if (blockedText == null) {
                        blockedText = childNode.getBlockedTextIfFound()
                    }
                    if (blockedText != null) {
                        logE { "Blocking title with self text: \"${childNode.text}\" at index: $index" }
                        childNode.getBoundsInScreen(rect)
                        rect.offset(0, -dy)
                        overlayView.addRect(rect, blockedText)
                    } else {
                        log { "Found title with self text: \"${childNode.text}\" at index: $index" }
                    }
                }
                else -> {
                    checkAndBlock(childNode)
                }
            }
        }
    }
}