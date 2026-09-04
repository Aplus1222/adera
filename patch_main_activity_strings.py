import re

path = "app/src/main/java/com/example/MainActivity.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("strings.verifyMasterPassword", "strings.enterMasterPassword")
content = content.replace("strings.enterMasterPasswordToContinue", '"Please enter your Master Password to continue."')
content = content.replace("strings.incorrectMasterPassword", "strings.incorrectPassword")

with open(path, "w") as f:
    f.write(content)
