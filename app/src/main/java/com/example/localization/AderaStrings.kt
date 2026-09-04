package com.example.localization

object AderaStrings {
    fun get(lang: AderaLanguage): LocalizedText {
        return when (lang) {
            AderaLanguage.ENGLISH -> EnglishText
            AderaLanguage.AMHARIC -> AmharicText
        }
    }
}

interface LocalizedText {
    val appName: String
    val appTagline: String
    val brandMeaning: String

    // Navigation
    val navHome: String
    val navVault: String
    val navGenerator: String
    val navSecurity: String
    val navSettings: String

    // Onboarding
    val onboard1Title: String
    val onboard1Desc: String
    val onboard2Title: String
    val onboard2Desc: String
    val onboard3Title: String
    val onboard3Desc: String
    val onboard4Title: String
    val onboard4Desc: String
    val getStarted: String
    val alreadyHaveAccount: String

    // Vault Setup
    val createVaultTitle: String
    val createMasterPassword: String
    val masterPasswordReq: String
    val confirmMasterPassword: String
    val zeroKnowledgeWarning: String
    val emergencyCodeNotice: String
    val emergencyRecoveryCode: String
    val saveEmergencyCode: String
    val createVaultButton: String
    val passwordsMatchError: String
    val passwordTooWeakError: String

    // Lock & Auth
    val unlockVault: String
    val enterMasterPassword: String
    val useBiometrics: String
    val biometricPromptTitle: String
    val incorrectPassword: String
    val lockNow: String
    val vaultLockedNotification: String

    // Home Dashboard
    val helloUser: String
    val welcomeBack: String
    val searchVaultPlaceholder: String
    val categoriesHeader: String
    val seeAll: String
    val recentlyUsedHeader: String
    val showAll: String
    val noRecentItems: String
    val quickCopyPassword: String
    val passwordCopiedToast: String
    val clipboardClearedToast: String

    // Categories
    val catSocial: String
    val catWork: String
    val catApps: String
    val catFinance: String
    val catShopping: String
    val catEmail: String
    val catEducation: String
    val catCrypto: String
    val catOther: String

    // Vault Items & Item Types
    val typeLogin: String
    val typeCard: String
    val typeIdentity: String
    val typeNote: String
    val typeWifi: String
    val typeApiKey: String
    val typeLicense: String
    val typeCryptoWallet: String
    val typeOther: String

    // Add / Edit Entry
    val addLoginTitle: String
    val editEntryTitle: String
    val titleLabel: String
    val usernameLabel: String
    val emailLabel: String
    val passwordLabel: String
    val websiteLabel: String
    val categoryLabel: String
    val notesLabel: String
    val tagsLabel: String
    val favoriteLabel: String
    val saveButton: String
    val cancelButton: String
    val deleteButton: String
    val confirmDeleteTitle: String
    val confirmDeleteMessage: String

    // Password Generator
    val generatorTitle: String
    val passwordLength: String
    val optionUppercase: String
    val optionLowercase: String
    val optionNumbers: String
    val optionSymbols: String
    val optionNoAmbiguous: String
    val generateButton: String
    val copyToClipboard: String
    val useThisPassword: String

    // Password Health / Security Analysis
    val securityDashboardTitle: String
    val overallSecurityScore: String
    val safePasswordsCount: String
    val weakPasswordsCount: String
    val reusedPasswordsCount: String
    val compromisedPasswordsCount: String
    val oldPasswordsCount: String
    val securityNeedsAttention: String
    val fixWeakPasswordsAction: String
    val localAnalysisNotice: String
    val healthScoreExcellent: String
    val healthScoreGood: String
    val healthScoreNeedsAttention: String
    val safePasswords: String
    val weakPasswords: String
    val reusedPasswords: String
    val compromisedPasswords: String
    val oldPasswords: String
    val privacyMode: String
    val languageSetting: String
    val lockVault: String

    // Empty States
    val emptyVaultTitle: String
    val emptyVaultDesc: String
    val emptyCategoryDesc: String
    val emptySearchTitle: String
    val emptySearchDesc: String

