package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.VaultRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab {
    VAULT,
    MARKETPLACE,
    ANALYTICS,
    CLOUD_SYNC,
    SETTINGS,
    PURCHASES,
    BUYER_WALLET
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    val repository = VaultRepository(application)

    // User Profile, Role & Authentication State
    val currentUser: StateFlow<UserProfile> = repository.currentUser.asStateFlow()
    val showAuthDialog = MutableStateFlow(false)
    val currentRole = MutableStateFlow(UserRole.ADMIN)
    val showRoleSwitchDialog = MutableStateFlow(false)

    // Buyer Specific State
    val buyerWalletBalance = MutableStateFlow(500.0)
    val buyerPurchasedPhotoIds = MutableStateFlow<Set<String>>(setOf("photo-2"))
    val isDownloadingPhoto = MutableStateFlow<String?>(null)

    // UI Navigation
    val currentTab = MutableStateFlow(MainTab.VAULT)
    val selectedPhoto = MutableStateFlow<VaultPhoto?>(null)
    val editingPhoto = MutableStateFlow<VaultPhoto?>(null)

    // AI Editor Working State
    val editBrightness = MutableStateFlow(0f)
    val editContrast = MutableStateFlow(1f)
    val editSaturation = MutableStateFlow(1f)
    val editWarmth = MutableStateFlow(0f)
    val editFilter = MutableStateFlow("None")
    val isComparingOriginal = MutableStateFlow(false)

    // Search, Category, and Face Recognition Filtering
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("all")
    val activeFaceFilter = MutableStateFlow<String?>(null)
    val showFaceSearchDialog = MutableStateFlow(false)
    val isScanningFace = MutableStateFlow(false)
    val faceScanResult = MutableStateFlow<FaceScanResult?>(null)

    // Language & Theme Settings
    val isThai = MutableStateFlow(true) // Default Thai as requested by prompt
    val isDarkMode = MutableStateFlow(true)
    val isOledBlack = MutableStateFlow(false)
    val isPowerSaving = MutableStateFlow(false)

    // Security & Lock
    val isPinEnabled = MutableStateFlow(false)
    val isVaultLocked = MutableStateFlow(false)
    val correctPin = MutableStateFlow("1234")
    val pinInput = MutableStateFlow("")
    val pinErrorMessage = MutableStateFlow<String?>(null)

    // NAS State & Dialogs
    val nasConfig: StateFlow<NasConfig> = repository.nasConfig.asStateFlow()
    val nasSyncState: StateFlow<NasSyncState> = repository.nasSyncState.asStateFlow()
    val showNasSetupDialog = MutableStateFlow(false)
    val isTestingNas = MutableStateFlow(false)

    // Modals
    val showAddPhotoDialog = MutableStateFlow(false)
    val showNotificationDrawer = MutableStateFlow(false)
    val showBuyDialog = MutableStateFlow<VaultPhoto?>(null)
    val lastOrderResult = MutableStateFlow<PurchaseOrder?>(null)
    val lastSlipVerification = MutableStateFlow<SlipVerificationResult?>(null)

    // All photos from Room
    val allPhotos: StateFlow<List<VaultPhoto>> = repository.allPhotosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered photos (Category + Query + Face Recognition + Private Owner Only)
    val filteredPhotos: StateFlow<List<VaultPhoto>> = combine(
        allPhotos,
        searchQuery,
        selectedCategory,
        activeFaceFilter,
        currentRole
    ) { photos, query, category, faceFilter, role ->
        photos.filter { photo ->
            // Buyer Role Strict Security: NEVER expose owner-private photos to buyers
            if (role == UserRole.BUYER && photo.isPrivateOwnerOnly) {
                return@filter false
            }

            // Category check: Support "private" owner vault & "people"
            val matchCategory = when (category) {
                "all" -> true
                "private" -> if (role == UserRole.ADMIN) photo.isPrivateOwnerOnly else false
                "people" -> photo.detectedFaces.isNotEmpty() || photo.category.equals("Portrait", ignoreCase = true)
                else -> photo.category.contains(category, ignoreCase = true)
            }

            // Text search check
            val cleanQuery = query.trim().removePrefix("#").lowercase()
            val matchQuery = cleanQuery.isEmpty() ||
                    photo.title.lowercase().contains(cleanQuery) ||
                    photo.description.lowercase().contains(cleanQuery) ||
                    photo.tags.any { it.lowercase().contains(cleanQuery) } ||
                    photo.detectedFaces.any { it.lowercase().contains(cleanQuery) }

            // Face filter check
            val matchFace = if (faceFilter == null || faceFilter.isBlank()) {
                true
            } else {
                photo.detectedFaces.any { it.contains(faceFilter, ignoreCase = true) } ||
                        photo.title.contains(faceFilter, ignoreCase = true)
            }

            matchCategory && matchQuery && matchFace
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All distinct people/faces detected across the vault
    val detectedPeopleList: StateFlow<List<String>> = allPhotos.map { photos ->
        photos.flatMap { it.detectedFaces }.filter { it.isNotBlank() }.distinct()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Nopparat (You)", "Emma", "Kenji", "Alice"))

    // Marketplace photos (Buyers only see public listed photos)
    val marketplacePhotos: StateFlow<List<VaultPhoto>> = combine(allPhotos, currentRole) { photos, role ->
        photos.filter { photo ->
            if (role == UserRole.BUYER) {
                photo.isMarketplaceListed && !photo.isPrivateOwnerOnly
            } else {
                photo.isMarketplaceListed
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Buyer Purchased Photos
    val buyerPurchasedPhotos: StateFlow<List<VaultPhoto>> = combine(
        allPhotos,
        buyerPurchasedPhotoIds
    ) { photos, purchasedIds ->
        photos.filter { it.id in purchasedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sales Analytics (Admin only)
    val analyticsData: StateFlow<SalesAnalyticsData> = allPhotos.map { photos ->
        repository.getSalesAnalytics(photos)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.getSalesAnalytics(emptyList())
    )

    fun tr(key: String): String {
        return LanguageStrings.get(key, isThai.value)
    }

    // Face Search & Recognition Workflow
    fun performFaceScan(sourceDesc: String, personNameTarget: String = "Nopparat (You)") {
        viewModelScope.launch {
            isScanningFace.value = true
            delay(1100) // Realistic facial geometry and embedding neural net scan

            val all = allPhotos.value
            // Find matches in vault
            val matches = all.mapNotNull { p ->
                val hasMatch = p.detectedFaces.any { it.contains(personNameTarget, ignoreCase = true) } ||
                        p.category.equals("Portrait", ignoreCase = true)
                if (hasMatch) {
                    val confidence = when {
                        p.title.contains("Emma", ignoreCase = true) -> 0.984f
                        p.category.contains("Portrait", ignoreCase = true) -> 0.962f
                        p.title.contains("Tokyo", ignoreCase = true) -> 0.938f
                        else -> 0.895f
                    }
                    Pair(p, confidence)
                } else null
            }.sortedByDescending { it.second }

            faceScanResult.value = FaceScanResult(
                scannedPhotoTitle = sourceDesc,
                detectedPeople = listOf(personNameTarget),
                landmarksDetected = 68,
                matchingPhotos = matches
            )
            isScanningFace.value = false
            activeFaceFilter.value = personNameTarget
        }
    }

    fun clearFaceFilter() {
        activeFaceFilter.value = null
        faceScanResult.value = null
    }

    // Authentication methods
    fun loginWithEmail(email: String, name: String = "") {
        repository.loginWithEmail(email, name)
        showAuthDialog.value = false
    }

    fun loginWithSocial(provider: AuthProvider) {
        repository.loginWithSocial(provider)
        showAuthDialog.value = false
    }

    fun logout() {
        repository.logout()
        showAuthDialog.value = false
    }

    fun openPhotoDetail(photo: VaultPhoto) {
        selectedPhoto.value = photo
    }

    fun closePhotoDetail() {
        selectedPhoto.value = null
    }

    fun startAiEditor(photo: VaultPhoto) {
        editingPhoto.value = photo
        editBrightness.value = photo.brightness
        editContrast.value = photo.contrast
        editSaturation.value = photo.saturation
        editWarmth.value = photo.warmth
        editFilter.value = photo.activeFilter
        isComparingOriginal.value = false
    }

    fun closeAiEditor() {
        editingPhoto.value = null
    }

    fun applyAutoEnhance() {
        editBrightness.value = 0.08f
        editContrast.value = 1.25f
        editSaturation.value = 1.30f
        editWarmth.value = 0.10f
        editFilter.value = "Smart HDR"
    }

    fun resetAiEditor() {
        editBrightness.value = 0f
        editContrast.value = 1f
        editSaturation.value = 1f
        editWarmth.value = 0f
        editFilter.value = "None"
    }

    fun saveAiEditorChanges() {
        val current = editingPhoto.value ?: return
        val updated = current.copy(
            aiEnhanced = true,
            brightness = editBrightness.value,
            contrast = editContrast.value,
            saturation = editSaturation.value,
            warmth = editWarmth.value,
            activeFilter = editFilter.value
        )
        viewModelScope.launch {
            repository.updatePhoto(updated)
            editingPhoto.value = null
            if (selectedPhoto.value?.id == updated.id) {
                selectedPhoto.value = updated
            }
        }
    }

    fun toggleMarketplaceListing(photo: VaultPhoto) {
        val updated = photo.copy(isMarketplaceListed = !photo.isMarketplaceListed)
        viewModelScope.launch {
            repository.updatePhoto(updated)
            if (selectedPhoto.value?.id == updated.id) {
                selectedPhoto.value = updated
            }
        }
    }

    fun addPhoto(
        title: String,
        description: String,
        category: String,
        tags: List<String>,
        localUri: String,
        priceThb: Double = 150.0,
        isMarketplace: Boolean = false,
        detectedFaces: List<String> = emptyList(),
        isPrivateOwnerOnly: Boolean = true
    ) {
        viewModelScope.launch {
            repository.addPhoto(
                title = title,
                description = description,
                category = category,
                tags = tags,
                localUri = localUri,
                resDrawableName = if (localUri.isBlank()) "img_sample_sunset" else "",
                priceThb = priceThb,
                isMarketplaceListed = isMarketplace,
                detectedFaces = detectedFaces,
                isPrivateOwnerOnly = isPrivateOwnerOnly
            )
            showAddPhotoDialog.value = false
        }
    }

    fun deletePhoto(photo: VaultPhoto) {
        viewModelScope.launch {
            repository.deletePhoto(photo.id)
            if (selectedPhoto.value?.id == photo.id) {
                selectedPhoto.value = null
            }
        }
    }

    fun triggerSync() {
        viewModelScope.launch {
            repository.syncAllNow()
        }
    }

    fun switchRole(newRole: UserRole) {
        currentRole.value = newRole
        if (newRole == UserRole.ADMIN) {
            repository.currentUser.value = UserProfile(
                userId = "usr-8829",
                name = "Nopparat",
                email = "nopparat45081@gmail.com",
                avatarEmoji = "👑",
                provider = AuthProvider.GOOGLE,
                isLoggedIn = true,
                joinedDate = "October 2026",
                token = "fv_sec_token_9821a",
                role = UserRole.ADMIN
            )
            if (currentTab.value == MainTab.PURCHASES || currentTab.value == MainTab.BUYER_WALLET) {
                currentTab.value = MainTab.VAULT
            }
        } else {
            repository.currentUser.value = UserProfile(
                userId = "buyer-102",
                name = "Somchai (Buyer)",
                email = "buyer.somchai@gmail.com",
                avatarEmoji = "🛍️",
                provider = AuthProvider.GOOGLE,
                isLoggedIn = true,
                joinedDate = "October 2026",
                token = "fv_sec_token_buyer77",
                role = UserRole.BUYER,
                buyerBalanceThb = buyerWalletBalance.value,
                buyerPoints = 350
            )
            if (currentTab.value == MainTab.VAULT || currentTab.value == MainTab.ANALYTICS || currentTab.value == MainTab.CLOUD_SYNC) {
                currentTab.value = MainTab.MARKETPLACE
            }
        }
        showRoleSwitchDialog.value = false
    }

    fun buyPhoto(photo: VaultPhoto, paymentMethod: PaymentMethod, pointsToUse: Int) {
        val (slip, order) = repository.verifySlipAndCompletePurchase(photo, paymentMethod, pointsToUse)
        buyerPurchasedPhotoIds.value = buyerPurchasedPhotoIds.value + photo.id
        // Deduct from buyer wallet balance if applicable
        if (buyerWalletBalance.value >= order.netAmountThb) {
            buyerWalletBalance.value = (buyerWalletBalance.value - order.netAmountThb).coerceAtLeast(0.0)
        }
        lastSlipVerification.value = slip
        lastOrderResult.value = order
        showBuyDialog.value = null
    }

    fun topUpBuyerWallet(amount: Double) {
        buyerWalletBalance.value += amount
        val notif = VaultNotification(
            id = java.util.UUID.randomUUID().toString(),
            titleEn = "Wallet Top-Up +฿${amount.toInt()}",
            titleTh = "เติมเงินเข้ากระเป๋าสำเร็จ +฿${amount.toInt()}",
            messageEn = "PromptPay bank transfer verified. New wallet balance: ฿${buyerWalletBalance.value.toInt()}",
            messageTh = "ตรวจสอบยอดชำระ PromptPay เรียบร้อยแล้ว ยอดเงินคงเหลือใหม่: ฿${buyerWalletBalance.value.toInt()}",
            timeAgo = "Just now",
            type = NotificationType.POINTS_EARNED
        )
        repository.addNotification(notif)
    }

    fun downloadPurchasedPhoto(photo: VaultPhoto, onComplete: () -> Unit) {
        viewModelScope.launch {
            isDownloadingPhoto.value = photo.id
            delay(1200) // Simulate fast high-res raw photo decryption and download
            isDownloadingPhoto.value = null
            val notif = VaultNotification(
                id = java.util.UUID.randomUUID().toString(),
                titleEn = "Photo Downloaded",
                titleTh = "ดาวน์โหลดภาพความละเอียดสูงสำเร็จ",
                messageEn = "'${photo.title}' decrypted and saved to your device gallery (License Verified).",
                messageTh = "ถอดรหัสและบันทึกรูป '${photo.title}' ลงในแกลเลอรีของเครื่องเรียบร้อยแล้ว",
                timeAgo = "Just now",
                type = NotificationType.CLOUD_BACKUP
            )
            repository.addNotification(notif)
            onComplete()
        }
    }

    fun unlockVault() {
        if (pinInput.value == correctPin.value) {
            isVaultLocked.value = false
            pinInput.value = ""
            pinErrorMessage.value = null
        } else {
            pinErrorMessage.value = if (isThai.value) "รหัส PIN ไม่ถูกต้อง" else "Incorrect PIN code"
        }
    }

    fun syncToNasNow() {
        viewModelScope.launch {
            repository.syncAllToNas()
        }
    }

    fun testNas(config: NasConfig, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            isTestingNas.value = true
            val success = repository.testNasConnection(config)
            isTestingNas.value = false
            onComplete?.invoke(success)
        }
    }

    fun saveNasConfig(config: NasConfig) {
        repository.nasConfig.value = config
        showNasSetupDialog.value = false
    }

    fun lockVault() {
        if (isPinEnabled.value) {
            isVaultLocked.value = true
        }
    }
}
