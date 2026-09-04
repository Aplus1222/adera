import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.material3.TextButton", "import androidx.compose.material3.TextButton\nimport androidx.compose.material3.Text")

with open(path, "w") as f:
    f.write(content)
