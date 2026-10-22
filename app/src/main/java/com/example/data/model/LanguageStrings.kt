package com.example.data.model

object LanguageStrings {
    fun get(key: String, isThai: Boolean): String {
        val entry = strings[key] ?: return key
        return if (isThai) entry.second else entry.first
    }

    private val strings = mapOf(
        // Tabs
        "tab_vault" to Pair("Vault", "คลังรูป"),
        "tab_market" to Pair("Marketplace", "ตลาดซื้อขาย"),
        "tab_analytics" to Pair("Analytics", "วิเคราะห์ยอด"),
        "tab_cloud" to Pair("Cloud Sync", "ซิงค์คลาวด์"),
        "tab_settings" to Pair("Settings", "ตั้งค่า"),

        // Vault Screen
        "vault_title" to Pair("Secure Photo Vault", "คลังภาพนิรภัย"),
        "search_hint" to Pair("Search by tags (#sunset), title...", "ค้นหาจากแท็ก (#วิว), ชื่อ..."),
        "add_photo" to Pair("Add Photo", "เพิ่มรูปภาพ"),
        "filter_category" to Pair("Category", "หมวดหมู่"),
        "photos_count" to Pair("Photos", "รูปภาพ"),
        "encrypted_status" to Pair("AES-256-GCM Encrypted", "เข้ารหัสระดับ AES-256-GCM"),
        "all_synced" to Pair("Synced with Cloud", "ซิงค์กับคลาวด์แล้ว"),
        "offline_mode" to Pair("Offline Mode", "โหมดออฟไลน์"),
        "empty_vault_title" to Pair("No Photos Found", "ไม่พบรูปภาพ"),
        "empty_vault_desc" to Pair("Tap + to import photos securely into your vault.", "แตะปุ่ม + เพื่อบันทึกรูปภาพอย่างปลอดภัย"),

        // Detail Screen
        "photo_details" to Pair("Photo Details", "รายละเอียดรูปภาพ"),
        "ai_editor" to Pair("AI Enhancer", "แต่งภาพ AI"),
        "list_on_market" to Pair("Sell on Market", "ขายในตลาด"),
        "share_social" to Pair("Share", "แชร์โซเชียล"),
        "delete_photo" to Pair("Delete", "ลบรูป"),
        "tags_label" to Pair("Tags", "แท็กค้นหา"),
        "category_label" to Pair("Category", "หมวดหมู่"),
        "resolution_label" to Pair("Resolution", "ความละเอียด"),
        "size_label" to Pair("File Size", "ขนาดไฟล์"),
        "created_label" to Pair("Date Created", "วันที่บันทึก"),
        "security_badge" to Pair("Zero-Knowledge Encrypted", "ความปลอดภัยระดับ Zero-Knowledge"),

        // AI Editor
        "ai_editor_title" to Pair("AI Smart Photo Studio", "สตูดิโอแต่งภาพ AI อัจฉริยะ"),
        "auto_enhance" to Pair("Auto AI Balance", "ปรับแต่งแสงและสีอัตโนมัติ"),
        "brightness" to Pair("Brightness", "ความสว่าง"),
        "contrast" to Pair("Contrast", "คอนทราสต์"),
        "saturation" to Pair("Saturation", "ความอิ่มสี"),
        "warmth" to Pair("Warmth", "โทนอุ่น"),
        "preset_filters" to Pair("Artistic Presets", "พรีเซ็ตโทนภาพ"),
        "save_changes" to Pair("Save to Vault", "บันทึกลงคลัง"),
        "reset_filters" to Pair("Reset", "รีเซ็ต"),
        "compare" to Pair("Hold to Compare", "กดค้างเพื่อดูภาพเดิม"),

        // Marketplace
        "market_title" to Pair("Creator Marketplace", "ตลาดรูปภาพสร้างสรรค์"),
        "market_subtitle" to Pair("Discover, buy, and sell verified enhanced photos", "ค้นพบและซื้อขายภาพถ่ายลิขสิทธิ์แท้"),
        "buy_now" to Pair("Buy Photo", "สั่งซื้อรูปภาพ"),
        "price" to Pair("Price", "ราคา"),
        "payment_method" to Pair("Payment Method", "ช่องทางชำระเงิน"),
        "scan_slip" to Pair("Scan & Verify Slip", "สแกนตรวจสลิปอัตโนมัติ"),
        "slip_verified" to Pair("Slip Verified Instantly", "ตรวจสอบสลิปถูกต้องแล้ว"),
        "use_points" to Pair("Use Points Discount", "ใช้แต้มสะสมลดราคา"),
        "points_earned_badge" to Pair("Earn Points on this purchase", "รับแต้มสะสมทันทีเมื่อซื้อ"),
        "confirm_purchase" to Pair("Confirm & Pay", "ยืนยันและชำระเงิน"),
        "purchase_success" to Pair("Purchase Successful!", "สั่งซื้อสำเร็จ!"),

        // Analytics
        "analytics_title" to Pair("Sales & Skill Analytics", "วิเคราะห์ยอดขายและทักษะ"),
        "total_earnings" to Pair("Total Revenue", "รายได้รวม"),
        "photos_sold" to Pair("Photos Sold", "ขายแล้ว"),
        "best_tag" to Pair("Top Tag", "แท็กยอดนิยม"),
        "conversion_rate" to Pair("Buyer Rating", "คะแนนความพึงพอใจ"),
        "ai_growth_tips" to Pair("AI Skill Recommendations", "คำแนะนำพัฒนาทักษะจาก AI"),
        "weekly_trends" to Pair("Weekly Sales Revenue", "แนวโน้มยอดขายรายสัปดาห์"),

        // Cloud & Security
        "cloud_title" to Pair("Cloud Backup & Sync", "สำรองข้อมูลและซิงค์คลาวด์"),
        "daily_auto_backup" to Pair("Daily Auto-Backup", "สำรองข้อมูลอัตโนมัติทุกวัน"),
        "backup_provider" to Pair("Storage Provider", "ผู้ให้บริการจัดเก็บข้อมูล"),
        "backup_now" to Pair("Backup to Cloud Now", "สำรองข้อมูลขึ้นคลาวด์ทันที"),
        "restore_cloud" to Pair("Restore from Cloud", "กู้คืนข้อมูลจากคลาวด์"),
        "security_pin" to Pair("Vault PIN Protection", "ระบบล็อกรหัสผ่าน PIN"),
        "enter_pin" to Pair("Enter 4-Digit Vault PIN", "กรอกรหัส PIN 4 หลัก"),
        "unlock" to Pair("Unlock", "ปลดล็อก"),

        // Settings
        "settings_title" to Pair("Settings & Preferences", "ตั้งค่าและปรับแต่ง"),
        "dark_mode" to Pair("Dark Mode", "โหมดมืด"),
        "oled_mode" to Pair("OLED Pure Black", "โหมดดำสนิท OLED"),
        "power_save" to Pair("Power Saving Mode", "โหมดประหยัดพลังงาน"),
        "language" to Pair("Language / ภาษา", "ภาษา / Language"),
        "points_balance" to Pair("Points Wallet", "กระเป๋าแต้มสะสม"),
        "notifications" to Pair("Smart Notifications", "การแจ้งเตือนอัจฉริยะ"),
        "privacy_policy" to Pair("End-to-End Encryption Guarantee", "รับประกันความปลอดภัยข้อมูล E2EE"),

        // Face Search & Recognition
        "face_search_title" to Pair("Smart Face Search", "ระบบค้นหาด้วยใบหน้าอัจฉริยะ"),
        "face_search_btn" to Pair("Face Search", "ค้นหาใบหน้า"),
        "upload_face" to Pair("Upload Face Photo", "อัปโหลดรูปใบหน้า"),
        "scan_face_cam" to Pair("Scan Face (Camera)", "สแกนใบหน้าด้วยกล้อง"),
        "face_scanning" to Pair("Analyzing facial landmarks & vector embeddings...", "กำลังสแกนวิเคราะห์จุดเด่นบนใบหน้า..."),
        "face_matches_found" to Pair("Matching Photos Found", "พบรูปภาพที่ตรงกับใบหน้านี้"),
        "no_face_matches" to Pair("No matching faces found in vault", "ไม่พบภาพใบหน้าที่ตรงกันในคลัง"),
        "match_confidence" to Pair("Match Confidence", "ความแม่นยำ"),
        "people_category" to Pair("People & Faces", "บุคคลและใบหน้า"),
        "filter_by_face" to Pair("Filter by Person", "กรองตามบุคคล"),

        // User Account & Multi-provider Login
        "auth_title" to Pair("FotoVault Cloud Account", "บัญชีผู้ใช้ FotoVault"),
        "auth_subtitle" to Pair("Sign in to sync your encrypted vault & marketplace earnings across all devices", "เข้าสู่ระบบเพื่อซิงค์คลังนิรภัยและรายได้ข้ามอุปกรณ์"),
        "login_email" to Pair("Sign In with Email", "เข้าสู่ระบบด้วยอีเมล"),
        "signup_email" to Pair("Create Free Account", "สร้างบัญชีผู้ใช้ใหม่"),
        "login_fb" to Pair("Continue with Facebook", "เข้าสู่ระบบด้วย Facebook"),
        "login_google" to Pair("Continue with Google", "เข้าสู่ระบบด้วย Google"),
        "login_apple" to Pair("Continue with Apple", "เข้าสู่ระบบด้วย Apple"),
        "or_social" to Pair("Or connect with social account", "หรือเข้าสู่ระบบผ่านโซเชียล"),
        "email_label" to Pair("Email Address", "อีเมล"),
        "password_label" to Pair("Password", "รหัสผ่าน"),
        "logged_in_as" to Pair("Signed in as", "เข้าสู่ระบบแล้ว"),
        "logout" to Pair("Sign Out", "ออกจากระบบ"),
        "cloud_account_active" to Pair("Cloud Sync Active", "เชื่อมต่อระบบคลาวด์แล้ว"),

        // Owner Private Vault & Universal NAS Sync
        "owner_private_vault" to Pair("Owner Private Vault", "คลังส่วนตัวเฉพาะเจ้าของ"),
        "owner_private_desc" to Pair("Zero-Knowledge private partition. Visible and decryptable only by the app owner.", "คลังข้อมูลส่วนบุคคล เข้ารหัสลับ มีเพียงเจ้าของเท่านั้นที่เข้าถึงและถอดรหัสได้"),
        "nas_sync_title" to Pair("Universal NAS Storage Sync", "ระบบซิงค์อุปกรณ์ NAS ทุกค่าย"),
        "nas_sync_desc" to Pair("Seamlessly sync and backup photos to Synology, QNAP, TrueNAS, ASUSTOR, WD My Cloud, or custom WebDAV/SMB.", "สำรองรูปภาพอัตโนมัติไปยัง NAS ประจำบ้านหรือสำนักงาน รองรับ Synology, QNAP, TrueNAS, ASUSTOR, WD และโปรโตคอลมาตรฐาน"),
        "connect_nas" to Pair("Connect NAS Device", "ตั้งค่าเชื่อมต่อ NAS"),
        "nas_brand" to Pair("NAS Brand / System", "แบรนด์อุปกรณ์ NAS"),
        "nas_host" to Pair("Host IP / QuickConnect ID", "IP Address หรือ QuickConnect ID"),
        "nas_port" to Pair("Port", "พอร์ต"),
        "nas_protocol" to Pair("Protocol", "โปรโตคอล"),
        "nas_username" to Pair("Username", "ชื่อผู้ใช้ NAS"),
        "nas_password" to Pair("Password / Token", "รหัสผ่าน / API Token"),
        "nas_folder" to Pair("Shared Folder Path", "โฟลเดอร์จัดเก็บใน NAS"),
        "nas_test_connection" to Pair("Test Connection", "ทดสอบการเชื่อมต่อ"),
        "nas_connected" to Pair("NAS Connected & Healthy", "เชื่อมต่อ NAS สมบูรณ์แล้ว"),
        "nas_sync_now" to Pair("Sync to NAS Now", "ซิงค์ขึ้น NAS ทันที"),
        "nas_wifi_only" to Pair("Auto-Sync on Home Wi-Fi Only", "ซิงค์อัตโนมัติเฉพาะเมื่อต่อ Wi-Fi"),
        "nas_storage_stat" to Pair("NAS Storage Capacity", "ความจุพื้นที่จัดเก็บบน NAS"),
        "nas_last_synced" to Pair("Last NAS Backup", "สำรองข้อมูลบน NAS ล่าสุด"),

        // Roles & Permissions Separation
        "role_admin" to Pair("Administrator / Owner", "ผู้ดูแลระบบ (Admin/Owner)"),
        "role_buyer" to Pair("Photo Buyer / Visitor", "ผู้เข้าชมซื้อรูป (Buyer/Visitor)"),
        "role_admin_badge" to Pair("Admin 👑", "ผู้ดูแลระบบ 👑"),
        "role_buyer_badge" to Pair("Buyer 🛍️", "ผู้ซื้อรูป 🛍️"),
        "role_current" to Pair("Current Active Role", "บทบาทการใช้งานปัจจุบัน"),
        "role_switch_title" to Pair("Switch User Role", "สลับโหมด / บทบาทผู้ใช้"),
        "role_admin_desc" to Pair("Full access: private encrypted vault, Universal NAS sync, AI photo editing, and sales earnings analytics.", "สิทธิ์เต็ม: เข้าถึงคลังรูปส่วนตัว จัดการอุปกรณ์ NAS ตกแต่งภาพด้วย AI และดูสถิติรายได้ยอดขายทั้งหมด"),
        "role_buyer_desc" to Pair("Buyer mode: browse public gallery, preview licensed photos, purchase with PromptPay/Points, and download high-res files.", "โหมดผู้ซื้อ: เลือกชมภาพถ่ายในตลาด สั่งซื้อผ่าน PromptPay/แต้มสะสม ปลดล็อกใบรับรองลิขสิทธิ์ และดาวน์โหลดภาพต้นฉบับ"),
        "role_switch_to_admin" to Pair("Switch to Admin 👑", "สลับเป็น ผู้ดูแลระบบ 👑"),
        "role_switch_to_buyer" to Pair("Switch to Buyer 🛍️", "สลับเป็น ผู้เข้าชมซื้อรูป 🛍️"),

        // Buyer Specific Tabs & Features
        "tab_my_purchases" to Pair("My Purchases", "รูปที่ซื้อแล้ว"),
        "tab_buyer_wallet" to Pair("Buyer Wallet", "กระเป๋าเงินผู้ซื้อ"),
        "buyer_no_purchases" to Pair("No Purchased Photos Yet", "ยังไม่มีรูปภาพที่สั่งซื้อ"),
        "buyer_no_purchases_desc" to Pair("Explore our creator marketplace to purchase high-res licenses with PromptPay or Points.", "เลือกดูตลาดภาพถ่ายเพื่อสั่งซื้อรูปภาพความละเอียดสูง พร้อมรับใบอนุญาตใช้งานแท้"),
        "download_highres" to Pair("Download High-Res Original", "ดาวน์โหลดภาพต้นฉบับคมชัดสูง"),
        "downloading" to Pair("Downloading encrypted raw...", "กำลังดาวน์โหลดไฟล์ต้นฉบับ..."),
        "download_success" to Pair("Saved to Gallery / Storage", "บันทึกไฟล์ภาพลงเครื่องแล้ว"),
        "license_commercial" to Pair("Commercial License Verified", "ใบอนุญาตใช้งานเชิงพาณิชย์สมบูรณ์"),
        "license_cert_id" to Pair("License Certificate", "รหัสใบรับรองลิขสิทธิ์"),
        "wallet_balance" to Pair("Wallet Balance", "ยอดเงินคงเหลือ"),
        "wallet_topup" to Pair("Top Up Balance", "เติมเงินเข้ากระเป๋า"),
        "topup_success" to Pair("Top Up Succeeded!", "เติมเงินสำเร็จแล้ว!"),
        "recent_transactions" to Pair("Transaction History", "ประวัติการสั่งซื้อ"),
        "admin_access_restricted" to Pair("Restricted to Administrator Only", "ส่วนนี้จำกัดสิทธิ์เฉพาะผู้ดูแลระบบเท่านั้น"),
        "watermark_preview" to Pair("Preview • FotoVault Protected", "ภาพตัวอย่าง • มีลายน้ำป้องกัน")
    )
}
