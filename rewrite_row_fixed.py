import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# Add getBrandIcon import if not present
if "com.example.ui.components.getBrandIcon" not in content:
    content = content.replace("import com.example.model.VaultItemType", "import com.example.model.VaultItemType\nimport com.example.ui.components.getBrandIcon\nimport androidx.compose.material.icons.filled.Visibility\nimport androidx.compose.material.icons.filled.VisibilityOff\nimport androidx.compose.foundation.Image\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember")

row_func_pattern = r'(@Composable\s*fun VaultItemRow\([\s\S]*?)(?=\n@Composable|\Z)'
match = re.search(row_func_pattern, content)

if match:
    row_code = match.group(1)
    
    # 1. Add `passwordVisible` state
    row_code = row_code.replace(
        'val haptic = LocalHapticFeedback.current',
        'val haptic = LocalHapticFeedback.current\n    var passwordVisible by remember { mutableStateOf(false) }'
    )
    
    # 2. Add brand icon
    brand_icon_code = """val brandIcon = getBrandIcon(item.title, item.website)
                if (brandIcon != null) {
                    Image(
                        painter = brandIcon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f))"""
    row_code = row_code.replace('Column(modifier = Modifier.weight(1f))', brand_icon_code)
    
    # 3. Add toggle icon and change password text
    pass_row_pattern = r'Row\(\s*verticalAlignment = Alignment\.CenterVertically,\s*horizontalArrangement = Arrangement\.SpaceBetween,\s*modifier = Modifier\.fillMaxWidth\(\)\s*\)\s*\{\s*Text\([\s\S]*?\}\s*\}\s*\}'
    
    new_pass_row = """Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (passwordVisible) item.password else "•••• ••••",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (passwordVisible) 1.sp else 4.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onCopyPassword() }.weight(1f)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            passwordVisible = !passwordVisible
                        },
                        modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCopyPassword()
                        },
                        modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Secret",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }"""
    
    row_code = re.sub(pass_row_pattern, new_pass_row, row_code)
    
    new_content = content[:match.start()] + row_code + content[match.end():]
    
    with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
        f.write(new_content)
else:
    print("VaultItemRow not found!")