    // Settings
    val settingsTitle: String
    val sectionAccount: String
    val sectionSecurity: String
    val sectionAppearance: String
    val sectionLanguage: String
    val sectionPrivacy: String
    val sectionBackup: String
    val sectionAbout: String

    val changeMasterPassword: String
    val enableBiometrics: String
    val autoLockDuration: String
    val clipboardTimeout: String
    val privacyModeLabel: String
    val privacyModeDesc: String
    val themeLight: String
    val themeDark: String
    val themeSystem: String

    val exportBackup: String
    val importBackup: String
    val backupWarningMessage: String
    val backupExportSuccess: String
    val backupImportSuccess: String
    val backupImportError: String

    val aboutBuiltWithPrivacy: String
    val appVersion: String
}

private object EnglishText : LocalizedText {
    override val appName = "ADERA"
    override val appTagline = "Your Passwords. Your Privacy. Your Adera."
    override val brandMeaning = "Trust, responsibility, and protecting what matters."

    override val navHome = "Home"
    override val navVault = "Vault"
    override val navGenerator = "Generator"
    override val navSecurity = "Security"
    override val navSettings = "Settings"

    override val onboard1Title = "Protect Your Digital Life"
    override val onboard1Desc = "Keep your accounts safe with end-to-end zero-knowledge encryption."
    override val onboard2Title = "Store Passwords Securely"
    override val onboard2Desc = "Organize logins, cards, and secure notes in one place."
    override val onboard3Title = "Generate Strong Passwords"
    override val onboard3Desc = "Create impenetrable, cryptographically strong passcodes instantly."
    override val onboard4Title = "Your Vault. Your Control."
    override val onboard4Desc = "Your master password is known only to you. Absolute privacy guaranteed."
    override val getStarted = "Get Started"
    override val alreadyHaveAccount = "Already Have Account"

    override val createVaultTitle = "Create Your Master Vault"
    override val createMasterPassword = "Master Password"
    override val masterPasswordReq = "Minimum 8 chars with uppercase, lowercase, number & symbol"
    override val confirmMasterPassword = "Confirm Master Password"
    override val zeroKnowledgeWarning = "If you lose your master password, Adera cannot see or recover it."
    override val emergencyCodeNotice = "Keep your emergency recovery phrase in a safe offline location."
    override val emergencyRecoveryCode = "Emergency Recovery Key"
    override val saveEmergencyCode = "I Have Saved My Recovery Key"
    override val createVaultButton = "Create Vault"
    override val passwordsMatchError = "Passwords do not match."
    override val passwordTooWeakError = "Password does not meet minimum strength requirements."

    override val unlockVault = "Unlock Your Vault"
    override val enterMasterPassword = "Enter Master Password"
    override val useBiometrics = "Unlock with Biometrics"
    override val biometricPromptTitle = "Biometric Verification"
    override val incorrectPassword = "Incorrect Master Password. Please try again."
    override val lockNow = "Lock Vault"
    override val vaultLockedNotification = "Your vault has been locked."

    override val helloUser = "Hello"
    override val welcomeBack = "Welcome back"
    override val searchVaultPlaceholder = "Search your vault..."
    override val categoriesHeader = "Categories"
    override val seeAll = "See All"
    override val recentlyUsedHeader = "Recently Used"
    override val showAll = "Show All"
    override val noRecentItems = "No recently used vault entries yet."
    override val quickCopyPassword = "Copy Password"
    override val passwordCopiedToast = "Password copied! Clipboard clears in 30s."
    override val clipboardClearedToast = "Clipboard automatically cleared."

    override val catSocial = "Social"
    override val catWork = "Work"
    override val catApps = "Apps"
    override val catFinance = "Finance"
    override val catShopping = "Shopping"
    override val catEmail = "Email"
    override val catEducation = "Education"
    override val catCrypto = "Crypto"
    override val catOther = "Other"

    override val typeLogin = "Login"
    override val typeCard = "Credit Card"
    override val typeIdentity = "Identity"
    override val typeNote = "Secure Note"
    override val typeWifi = "Wi-Fi"
    override val typeApiKey = "API Key"
    override val typeLicense = "License"
    override val typeCryptoWallet = "Crypto Wallet"
    override val typeOther = "Other"

