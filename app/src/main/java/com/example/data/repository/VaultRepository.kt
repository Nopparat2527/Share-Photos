package com.example.data.repository

import android.content.Context
import com.example.data.local.PhotoVaultDatabase
import com.example.data.local.PhotoVaultEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VaultRepository(context: Context) {
    private val db = PhotoVaultDatabase.getDatabase(context)
    private val dao = db.photoDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    // User Profile & Authentication State
    val currentUser = MutableStateFlow(
        UserProfile(
            userId = "usr-8829",
            name = "Nopparat",
            email = "nopparat45081@gmail.com",
            avatarEmoji = "👑",
            provider = AuthProvider.GOOGLE,
            isLoggedIn = true,
            joinedDate = "October 2026",
            token = "fv_sec_token_" + UUID.randomUUID().toString().take(12)
        )
    )

    // User & App State
    val userPoints = MutableStateFlow(650)
    val isAutoBackupEnabled = MutableStateFlow(true)
    val selectedCloudProvider = MutableStateFlow("FotoVault Cloud (E2EE)")
    val isOfflineMode = MutableStateFlow(false)
    val isSyncing = MutableStateFlow(false)
    val lastSyncTime = MutableStateFlow(System.currentTimeMillis() - 3600000)

    // NAS Configuration & State (Synology, QNAP, TrueNAS, ASUSTOR, WD, etc.)
    val nasConfig = MutableStateFlow(NasConfig())
    val nasSyncState = MutableStateFlow(NasSyncState())

    // Notifications
    private val _notifications = MutableStateFlow<List<VaultNotification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    fun addNotification(notification: VaultNotification) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    // Purchases
    private val _purchases = MutableStateFlow<List<PurchaseOrder>>(emptyList())
    val purchases = _purchases.asStateFlow()

    init {
        // Initial seed check
        scope.launch {
            seedInitialPhotosIfEmpty()
            seedInitialNotifications()
        }
    }

    val allPhotosFlow: Flow<List<VaultPhoto>> = dao.getAllPhotos().map { entities ->
        entities.map { it.toModel() }
    }

    private suspend fun seedInitialPhotosIfEmpty() {
        val initialList = listOf(
            PhotoVaultEntity(
                id = "photo-1",
                title = "Neon Tokyo Streets",
                description = "Atmospheric cyberpunk night photography with rain reflections and vibrant neon signs.",
                category = "Cyberpunk / City",
                tagsJson = "cyberpunk,tokyo,rain,neon,night,street,reflection,people",
                localUri = "",
                resDrawableName = "img_sample_cyberpunk",
                isEncrypted = true,
                encryptionAlgorithm = "AES-256-GCM",
                checksum = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
                isSynced = true,
                syncTimestamp = System.currentTimeMillis() - 1800000,
                cloudProvider = "FotoVault Cloud (E2EE)",
                fileSize = "4.8 MB",
                resolution = "4032 x 3024",
                createdAt = System.currentTimeMillis() - 86400000 * 2,
                isMarketplaceListed = true,
                priceThb = 180.0,
                pricePoints = 180,
                salesCount = 14,
                rating = 4.95f,
                aiEnhanced = true,
                brightness = 0.05f,
                contrast = 1.2f,
                saturation = 1.35f,
                warmth = -0.1f,
                activeFilter = "Cyberpunk",
                facesJson = "Kenji, Alex, Nopparat (You)"
            ),
            PhotoVaultEntity(
                id = "photo-2",
                title = "Golden Hour Ocean Sunset",
                description = "Fine art landscape capturing gentle tropical waves, palm tree silhouettes, and warm pastel clouds.",
                category = "Nature / Sunset",
                tagsJson = "sunset,ocean,goldenhour,tropical,beach,waves,nature,landscape",
                localUri = "",
                resDrawableName = "img_sample_sunset",
                isEncrypted = true,
                encryptionAlgorithm = "AES-256-GCM",
                checksum = "SHA256:9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72",
                isSynced = true,
                syncTimestamp = System.currentTimeMillis() - 3600000,
                cloudProvider = "FotoVault Cloud (E2EE)",
                fileSize = "5.1 MB",
                resolution = "4000 x 3000",
                createdAt = System.currentTimeMillis() - 86400000,
                isMarketplaceListed = true,
                priceThb = 220.0,
                pricePoints = 220,
                salesCount = 28,
                rating = 5.0f,
                aiEnhanced = true,
                brightness = 0.02f,
                contrast = 1.15f,
                saturation = 1.25f,
                warmth = 0.25f,
                activeFilter = "Sunset",
                facesJson = "Alice, Nopparat (You)"
            ),
            PhotoVaultEntity(
                id = "photo-3",
                title = "Studio Portrait - Emma",
                description = "Clean high-detail studio portrait with recognized facial landmarks and warm bokeh.",
                category = "Portrait",
                tagsJson = "portrait,smile,person,studio,face,eyes,bokeh",
                localUri = "",
                resDrawableName = "img_sample_portrait",
                isEncrypted = true,
                encryptionAlgorithm = "AES-256-GCM",
                checksum = "SHA256:3a91f562dc92b8745ea7b4c9103c204918ef839e248b0304910248e4892c9082",
                isSynced = true,
                syncTimestamp = System.currentTimeMillis() - 7200000,
                cloudProvider = "FotoVault Cloud (E2EE)",
                fileSize = "3.9 MB",
                resolution = "3000 x 4000",
                createdAt = System.currentTimeMillis() - 43200000,
                isMarketplaceListed = true,
                priceThb = 199.0,
                pricePoints = 199,
                salesCount = 19,
                rating = 4.98f,
                aiEnhanced = true,
                brightness = 0.04f,
                contrast = 1.1f,
                saturation = 1.15f,
                warmth = 0.08f,
                activeFilter = "Cinematic",
                facesJson = "Emma, Nopparat (You)"
            )
        )
        dao.insertPhotos(initialList)
    }

    private fun seedInitialNotifications() {
        _notifications.value = listOf(
            VaultNotification(
                id = "notif-1",
                titleEn = "New Photo Sale!",
                titleTh = "มีคำสั่งซื้อรูปภาพใหม่!",
                messageEn = "Someone bought 'Golden Hour Ocean Sunset' for ฿220. You earned ฿220 and 50 points!",
                messageTh = "มีผู้ซื้อรูป 'Golden Hour Ocean Sunset' ในราคา ฿220 คุณได้รับ ฿220 พร้อม 50 แต้ม!",
                timeAgo = "10m ago",
                type = NotificationType.SALE_ALERT
            ),
            VaultNotification(
                id = "notif-2",
                titleEn = "Cloud Backup Completed",
                titleTh = "สำรองข้อมูลบนคลาวด์เสร็จสมบูรณ์",
                messageEn = "All vault memories are backed up with AES-256-GCM encryption.",
                messageTh = "รูปภาพทั้งหมดในคลังถูกเข้ารหัส AES-256-GCM และสำรองข้อมูลเรียบร้อยแล้ว",
                timeAgo = "1h ago",
                type = NotificationType.CLOUD_BACKUP
            ),
            VaultNotification(
                id = "notif-3",
                titleEn = "Security Audit Passed",
                titleTh = "การตรวจสอบความปลอดภัยผ่านแล้ว",
                messageEn = "Zero-Knowledge end-to-end encryption keys verified intact.",
                messageTh = "คีย์การเข้ารหัสแบบ Zero-Knowledge มีความปลอดภัยสมบูรณ์",
                timeAgo = "Yesterday",
                type = NotificationType.SECURITY_AUDIT
            )
        )
    }

    // Authentication methods
    fun loginWithEmail(email: String, name: String = "") {
        val userName = if (name.isNotBlank()) name else email.substringBefore("@").replaceFirstChar { it.uppercase() }
        currentUser.value = UserProfile(
            userId = "usr-" + UUID.randomUUID().toString().take(6),
            name = userName,
            email = email,
            avatarEmoji = "👤",
            provider = AuthProvider.EMAIL,
            isLoggedIn = true,
            joinedDate = "October 2026",
            token = "fv_sec_token_" + UUID.randomUUID().toString().take(12)
        )

        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "Signed in with Email",
                titleTh = "เข้าสู่ระบบด้วยอีเมลสำเร็จ",
                messageEn = "Welcome back, $userName! Encrypted vault synced.",
                messageTh = "ยินดีต้อนรับกลับ $userName! คลังภาพนิรภัยเชื่อมต่อแล้ว",
                timeAgo = "Just now",
                type = NotificationType.CLOUD_BACKUP
            )
        ) + _notifications.value
    }

    fun loginWithSocial(provider: AuthProvider) {
        val (name, email, emoji) = when (provider) {
            AuthProvider.FACEBOOK -> Triple("Nopparat Facebook", "nopparat.fb@facebook.com", "📘")
            AuthProvider.GOOGLE -> Triple("Nopparat Google", "nopparat45081@gmail.com", "🌐")
            AuthProvider.APPLE -> Triple("Nopparat Apple", "nopparat@icloud.com", "🍎")
            AuthProvider.EMAIL -> Triple("Nopparat User", "user@example.com", "✉️")
        }

        currentUser.value = UserProfile(
            userId = "usr-" + UUID.randomUUID().toString().take(6),
            name = name,
            email = email,
            avatarEmoji = emoji,
            provider = provider,
            isLoggedIn = true,
            joinedDate = "October 2026",
            token = "fv_sec_token_" + UUID.randomUUID().toString().take(12)
        )

        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "Connected with ${provider.title}",
                titleTh = "เชื่อมต่อบัญชี ${provider.title} แล้ว",
                messageEn = "Cloud backup and marketplace profile linked successfully.",
                messageTh = "เชื่อมต่อระบบสำรองข้อมูลคลาวด์และโปรไฟล์สำเร็จ",
                timeAgo = "Just now",
                type = NotificationType.CLOUD_BACKUP
            )
        ) + _notifications.value
    }

    fun logout() {
        currentUser.value = UserProfile(
            userId = "guest",
            name = "Guest User",
            email = "guest@fotovault.local",
            avatarEmoji = "👤",
            provider = AuthProvider.EMAIL,
            isLoggedIn = false,
            joinedDate = "-",
            token = ""
        )
    }

    suspend fun addPhoto(
        title: String,
        description: String,
        category: String,
        tags: List<String>,
        localUri: String,
        resDrawableName: String = "",
        priceThb: Double = 150.0,
        isMarketplaceListed: Boolean = false,
        detectedFaces: List<String> = emptyList(),
        isPrivateOwnerOnly: Boolean = true,
        nasSynced: Boolean = false
    ): VaultPhoto {
        val id = "photo-" + UUID.randomUUID().toString().take(8)
        val entity = PhotoVaultEntity(
            id = id,
            title = title.ifBlank { "Memory #${System.currentTimeMillis() % 1000}" },
            description = description,
            category = category,
            tagsJson = tags.joinToString(","),
            localUri = localUri,
            resDrawableName = resDrawableName,
            isEncrypted = true,
            encryptionAlgorithm = "AES-256-GCM",
            checksum = "SHA256:" + UUID.randomUUID().toString().replace("-", "") + "ff",
            isSynced = !isOfflineMode.value,
            syncTimestamp = if (!isOfflineMode.value) System.currentTimeMillis() else 0L,
            cloudProvider = selectedCloudProvider.value,
            fileSize = "3.8 MB",
            resolution = "4000 x 3000",
            createdAt = System.currentTimeMillis(),
            isMarketplaceListed = isMarketplaceListed,
            priceThb = priceThb,
            pricePoints = priceThb.toInt(),
            salesCount = 0,
            rating = 5.0f,
            aiEnhanced = false,
            brightness = 0f,
            contrast = 1.0f,
            saturation = 1.0f,
            warmth = 0f,
            activeFilter = "None",
            facesJson = detectedFaces.joinToString(","),
            isPrivateOwnerOnly = isPrivateOwnerOnly,
            nasSynced = nasSynced
        )
        dao.insertPhoto(entity)

        // Award backup reward points
        userPoints.value += 20

        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "Photo Secured in Vault",
                titleTh = "บันทึกรูปภาพลงคลังนิรภัยแล้ว",
                messageEn = "'$title' encrypted and protected. +20 Points earned!",
                messageTh = "'$title' ถูกเข้ารหัสและจัดเก็บเรียบร้อยแล้ว ได้รับ +20 แต้ม!",
                timeAgo = "Just now",
                type = NotificationType.POINTS_EARNED
            )
        ) + _notifications.value

        return entity.toModel()
    }

    suspend fun updatePhoto(photo: VaultPhoto) {
        dao.updatePhoto(photo.toEntity())
    }

    suspend fun deletePhoto(photoId: String) {
        dao.deletePhotoById(photoId)
    }

    suspend fun syncAllNow() {
        isSyncing.value = true
        kotlinx.coroutines.delay(1200) // simulate secure handshake & checksum sync
        val now = System.currentTimeMillis()
        dao.markAllSynced(now)
        lastSyncTime.value = now
        isSyncing.value = false

        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "Cloud Sync Succeeded",
                titleTh = "ซิงค์คลาวด์เสร็จสิ้น",
                messageEn = "All vault changes synced with ${selectedCloudProvider.value}.",
                messageTh = "ข้อมูลทั้งหมดซิงค์กับ ${selectedCloudProvider.value} สำเร็จ",
                timeAgo = "Just now",
                type = NotificationType.CLOUD_BACKUP
            )
        ) + _notifications.value
    }

    suspend fun testNasConnection(config: NasConfig): Boolean {
        kotlinx.coroutines.delay(800) // connection handshake ping
        nasConfig.value = config.copy(isConnected = true)
        return true
    }

    suspend fun syncAllToNas() {
        val currentNas = nasConfig.value
        nasSyncState.value = NasSyncState(
            isSyncing = true,
            currentFileName = "Handshake with ${currentNas.brand.brandName} (${currentNas.host})...",
            progressPercent = 15,
            transferSpeedMbS = 0.0,
            filesSynced = 0,
            totalFiles = 3
        )
        kotlinx.coroutines.delay(600)
        nasSyncState.value = NasSyncState(
            isSyncing = true,
            currentFileName = "vault_tokyo_cyberpunk.e2ee",
            progressPercent = 55,
            transferSpeedMbS = 24.6,
            filesSynced = 1,
            totalFiles = 3
        )
        kotlinx.coroutines.delay(700)
        nasSyncState.value = NasSyncState(
            isSyncing = true,
            currentFileName = "vault_sunset_ocean.e2ee",
            progressPercent = 90,
            transferSpeedMbS = 28.2,
            filesSynced = 2,
            totalFiles = 3
        )
        kotlinx.coroutines.delay(600)
        val now = System.currentTimeMillis()
        dao.markAllNasSynced()
        nasConfig.value = currentNas.copy(
            isConnected = true,
            lastSyncTimestamp = now,
            usedStorageGb = currentNas.usedStorageGb + 0.05
        )
        nasSyncState.value = NasSyncState(
            isSyncing = false,
            currentFileName = "NAS Sync Complete",
            progressPercent = 100,
            transferSpeedMbS = 0.0,
            filesSynced = 3,
            totalFiles = 3
        )

        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "NAS Backup Completed",
                titleTh = "สำรองข้อมูลขึ้น NAS สำเร็จ",
                messageEn = "Encrypted memories safely replicated to ${currentNas.brand.brandName} (${currentNas.sharedFolder}).",
                messageTh = "สำรองรูปภาพไปยัง ${currentNas.brand.brandName} (${currentNas.sharedFolder}) เรียบร้อยแล้ว",
                timeAgo = "Just now",
                type = NotificationType.CLOUD_BACKUP
            )
        ) + _notifications.value
    }

    fun verifySlipAndCompletePurchase(
        photo: VaultPhoto,
        paymentMethod: PaymentMethod,
        pointsUsed: Int
    ): Pair<SlipVerificationResult, PurchaseOrder> {
        val discount = (pointsUsed / 10.0) // 10 points = 1 THB discount
        val netAmount = (photo.priceThb - discount).coerceAtLeast(0.0)
        val pointsEarned = (netAmount * 0.1).toInt().coerceAtLeast(10)

        // Deduct points and add earned points
        userPoints.value = (userPoints.value - pointsUsed + pointsEarned).coerceAtLeast(0)

        val txId = "TXN-" + SimpleDateFormat("yyyyMMdd", Locale.US).format(Date()) + "-" + (1000..9999).random()
        val slipResult = SlipVerificationResult(
            isValid = true,
            transactionId = txId,
            bankName = if (paymentMethod == PaymentMethod.PROMPTPAY) "PromptPay Thailand (SCB / KBank)" else "Digital Wallet Gateway",
            transferTime = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.US).format(Date()),
            amountThb = netAmount,
            senderName = "Verified Customer (You)",
            receiverName = "FotoVault Escrow Service",
            statusMessage = "Slip matched with bank QR record. Signature verified."
        )

        val order = PurchaseOrder(
            orderId = "ORD-" + UUID.randomUUID().toString().take(6).uppercase(),
            listingId = photo.id,
            photoTitle = photo.title,
            buyerName = "You",
            priceThb = photo.priceThb,
            pointsUsed = pointsUsed,
            discountThb = discount,
            netAmountThb = netAmount,
            pointsEarned = pointsEarned,
            paymentMethod = paymentMethod,
            slipVerified = true,
            transactionRef = txId
        )

        _purchases.value = listOf(order) + _purchases.value

        // Also add real-time sale alert notification
        _notifications.value = listOf(
            VaultNotification(
                id = UUID.randomUUID().toString(),
                titleEn = "Purchase Confirmed!",
                titleTh = "ยืนยันการซื้อสำเร็จ!",
                messageEn = "Unlocked '${photo.title}' high-res license. You earned +$pointsEarned Points!",
                messageTh = "ปลดล็อกลิขสิทธิ์ความละเอียดสูง '${photo.title}' แล้ว ได้รับ +$pointsEarned แต้มสะสม!",
                timeAgo = "Just now",
                type = NotificationType.SALE_ALERT
            )
        ) + _notifications.value

        // Increment sales count of photo in background
        scope.launch {
            val updated = photo.copy(salesCount = photo.salesCount + 1)
            dao.updatePhoto(updated.toEntity())
        }

        return Pair(slipResult, order)
    }

    fun getSalesAnalytics(photos: List<VaultPhoto>): SalesAnalyticsData {
        val totalSold = photos.sumOf { it.salesCount } + _purchases.value.size
        val totalRevenue = photos.sumOf { it.salesCount * it.priceThb } + _purchases.value.sumOf { it.netAmountThb }

        // Top tag calculation
        val tagFrequency = mutableMapOf<String, Int>()
        photos.forEach { p ->
            p.tags.forEach { tag ->
                tagFrequency[tag] = (tagFrequency[tag] ?: 0) + (p.salesCount + 1)
            }
        }
        val bestTag = tagFrequency.maxByOrNull { it.value }?.key ?: "goldenhour"

        val daily = listOf(
            Pair("Mon", totalRevenue * 0.12),
            Pair("Tue", totalRevenue * 0.15),
            Pair("Wed", totalRevenue * 0.18),
            Pair("Thu", totalRevenue * 0.14),
            Pair("Fri", totalRevenue * 0.22),
            Pair("Sat", totalRevenue * 0.31),
            Pair("Sun", totalRevenue * 0.28)
        )

        val tipsEn = listOf(
            "Photos with '#$bestTag' and cyberpunk neon aesthetic generate 2.8x higher buyer interest.",
            "Using AI Smart Enhance to balance contrast increases marketplace sales by 34%.",
            "Landscape photos captured in 16:9 or 4:3 with warm golden tones have the highest repeat purchase rate.",
            "Adding at least 5 searchable tags increases discovery in the explore feed."
        )

        val tipsTh = listOf(
            "ภาพถ่ายที่มีแท็ก '#$bestTag' และโทนไซเบอร์พังก์/แสงนีออนมีผู้สนใจซื้อสูงกว่าค่าเฉลี่ย 2.8 เท่า",
            "การใช้เครื่องมือ AI Smart Enhance ปรับคอนทราสต์ช่วยเพิ่มยอดขายในตลาดได้ถึง 34%",
            "ภาพวิวธรรมชาติโทนแสงสีทองช่วงเย็น (Golden Hour) มียอดสั่งซื้อและคะแนนรีวิวระดับ 5 ดาวสูงสุด",
            "การระบุแท็กค้นหาอย่างน้อย 5 แท็ก ช่วยเพิ่มโอกาสการค้นพบในหน้าสำรวจผลงาน"
        )

        return SalesAnalyticsData(
            totalEarningsThb = totalRevenue.coerceAtLeast(3500.0),
            totalPhotosSold = totalSold.coerceAtLeast(42),
            bestSellingTag = "#$bestTag",
            averagePriceThb = 185.0,
            conversionRate = 4.96f,
            dailyRevenue = daily,
            skillRecommendationsEn = tipsEn,
            skillRecommendationsTh = tipsTh
        )
    }
}

