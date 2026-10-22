package com.example.data.model

data class NasConfig(
    val id: String = "nas-primary",
    val brand: NasBrand = NasBrand.SYNOLOGY,
    val deviceName: String = "DiskStation DS923+",
    val host: String = "192.168.1.120", // or QuickConnect ID
    val port: Int = 5001,
    val protocol: String = "HTTPS / WebDAV",
    val username: String = "nopparat_admin",
    val password: String = "••••••••",
    val sharedFolder: String = "/volume1/photo/vault_backup/",
    val isConnected: Boolean = true,
    val useHttps: Boolean = true,
    val autoSyncOnWifi: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis() - 14400000,
    val usedStorageGb: Double = 1240.5,
    val totalStorageGb: Double = 8000.0 // 8 TB NAS pool
)

enum class NasBrand(
    val brandName: String,
    val description: String,
    val iconEmoji: String,
    val defaultPort: Int,
    val supportedProtocols: List<String>
) {
    SYNOLOGY(
        brandName = "Synology",
        description = "Synology DSM / QuickConnect / WebDAV / Synology Drive",
        iconEmoji = "🟠",
        defaultPort = 5001,
        supportedProtocols = listOf("QuickConnect", "HTTPS WebDAV", "SMB/CIFS")
    ),
    QNAP(
        brandName = "QNAP",
        description = "QNAP QTS / QuTS hero / myQNAPcloud / Qsync",
        iconEmoji = "🔵",
        defaultPort = 8080,
        supportedProtocols = listOf("myQNAPcloud", "WebDAV", "SMB")
    ),
    TRUENAS(
        brandName = "TrueNAS",
        description = "TrueNAS Core & SCALE / Enterprise ZFS Storage Pools",
        iconEmoji = "🦈",
        defaultPort = 443,
        supportedProtocols = listOf("ZFS Snapshot / SSH", "WebDAV", "SMB", "NFS")
    ),
    ASUSTOR(
        brandName = "ASUSTOR",
        description = "ASUSTOR ADM / Cloud Backup Center / EZ-Connect",
        iconEmoji = "🟢",
        defaultPort = 8001,
        supportedProtocols = listOf("EZ-Connect", "WebDAV", "Rsync")
    ),
    WD_MYCLOUD(
        brandName = "WD My Cloud",
        description = "Western Digital My Cloud Home & OS 5",
        iconEmoji = "🔷",
        defaultPort = 443,
        supportedProtocols = listOf("My Cloud Access", "Local Network SMB")
    ),
    CUSTOM_WEBDAV(
        brandName = "Generic WebDAV / Nextcloud",
        description = "Self-hosted Nextcloud, ownCloud, or custom WebDAV",
        iconEmoji = "🌐",
        defaultPort = 443,
        supportedProtocols = listOf("Nextcloud API", "WebDAV over SSL")
    ),
    SMB_CIFS(
        brandName = "Windows SMB / Samba",
        description = "Local Area Network Samba / CIFS File Share",
        iconEmoji = "💻",
        defaultPort = 445,
        supportedProtocols = listOf("SMB 3.1.1", "Samba Share")
    );

    val defaultProtocol: String get() = supportedProtocols.firstOrNull() ?: "HTTPS"
    val protocolName: String get() = supportedProtocols.joinToString(" • ")
}

data class NasSyncState(
    val isSyncing: Boolean = false,
    val currentFileName: String = "",
    val progressPercent: Int = 0,
    val transferSpeedMbS: Double = 0.0,
    val filesSynced: Int = 0,
    val totalFiles: Int = 0
)
