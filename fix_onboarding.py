import re

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Fix the duplicate column
content = content.replace("            Column(\n        Column(\n            modifier = modifier", "            Column(\n                modifier = modifier")

# Fix the end of file (extra brace)
content = re.sub(r"        \}\n    \}\n\}\n?\}\n?$", "        }\n    }\n}\n", content)

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