    override val addLoginTitle = "Add Vault Entry"
    override val editEntryTitle = "Edit Entry"
    override val titleLabel = "Title / Service Name"
    override val usernameLabel = "Username"
    override val emailLabel = "Email Address"
    override val passwordLabel = "Password"
    override val websiteLabel = "Website / App URL"
    override val categoryLabel = "Category"
    override val notesLabel = "Notes"
    override val tagsLabel = "Tags (comma separated)"
    override val favoriteLabel = "Add to Favorites"
    override val saveButton = "Save Entry"
    override val cancelButton = "Cancel"
    override val deleteButton = "Delete Entry"
    override val confirmDeleteTitle = "Delete Vault Entry?"
    override val confirmDeleteMessage = "Are you sure you want to permanently remove this entry?"

    override val generatorTitle = "Password Generator"
    override val passwordLength = "Password Length"
    override val optionUppercase = "Uppercase Letters (A-Z)"
    override val optionLowercase = "Lowercase Letters (a-z)"
    override val optionNumbers = "Numbers (0-9)"
    override val optionSymbols = "Symbols (!@#$%^&*)"
    override val optionNoAmbiguous = "Exclude Ambiguous (O,0,I,1,l)"
    override val generateButton = "Regenerate Password"
    override val copyToClipboard = "Copy to Clipboard"
    override val useThisPassword = "Use This Password"

    override val securityDashboardTitle = "Password Health"
    override val overallSecurityScore = "Security Score"
    override val safePasswordsCount = "Safe Passwords"
    override val weakPasswordsCount = "Weak Passwords"
    override val reusedPasswordsCount = "Reused Passwords"
    override val compromisedPasswordsCount = "Risky Passwords"
    override val oldPasswordsCount = "Old Passwords"
    override val securityNeedsAttention = "Your password security score needs attention."
    override val fixWeakPasswordsAction = "Review & Fix Passwords"
    override val localAnalysisNotice = "All security analysis is calculated locally on your device."
    override val healthScoreExcellent = "Excellent Vault Security"
    override val healthScoreGood = "Good Vault Security"
    override val healthScoreNeedsAttention = "Security Needs Attention"
    override val safePasswords = "Safe Passwords"
    override val weakPasswords = "Weak Passwords"
    override val reusedPasswords = "Reused Passwords"
    override val compromisedPasswords = "Risky / Compromised"
    override val oldPasswords = "Old / Unchanged"
    override val privacyMode = "Privacy Protection Mode"
    override val languageSetting = "Display Language"
    override val lockVault = "Lock Vault"

    override val emptyVaultTitle = "No Passwords Yet"
    override val emptyVaultDesc = "Add your first login to start protecting your digital accounts."
    override val emptyCategoryDesc = "No items stored in this category yet."
    override val emptySearchTitle = "No Matching Entries"
    override val emptySearchDesc = "Try searching with a different keyword or category."

    override val settingsTitle = "Settings"
    override val sectionAccount = "Account & Vault"
    override val sectionSecurity = "Security Settings"
    override val sectionAppearance = "Appearance"
    override val sectionLanguage = "Language / ቋንቋ"
    override val sectionPrivacy = "Privacy & Screen Guard"
    override val sectionBackup = "Encrypted Backup & Restore"
    override val sectionAbout = "About ADERA"

    override val changeMasterPassword = "Change Master Password"
    override val enableBiometrics = "Biometric Unlock"
    override val autoLockDuration = "Auto-Lock Timer"
    override val clipboardTimeout = "Clipboard Timeout"
    override val privacyModeLabel = "Privacy Protection"
    override val privacyModeDesc = "Hide sensitive password previews in recent apps switcher."
    override val themeLight = "Light Theme"
    override val themeDark = "Dark Theme"
    override val themeSystem = "System Default"

    override val exportBackup = "Export Encrypted Backup"
    override val importBackup = "Import Encrypted Backup"
    override val backupWarningMessage = "Keep your encrypted backup file somewhere safe. It contains your encrypted vault payload."
    override val backupExportSuccess = "Encrypted backup file saved successfully."
    override val backupImportSuccess = "Vault entries imported successfully!"
    override val backupImportError = "Failed to import backup. Check password or file integrity."

