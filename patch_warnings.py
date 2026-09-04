import re
import os

# 1. MainActivity.kt
path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

# Replace LocalLifecycleOwner import
content = content.replace("androidx.compose.ui.platform.LocalLifecycleOwner", "androidx.lifecycle.compose.LocalLifecycleOwner")
with open(path, "w") as f:
    f.write(content)

# 2. CryptoWalletBackupScreen.kt
path = "app/src/main/java/com/example/ui/screens/CryptoWalletBackupScreen.kt"
if os.path.exists(path):
    with open(path, "r") as f:
        content = f.read()
    content = content.replace("Icons.Rounded.ArrowBack", "Icons.AutoMirrored.Rounded.ArrowBack")
    with open(path, "w") as f:
        f.write(content)

# 3. AderaAutofillService.kt
path = "app/src/main/java/com/example/autofill/AderaAutofillService.kt"
with open(path, "r") as f:
    content = f.read()

if "@Suppress(\"DEPRECATION\")" not in content:
    content = content.replace("override fun onFillRequest(", "@Suppress(\"DEPRECATION\")\n    override fun onFillRequest(")

with open(path, "w") as f:
    f.write(content)
