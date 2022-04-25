package spoiler.blocker.apps

import android.graphics.Rect
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityNodeInfo
import spoiler.blocker.OverlayView
import spoiler.blocker.SpoilerBlockerService.Companion.getBlockedTextIfFound

/**
 * Created by Muthuraj on 26/04/22.
 */
abstract class Blocker(
    windowManager: WindowManager,
    protected val overlayView: OverlayView,
    val packageName: String
) {

    protected val dy by lazy {
        windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
    }

    private val rect = Rect()

    /**
     * Blocks [AccessibilityNodeInfo] passed as receiver if [nodeToCheck] has blocked text.
     * Returns true if blocked and false if no text to block.
     * */
    fun AccessibilityNodeInfo.blockIfNeeded(nodeToCheck: AccessibilityNodeInfo = this): Boolean {
        val blockedText =
            nodeToCheck.getBlockedTextIfFound()
        if (blockedText != null) {
            getBoundsInScreen(rect)
            rect.offset(0, -dy)
            overlayView.addRect(rect, blockedText)
            return true
        }
        return false
    }

    abstract fun checkAndBlock(nodeInfo: AccessibilityNodeInfo?)
}