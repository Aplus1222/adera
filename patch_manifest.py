import re

path = "app/src/main/AndroidManifest.xml"
with open(path, "r") as f:
    content = f.read()

content = content.replace("<service\n            android:name=\".autofill.AderaAutofillService\"", "<service\n            android:name=\".autofill.AderaAutofillService\"\n            tools:targetApi=\"26\"")

with open(path, "w") as f:
    f.write(content)