    override val aboutBuiltWithPrivacy = "Built with privacy and security in mind."
    override val appVersion = "ADERA v1.0.0 (Production Build)"
}

private object AmharicText : LocalizedText {
    override val appName = "አደራ"
    override val appTagline = "የይለፍ ቃልዎ። የግላዊነትዎ። አደራዎ።"
    override val brandMeaning = "እምነት፣ ኃላፊነት እና ውድ ነገርን በደህንነት መጠበቅ።"

    override val navHome = "መነሻ"
    override val navVault = "ቮልት"
    override val navGenerator = "አመንጪ"
    override val navSecurity = "ደህንነት"
    override val navSettings = "ቅንብሮች"

    override val onboard1Title = "የዲጂታል ሕይወትዎን ይጠብቁ"
    override val onboard1Desc = "መለያዎችዎን በከፍተኛ የኢንክሪፕሽን ደረጃ እና በሙሉ ደህንነት ያስቀምጡ።"
    override val onboard2Title = "የይለፍ ቃሎችዎን በደህንነት ያስቀምጡ"
    override val onboard2Desc = "መግቢያዎችን፣ ካርዶችን እና ሚስጥራዊ ማስታወሻዎችን በአንድ ቦታ ያደራጁ።"
    override val onboard3Title = "ጠንካራ የይለፍ ቃሎችን ይፍጠሩ"
    override val onboard3Desc = "ሊገመቱ የማይችሉ እና አስተማማኝ የይለፍ ቃሎችን በቅጽበት ያመንጩ።"
    override val onboard4Title = "የእርስዎ ቮልት። የእርስዎ ቁጥጥር።"
    override val onboard4Desc = "ዋና የይለፍ ቃልዎን እርስዎ ብቻ ያውቃሉ። ሙሉ ግላዊነት የተረጋገጠ ነው።"
    override val getStarted = "ጀምር"
    override val alreadyHaveAccount = "አካውንት አለኝ"

    override val createVaultTitle = "ዋና ቮልትዎን ይፍጠሩ"
    override val createMasterPassword = "ዋና የይለፍ ቃል"
    override val masterPasswordReq = "ቢያንስ 8 ቁምፊዎች ከትልቅ/ትንሽ ፊደላት፣ ቁጥሮች እና ምልክቶች ጋር"
    override val confirmMasterPassword = "ዋና የይለፍ ቃል ያረጋግጡ"
    override val zeroKnowledgeWarning = "ዋና የይለፍ ቃልዎን ከረሱ፣ አደራ ሊያየው ወይም ሊመልሰው አይችልም።"
    override val emergencyCodeNotice = "የአደጋ ጊዜ መመለሻ ቁልፍዎን በጥንቃቄ ከመስመር ውጭ ቦታ ያስቀምጡ።"
    override val emergencyRecoveryCode = "የአደጋ ጊዜ መመለሻ ቁልፍ"
    override val saveEmergencyCode = "መመለሻ ቁልፌን በጥንቃቄ አስቀምጫለሁ"
    override val createVaultButton = "ቮልት ይፍጠሩ"
    override val passwordsMatchError = "የይለፍ ቃሎቹ አይመሳሰሉም።"
    override val passwordTooWeakError = "የይለፍ ቃሉ የሚፈለገውን አነስተኛ ጥንካሬ አላሟላም።"

    override val unlockVault = "ቮልትዎን ይክፈቱ"
    override val enterMasterPassword = "ዋና የይለፍ ቃል ያስገቡ"
    override val useBiometrics = "በባዮሜትሪክ ይክፈቱ"
    override val biometricPromptTitle = "የባዮሜትሪክ ማረጋገጫ"
    override val incorrectPassword = "ተሳስቷል። እባክዎ እንደገና ይሞክሩ።"
    override val lockNow = "ቮልቱን ዝጋ"
    override val vaultLockedNotification = "ቮልትዎ ተቆልፏል።"

