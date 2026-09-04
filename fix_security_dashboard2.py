import re

with open("app/src/main/java/com/example/ui/screens/SecurityDashboardScreen.kt", "r") as f:
    content = f.read()

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

# I need to add one extra closing brace at the end of the SecurityDashboardScreen composable.
# Let's find where the composable ends. It's before `data class SecurityIssueItem` or `@Composable fun SecurityIssueCard`
# Let's just find `@Composable\nprivate fun SecurityIssueCard` and insert it there.
card_regex = r"(@Composable\nfun SecurityIssueCard)"
content = re.sub(card_regex, r"    }\n}\n\n\1", content)

with open("app/src/main/java/com/example/ui/screens/SecurityDashboardScreen.kt", "w") as f:
    f.write(content)
