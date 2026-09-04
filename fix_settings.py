import re

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.platform.LocalUriHandler", "import androidx.compose.ui.platform.LocalUriHandler\nimport com.example.ui.components.AnimatedMeshBackground")

column_regex = r"    Column\(\n        modifier = modifier\n            \.fillMaxSize\(\)\n            \.verticalScroll\(rememberScrollState\(\)\)\n            \.padding\(horizontal = 20\.dp, vertical = 16\.dp\)\n    \) \{"

new_column = """    Box(modifier = modifier.fillMaxSize()) {
        AnimatedMeshBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {"""

content = re.sub(column_regex, new_column, content)

card_regex = r"(@Composable\nfun SettingsSectionHeader)"
content = re.sub(card_regex, r"    }\n}\n\n\1", content)

with open("app/src/main/java/com/example/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
