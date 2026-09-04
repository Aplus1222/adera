import re

with open("app/src/main/java/com/example/data/VaultRepository.kt", "r") as f:
    content = f.read()

content = content.replace("private var activeSecretKey: SecretKeySpec? = null", "")
content = content.replace("companion object {", "companion object {\n        var activeSecretKey: SecretKeySpec? = null")

with open("app/src/main/java/com/example/data/VaultRepository.kt", "w") as f:
    f.write(content)
