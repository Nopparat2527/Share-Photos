package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FotoVaultTheme
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.VaultViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VaultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isOledBlack by viewModel.isOledBlack.collectAsState()
            val isVaultLocked by viewModel.isVaultLocked.collectAsState()

            FotoVaultTheme(darkTheme = isDarkMode, oledBlack = isOledBlack) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isVaultLocked) {
                        LockScreenDialog(viewModel = viewModel)
                    } else {
                        MainAppContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: VaultViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedPhoto by viewModel.selectedPhoto.collectAsState()
    val editingPhoto by viewModel.editingPhoto.collectAsState()
    val isThai by viewModel.isThai.collectAsState()

    // Dialog states
    val showAddPhoto by viewModel.showAddPhotoDialog.collectAsState()
    val showNotifications by viewModel.showNotificationDrawer.collectAsState()
    val showFaceSearch by viewModel.showFaceSearchDialog.collectAsState()
    val showAuth by viewModel.showAuthDialog.collectAsState()
    val buyTargetPhoto by viewModel.showBuyDialog.collectAsState()
    val slipResult by viewModel.lastSlipVerification.collectAsState()
    val orderResult by viewModel.lastOrderResult.collectAsState()
    val showNasSetup by viewModel.showNasSetupDialog.collectAsState()

    Scaffold(
        topBar = {
            if (editingPhoto == null && selectedPhoto == null) {
                AppTopBar(
                    viewModel = viewModel,
                    modifier = Modifier.statusBarsPadding()
                )
            }
        },
        bottomBar = {
            if (editingPhoto == null && selectedPhoto == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.VAULT,
                        onClick = { viewModel.currentTab.value = MainTab.VAULT },
                        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                        label = { Text(viewModel.tr("tab_vault")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("nav_vault")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.MARKETPLACE,
                        onClick = { viewModel.currentTab.value = MainTab.MARKETPLACE },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                        label = { Text(viewModel.tr("tab_market")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("nav_market")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.ANALYTICS,
                        onClick = { viewModel.currentTab.value = MainTab.ANALYTICS },
                        icon = { Icon(Icons.Default.Insights, contentDescription = null) },
                        label = { Text(viewModel.tr("tab_analytics")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("nav_analytics")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.CLOUD_SYNC,
                        onClick = { viewModel.currentTab.value = MainTab.CLOUD_SYNC },
                        icon = { Icon(Icons.Default.CloudSync, contentDescription = null) },
                        label = { Text(viewModel.tr("tab_cloud")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("nav_cloud")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.SETTINGS,
                        onClick = { viewModel.currentTab.value = MainTab.SETTINGS },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(viewModel.tr("tab_settings")) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                editingPhoto != null -> {
                    AiEditorScreen(
                        photo = editingPhoto!!,
                        viewModel = viewModel,
                        onBack = { viewModel.closeAiEditor() }
                    )
                }
                selectedPhoto != null -> {
                    PhotoDetailScreen(
                        photo = selectedPhoto!!,
                        viewModel = viewModel,
                        onBack = { viewModel.closePhotoDetail() }
                    )
                }
                else -> {
                    when (currentTab) {
                        MainTab.VAULT -> VaultScreen(viewModel = viewModel)
                        MainTab.MARKETPLACE -> MarketplaceScreen(viewModel = viewModel)
                        MainTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        MainTab.CLOUD_SYNC -> CloudSyncScreen(viewModel = viewModel)
                        MainTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }

            // Global Dialog Overlays
            if (showAddPhoto) {
                AddPhotoDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showAddPhotoDialog.value = false }
                )
            }

            if (buyTargetPhoto != null) {
                BuyPhotoDialog(
                    photo = buyTargetPhoto!!,
                    viewModel = viewModel,
                    onDismiss = { viewModel.showBuyDialog.value = null }
                )
            }

            if (slipResult != null && orderResult != null) {
                SlipVerificationDialog(
                    slip = slipResult!!,
                    order = orderResult!!,
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.lastSlipVerification.value = null
                        viewModel.lastOrderResult.value = null
                    }
                )
            }

            if (showFaceSearch) {
                FaceSearchDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showFaceSearchDialog.value = false }
                )
            }

            if (showAuth) {
                AuthDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showAuthDialog.value = false }
                )
            }

            if (showNotifications) {
                NotificationDrawer(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showNotificationDrawer.value = false }
                )
            }

            if (showNasSetup) {
                NasSetupDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.showNasSetupDialog.value = false }
                )
            }
        }
    }
}
