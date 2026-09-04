#!/bin/bash
awk '
BEGIN { output=1 }
/^@Composable/ {
    if (in_sheet == 0 && sheet_done == 0) {
        # Nothing
    }
}
' app/src/main/java/com/example/ui/screens/VaultScreen.kt
