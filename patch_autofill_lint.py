import re

path = "app/src/main/java/com/example/autofill/AderaAutofillService.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("class AderaAutofillService", "import androidx.annotation.RequiresApi\nimport android.os.Build\n\n@RequiresApi(Build.VERSION_CODES.O)\nclass AderaAutofillService")

with open(path, "w") as f:
    f.write(content)
