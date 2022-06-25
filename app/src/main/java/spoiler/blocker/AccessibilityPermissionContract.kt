/* $Id$ */
package spoiler.blocker

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

/**
 * Created by Muthuraj on 26/06/22.
 *
 * Jambav, Zoho Corporation
 */
class AccessibilityPermissionContract : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return SpoilerBlockerService.isEnabled
    }
}