import re

with open("app/src/main/java/com/example/ui/screens/VaultScreen.kt", "r") as f:
    content = f.read()

# Add import
content = content.replace("import com.example.ui.theme.CategoryAppsBg", "import com.example.ui.theme.CategoryAppsBg\nimport com.example.ui.components.AnimatedMeshBackground")

# Replace Scaffold
scaffold_regex = r"    Scaffold\(\n        modifier = modifier\n    \) \{ innerPadding ->\n        Column\(\n            modifier = Modifier\n                \.fillMaxSize\(\)\n                \.padding\(innerPadding\)\n                \.padding\(horizontal = 20\.dp, vertical = 12\.dp\)\n        \) \{"

new_scaffold = """    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {"""

content = re.sub(scaffold_regex, new_scaffold, content)

# I need to add one extra closing brace for the Box instead of the Scaffold. Wait, Scaffold has one closing brace, Box has one. So I don't need to change the closing braces!
# Wait, Scaffold has `) { innerPadding -> Column(...) {`
# So the closing braces at the end of the `filteredItems` processing are:
# `    }` -> closes Column
# `}` -> closes Scaffold
# If I change it to `Box { AnimatedMeshBackground(); Column {`, the braces match exactly.

with open("app/src/main/java/com/example/ui/screens/VaultScreen.kt", "w") as f:
    f.write(content)