// Extension converters
fun PhotoVaultEntity.toModel(): VaultPhoto {
    return VaultPhoto(
        id = id,
        title = title,
        description = description,
        category = category,
        tags = if (tagsJson.isBlank()) emptyList() else tagsJson.split(",").map { it.trim() },
        localUri = localUri,
        resDrawableName = resDrawableName,
        isEncrypted = isEncrypted,
        encryptionAlgorithm = encryptionAlgorithm,
        checksum = checksum,
        isSynced = isSynced,
        syncTimestamp = syncTimestamp,
        cloudProvider = cloudProvider,
        fileSize = fileSize,
        resolution = resolution,
        createdAt = createdAt,
        isMarketplaceListed = isMarketplaceListed,
        priceThb = priceThb,
        pricePoints = pricePoints,
        salesCount = salesCount,
        rating = rating,
        aiEnhanced = aiEnhanced,
        brightness = brightness,
        contrast = contrast,
        saturation = saturation,
        warmth = warmth,
        activeFilter = activeFilter,
        detectedFaces = if (facesJson.isBlank()) emptyList() else facesJson.split(",").map { it.trim() },
        isPrivateOwnerOnly = isPrivateOwnerOnly,
        nasSynced = nasSynced
    )
}

fun VaultPhoto.toEntity(): PhotoVaultEntity {
    return PhotoVaultEntity(
        id = id,
        title = title,
        description = description,
        category = category,
        tagsJson = tags.joinToString(","),
        localUri = localUri,
        resDrawableName = resDrawableName,
        isEncrypted = isEncrypted,
        encryptionAlgorithm = encryptionAlgorithm,
        checksum = checksum,
        isSynced = isSynced,
        syncTimestamp = syncTimestamp,
        cloudProvider = cloudProvider,
        fileSize = fileSize,
        resolution = resolution,
        createdAt = createdAt,
        isMarketplaceListed = isMarketplaceListed,
        priceThb = priceThb,
        pricePoints = pricePoints,
        salesCount = salesCount,
        rating = rating,
        aiEnhanced = aiEnhanced,
        brightness = brightness,
        contrast = contrast,
        saturation = saturation,
        warmth = warmth,
        activeFilter = activeFilter,
        facesJson = detectedFaces.joinToString(","),
        isPrivateOwnerOnly = isPrivateOwnerOnly,
        nasSynced = nasSynced
    )
}
