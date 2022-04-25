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
open class Blocker(windowManager: WindowManager, protected val overlayView: OverlayView) {

    protected val dy by lazy {
        windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
    }

    private val rect = Rect()

    fun AccessibilityNodeInfo.blockIfNeeded(
        nodeToCheck: AccessibilityNodeInfo = this,
        checkForContentDesc: Boolean = false
    ) {
        val blockedText =
            nodeToCheck.getBlockedTextIfFound(checkForContentDesc = checkForContentDesc)
        if (blockedText != null) {
            getBoundsInScreen(rect)
            rect.offset(0, -dy)
            overlayView.addRect(rect, blockedText)
        }
    }

}