package com.example.data.model

data class VaultPhoto(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "All", "Travel", "Cyberpunk", "Nature", "Art", "Documents", "Portrait"
    val tags: List<String>,
    val localUri: String = "",
    val resDrawableName: String = "", // e.g. "img_sample_cyberpunk"
    val isEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM",
    val checksum: String = "SHA256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    val isSynced: Boolean = true,
    val syncTimestamp: Long = System.currentTimeMillis(),
    val cloudProvider: String = "FotoVault Cloud (E2EE)",
    val fileSize: String = "4.2 MB",
    val resolution: String = "4032 x 3024",
    val createdAt: Long = System.currentTimeMillis(),
    val isMarketplaceListed: Boolean = false,
    val priceThb: Double = 150.0,
    val pricePoints: Int = 150,
    val salesCount: Int = 0,
    val rating: Float = 4.9f,
    // AI Photo enhancement parameters
    val aiEnhanced: Boolean = false,
    val brightness: Float = 0f, // -1.0 to 1.0
    val contrast: Float = 1.0f,   // 0.5 to 2.0
    val saturation: Float = 1.0f, // 0.0 to 2.0
    val warmth: Float = 0f,       // -1.0 to 1.0
    val activeFilter: String = "None", // "None", "Cyberpunk", "Cinematic", "Sunset", "Emerald", "Noir"
    val detectedFaces: List<String> = emptyList(), // e.g. ["Nopparat (You)", "Alex", "Emma"]
    val faceMatchConfidence: Float = 0f, // 0.0 to 1.0 (used in face search filtering)
    val isPrivateOwnerOnly: Boolean = true, // Private to the app owner only
    val nasSynced: Boolean = true, // Synced to connected NAS
    val nasBackupPath: String = "/volume1/photo/vault_backup/"
)

enum class PhotoCategory(val id: String, val labelEn: String, val labelTh: String) {
    ALL("all", "All Photos", "ทั้งหมด"),
    OWNER_PRIVATE("private", "Owner Private", "ส่วนตัวเฉพาะเจ้าของ"),
    PEOPLE("people", "People & Faces", "บุคคลและใบหน้า"),
    TRAVEL("travel", "Travel", "ท่องเที่ยว"),
    CYBERPUNK("cyberpunk", "Cyberpunk / City", "ไซเบอร์พังก์/เมือง"),
    NATURE("nature", "Nature / Sunset", "ธรรมชาติ/วิว"),
    ART("art", "Fine Art", "งานศิลป์"),
    DOCUMENTS("documents", "Private Docs", "เอกสารสำคัญ"),
    PORTRAIT("portrait", "Portrait", "ภาพบุคคล")
}

enum class UserRole(val id: String, val labelEn: String, val labelTh: String, val iconEmoji: String) {
    ADMIN("admin", "Administrator / Owner", "ผู้ดูแลระบบ (Admin)", "👑"),
    BUYER("buyer", "Photo Buyer / Visitor", "ผู้เข้าชมซื้อรูป (Buyer)", "🛍️")
}

data class UserProfile(
    val userId: String = "user-101",
    val name: String = "Nopparat",
    val email: String = "nopparat45081@gmail.com",
    val avatarEmoji: String = "👑",
    val provider: AuthProvider = AuthProvider.GOOGLE,
    val isLoggedIn: Boolean = true,
    val joinedDate: String = "Oct 2026",
    val token: String = "e2ee_token_fv_98a76f",
    val role: UserRole = UserRole.ADMIN,
    val buyerBalanceThb: Double = 500.0,
    val buyerPoints: Int = 350
)

enum class AuthProvider(val title: String, val icon: String, val brandColorHex: Long) {
    EMAIL("Email / Password", "✉️", 0xFF00E5FF),
    FACEBOOK("Facebook", "📘", 0xFF1877F2),
    GOOGLE("Google", "🌐", 0xFFEA4335),
    APPLE("Apple ID", "🍎", 0xFFFFFFFF)
}

data class FaceScanResult(
    val scannedPhotoTitle: String,
    val detectedPeople: List<String>,
    val landmarksDetected: Int, // e.g. 68 landmarks (eyes, eyebrows, nose, mouth, jaw)
    val matchingPhotos: List<Pair<VaultPhoto, Float>> // photo to confidence 0.0 - 1.0
)

