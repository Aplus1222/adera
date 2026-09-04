import re

path = "app/src/main/java/com/example/ui/screens/CryptoWalletBackupScreen.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material.icons.Icons", "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.automirrored.rounded.ArrowBack")
content = content.replace("Icons.Rounded.ArrowBack", "Icons.AutoMirrored.Rounded.ArrowBack")

with open(path, "w") as f:
    f.write(content)