    override val helloUser = "ሰላም"
    override val welcomeBack = "እንኳን ደህና መጡ"
    override val searchVaultPlaceholder = "በቮልትዎ ውስጥ ይፈልጉ..."
    override val categoriesHeader = "ምድቦች"
    override val seeAll = "ሁሉንም አሳይ"
    override val recentlyUsedHeader = "በቅርብ የተጠቀሟቸው"
    override val showAll = "ሁሉንም አሳይ"
    override val noRecentItems = "እስካሁን በቅርብ የተጠቀሙበት የይለፍ ቃል የለም።"
    override val quickCopyPassword = "የይለፍ ቃል ቅዳ"
    override val passwordCopiedToast = "የይለፍ ቃሉ ተቀድቷል! በ30 ሰከንድ ውስጥ ይጠፋል።"
    override val clipboardClearedToast = "የተቀዳው የይለፍ ቃል በራስ-ሰር ተሰርዟል።"

    override val catSocial = "ማህበራዊ"
    override val catWork = "ስራ"
    override val catApps = "መተግበሪያዎች"
    override val catFinance = "ፋይናንስ"
    override val catShopping = "ግዢ"
    override val catEmail = "ኢሜይል"
    override val catEducation = "ትምህርት"
    override val catCrypto = "ክሪፕቶ (Crypto)"
    override val catOther = "ሌሎች"

    override val typeLogin = "መግቢያ"
    override val typeCard = "የክሬዲት ካርድ"
    override val typeIdentity = "ማንነት"
    override val typeNote = "ሚስጥራዊ ማስታወሻ"
    override val typeWifi = "ዋይ-ፋይ"
    override val typeApiKey = "ኤፒአይ ቁልፍ"
    override val typeLicense = "ፍቃድ"
    override val typeCryptoWallet = "ክሪፕቶ ዋሌት (Crypto Wallet)"
    override val typeOther = "ሌላ"

    override val addLoginTitle = "አዲስ የይለፍ ቃል አክል"
    override val editEntryTitle = "መረጃውን አርም"
    override val titleLabel = "ርዕስ / የአገልግሎት ስም"
    override val usernameLabel = "የተጠቃሚ ስም"
    override val emailLabel = "ኢሜይል አድራሻ"
    override val passwordLabel = "የይለፍ ቃል"
    override val websiteLabel = "ድረ-ገጽ / መተግበሪያ"
    override val categoryLabel = "ምድብ"
    override val notesLabel = "ማስታወሻዎች"
    override val tagsLabel = "ታጎች (በኮማ የተለዩ)"
    override val favoriteLabel = "ወደ ተመረጡት አክል"
    override val saveButton = "አስቀምጥ"
    override val cancelButton = "ሰርዝ"
    override val deleteButton = "አስወግድ"
    override val confirmDeleteTitle = "ይህ መረጃ ይወገድ?"
    override val confirmDeleteMessage = "ይህንን የይለፍ ቃል በቋሚነት ማስወገድ መፈለግዎን እርግጠኛ ነዎት?"

    override val generatorTitle = "የይለፍ ቃል አመንጪ"
    override val passwordLength = "የይለፍ ቃል ርዝመት"
    override val optionUppercase = "ትላልቅ ፊደላት (A-Z)"
    override val optionLowercase = "ትናንሽ ፊደላት (a-z)"
    override val optionNumbers = "ቁጥሮች (0-9)"
    override val optionSymbols = "ምልክቶች (!@#$%^&*)"
    override val optionNoAmbiguous = "የሚያደናግሩ ቁምፊዎችን አስወግድ"
    override val generateButton = "እንደገና አመንጭ"
    override val copyToClipboard = "ወደ ክሊፕቦርድ ቅዳ"
    override val useThisPassword = "ይህንን የይለፍ ቃል ተጠቀም"

