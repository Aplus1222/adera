with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    lines = f.readlines()

# Add a closing brace before line 437 (which is private data class CategoryItem)
lines.insert(436, "    }\n")

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.writelines(lines)
