package com.example.data.model

data class MarketplaceListing(
    val id: String,
    val photo: VaultPhoto,
    val creatorName: String,
    val creatorAvatar: String = "👤",
    val title: String,
    val description: String,
    val tags: List<String>,
    val priceThb: Double,
    val pricePoints: Int,
    val totalPurchases: Int,
    val rating: Float,
    val isVerifiedCreator: Boolean = true,
    val licenseType: String = "Commercial Use"
)

data class PurchaseOrder(
    val orderId: String,
    val listingId: String,
    val photoTitle: String,
    val buyerName: String,
    val priceThb: Double,
    val pointsUsed: Int,
    val discountThb: Double,
    val netAmountThb: Double,
    val pointsEarned: Int,
    val paymentMethod: PaymentMethod,
    val slipVerified: Boolean,
    val transactionRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PaymentMethod(val labelEn: String, val labelTh: String, val icon: String) {
    PROMPTPAY("PromptPay QR", "พร้อมเพย์ QR", "📱"),
    CREDIT_CARD("Credit / Debit Card", "บัตรเครดิต/เดบิต", "💳"),
    TRUEMONEY("TrueMoney Wallet", "ทรูมันนี่ วอลเล็ท", "👛"),
    POINTS("Vault Points", "แต้มสะสม", "⭐")
}

data class SlipVerificationResult(
    val isValid: Boolean,
    val transactionId: String,
    val bankName: String,
    val transferTime: String,
    val amountThb: Double,
    val senderName: String,
    val receiverName: String,
    val statusMessage: String
)

data class SalesAnalyticsData(
    val totalEarningsThb: Double,
    val totalPhotosSold: Int,
    val bestSellingTag: String,
    val averagePriceThb: Double,
    val conversionRate: Float,
    val dailyRevenue: List<Pair<String, Double>>,
    val skillRecommendationsEn: List<String>,
    val skillRecommendationsTh: List<String>
)

data class VaultNotification(
    val id: String,
    val titleEn: String,
    val titleTh: String,
    val messageEn: String,
    val messageTh: String,
    val timeAgo: String,
    val type: NotificationType,
    val read: Boolean = false
)

enum class NotificationType {
    SALE_ALERT,
    CLOUD_BACKUP,
    POINTS_EARNED,
    SECURITY_AUDIT
}
