import sys

def check(file):
    with open(file, 'r') as f:
        content = f.read()
    
    count = 0
    for i, c in enumerate(content):
        if c == '{': count += 1
        elif c == '}': count -= 1
    print(f"{file} balance: {count}")

check("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
check("app/src/main/java/com/example/ui/screens/SecurityDashboardScreen.kt")
check("app/src/main/java/com/example/ui/screens/SettingsScreen.kt")
