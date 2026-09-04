import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# Add import
content = content.replace("import com.example.ui.components.getBrandIcon", "import com.example.ui.components.getBrandIcon\nimport com.example.ui.components.AnimatedMeshBackground")

# Replace Column with Box + AnimatedMeshBackground
column_regex = r"    Column\(\n        modifier = modifier\n            \.fillMaxSize\(\)"
new_column = """    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()"""

content = re.sub(column_regex, new_column, content)

# I need to add one extra closing brace at the end of the HomeScreen composable.
# Let's find where HomeScreen ends.
# I'll just look for "@Composable\nprivate fun CategoryGrid" and insert the brace right before it.
category_grid_regex = r"(@Composable\nprivate fun CategoryGrid)"
content = re.sub(category_grid_regex, r"    }\n}\n\n\1", content)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
