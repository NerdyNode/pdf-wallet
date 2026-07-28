package com.pdfwallet.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pdfwallet.ui.home.HomeScreen
import com.pdfwallet.ui.lock.AppLockScreen
import com.pdfwallet.ui.theme.PDFWalletTheme
import com.pdfwallet.ui.wallet.WalletScreen
import com.pdfwallet.util.NotificationPermissionHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var notificationPermissionHelper: NotificationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        notificationPermissionHelper = NotificationPermissionHelper(this)
        notificationPermissionHelper.checkAndRequestPermission()

        setContent {
            PDFWalletTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "lock") {
                        composable("lock") {
                            AppLockScreen(onUnlocked = {
                                navController.navigate("home") {
                                    popUpTo("lock") { inclusive = true }
                                }
                            })
                        }
                        composable("home") {
                            HomeScreen(
                                onNavigateToWallet = { navController.navigate("wallet") }
                            )
                        }
                        composable("wallet") {
                            WalletScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