    override val securityDashboardTitle = "የይለፍ ቃል ደህንነት"
    override val overallSecurityScore = "የደህንነት ነጥብ"
    override val safePasswordsCount = "አስተማማኝ"
    override val weakPasswordsCount = "ደካማ"
    override val reusedPasswordsCount = "የተደጋገሙ"
    override val compromisedPasswordsCount = "ስጋት ያለባቸው"
    override val oldPasswordsCount = "የቆዩ"
    override val securityNeedsAttention = "የይለፍ ቃል ደህንነት ነጥብዎ ትኩረት ይፈልጋል።"
    override val fixWeakPasswordsAction = "ይመርምሩ እና ያስተካክሉ"
    override val localAnalysisNotice = "ሁሉም የደህንነት ትንተና በመሳሪያዎ ላይ ብቻ ይሰላል።"
    override val healthScoreExcellent = "ከፍተኛ የደህንነት ደረጃ"
    override val healthScoreGood = "ጥሩ የደህንነት ደረጃ"
    override val healthScoreNeedsAttention = "ትኩረት የሚፈልግ የደህንነት ደረጃ"
    override val safePasswords = "አስተማማኝ የይለፍ ቃሎች"
    override val weakPasswords = "ደካማ የይለፍ ቃሎች"
    override val reusedPasswords = "የተደጋገሙ የይለፍ ቃሎች"
    override val compromisedPasswords = "ስጋት ያለባቸው የይለፍ ቃሎች"
    override val oldPasswords = "ያልተቀየሩ የቆዩ የይለፍ ቃሎች"
    override val privacyMode = "የግላዊነት ጥበቃ ሁነታ"
    override val languageSetting = "የመተግበሪያው ቋንቋ"
    override val lockVault = "ቮልቱን ዝጋ"

    override val emptyVaultTitle = "እስካሁን የተቀመጠ የይለፍ ቃል የለም"
    override val emptyVaultDesc = "የዲጂታል መለያዎችዎን መጠበቅ ለመጀመር የመጀመሪያ መግቢያዎን ያክሉ።"
    override val emptyCategoryDesc = "በዚህ ምድብ እስካሁን የተመዘገበ መረጃ የለም።"
    override val emptySearchTitle = "ምንም የተገኘ መረጃ የለም"
    override val emptySearchDesc = "እባክዎ በሌላ ቃል ወይም ምድብ ይፈልጉ።"

    override val settingsTitle = "ቅንብሮች"
    override val sectionAccount = "አካውንት እና ቮልት"
    override val sectionSecurity = "የደህንነት ቅንብሮች"
    override val sectionAppearance = "ገጽታ"
    override val sectionLanguage = "ቋንቋ / Language"
    override val sectionPrivacy = "ግላዊነት እና የቪዲዮ ጥበቃ"
    override val sectionBackup = "የተመሰጠረ የመጠባበቂያ ፋይል"
    override val sectionAbout = "ስለ አደራ"

    override val changeMasterPassword = "ዋና የይለፍ ቃል ቀይር"
    override val enableBiometrics = "ባዮሜትሪክ መክፈቻ"
    override val autoLockDuration = "የመቆለፊያ ጊዜ"
    override val clipboardTimeout = "የክሊፕቦርድ መጽጃ ጊዜ"
    override val privacyModeLabel = "የግላዊነት ጥበቃ"
    override val privacyModeDesc = "በመተግበሪያዎች መቀየሪያ ላይ ሚስጥራዊ የይለፍ ቃሎችን ደብቅ።"
    override val themeLight = "ብርሃናማ"
    override val themeDark = "ጨለማማ"
    override val themeSystem = "የስርዓቱ ነባር"

    override val exportBackup = "የተመሰጠረ መጠባበቂያ ፋይል አውጣ"
    override val importBackup = "የተመሰጠረ መጠባበቂያ ፋይል አስገባ"
    override val backupWarningMessage = "የመጠባበቂያ ፋይልዎን በደህና ያስቀምጡ።"
    override val backupExportSuccess = "የተመሰጠረ የመጠባበቂያ ፋይል በስኬት ተቀምጧል።"
    override val backupImportSuccess = "መረጃዎች በስኬት ተመልሰዋል!"
    override val backupImportError = "የመጠባበቂያ ፋይሉን ማስገባት አልተቻለም። የይለፍ ቃሉን ያረጋግጡ።"

    override val aboutBuiltWithPrivacy = "በግላዊነት እና በደህንነት የተሰራ።"
    override val appVersion = "አደራ ሥሪት v1.0.0"
}
