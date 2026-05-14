package dev.rufex.aguaribay.ui.confirmation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

class ConfirmationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(EXTRA_PACKAGE) ?: run { finish(); return }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = goHome()
            },
        )

        val isRecheck = intent.getBooleanExtra(EXTRA_IS_RECHECK, false)

        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                ConfirmationScreen(
                    packageName = packageName,
                    isRecheck = isRecheck,
                    onConfirm = { viewModel ->
                        if (isRecheck) {
                            viewModel.rescheduleRecheck(packageName)
                            finish()
                        } else {
                            viewModel.confirmOpen(packageName)
                            openApp(packageName)
                        }
                    },
                    onDecline = { goHome() },
                )
            }
        }
    }

    private fun openApp(targetPackage: String) {
        packageManager.getLaunchIntentForPackage(targetPackage)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }?.let { startActivity(it) }
        finish()
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
        )
        finish()
    }

    companion object {
        const val EXTRA_PACKAGE = "extra_package"
        const val EXTRA_IS_RECHECK = "extra_is_recheck"
    }
}
