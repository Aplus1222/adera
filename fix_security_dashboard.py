import re

with open("app/src/main/java/com/example/ui/screens/SecurityDashboardScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import com.example.model.PasswordHealth", "import com.example.model.PasswordHealth\nimport com.example.ui.components.AnimatedMeshBackground")

# Replace Scaffold
scaffold_regex = r"    Scaffold\(\n        topBar = \{[\s\S]*?\} ->\n        Column\(\n            modifier = Modifier\n                \.fillMaxSize\(\)\n                \.padding\(innerPadding\)"

new_scaffold = """    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.securityDashboardTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedMeshBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)"""

content = re.sub(r"    Scaffold\([\s\S]*?\} ->\n        Column\(\n            modifier = Modifier\n                \.fillMaxSize\(\)\n                \.padding\(innerPadding\)", new_scaffold, content)

with open("app/src/main/java/com/example/ui/screens/SecurityDashboardScreen.kt", "w") as f:
    f.write(content)
