# Running TeamCaptain in Android Studio

This guide explains how to ensure Android Studio picks up your OAuth environment variables when you click "Run 'app'".

## The Problem

Android Studio is typically launched by your desktop environment (not from a terminal), so it doesn't inherit your shell's environment variables. When you set variables in `~/.bashrc` or `~/.zshrc`, they won't be available to Android Studio.

## Solutions (Choose One)

### ✅ Solution 1: Launch Android Studio from Terminal (Recommended)

This is the simplest and most reliable method.

#### On Linux
```bash
# Set your environment variables
export CAPTAIN_GOOGLE_CLIENT_ID="your_google_client_id"
export CAPTAIN_GITHUB_CLIENT_ID="your_github_client_id"
export CAPTAIN_GITHUB_CLIENT_SECRET="your_github_client_secret"

# Launch Android Studio from the same terminal
studio.sh  # or wherever your Android Studio is installed
# Common paths:
# - /opt/android-studio/bin/studio.sh
# - ~/android-studio/bin/studio.sh
```

#### On macOS
```bash
# Set your environment variables
export CAPTAIN_GOOGLE_CLIENT_ID="your_google_client_id"
export CAPTAIN_GITHUB_CLIENT_ID="your_github_client_id"
export CAPTAIN_GITHUB_CLIENT_SECRET="your_github_client_secret"

# Launch Android Studio from terminal
open -a "Android Studio"
```

#### On Windows
```powershell
# Set environment variables in PowerShell
$env:CAPTAIN_GOOGLE_CLIENT_ID="your_google_client_id"
$env:CAPTAIN_GITHUB_CLIENT_ID="your_github_client_id"
$env:CAPTAIN_GITHUB_CLIENT_SECRET="your_github_client_secret"

# Launch Android Studio from same PowerShell window
& "C:\Program Files\Android\Android Studio\bin\studio64.exe"
```

### ✅ Solution 2: Use gradle.properties (Local Only)

Create a `gradle.properties` file in your project root that's **ignored by Git**.

#### Step 1: Create `gradle.properties`
```bash
# In project root (/home/loek/dev/captain/)
touch gradle.properties
```

#### Step 2: Add to .gitignore (already done in this project)
The `.gitignore` already excludes `gradle.properties`, but verify:
```
# Check .gitignore contains:
gradle.properties
```

#### Step 3: Add your credentials to `gradle.properties`
```properties
# gradle.properties
CAPTAIN_GOOGLE_CLIENT_ID=your_google_client_id_here
CAPTAIN_GITHUB_CLIENT_ID=your_github_client_id_here
CAPTAIN_GITHUB_CLIENT_SECRET=your_github_client_secret_here
```

#### Step 4: Update `app/build.gradle.kts`
You would need to modify the build script to read from gradle.properties:
```kotlin
// In defaultConfig block
buildConfigField(
    "String",
    "GITHUB_CLIENT_ID",
    "\"${project.findProperty("CAPTAIN_GITHUB_CLIENT_ID") ?: System.getenv("CAPTAIN_GITHUB_CLIENT_ID") ?: ""}\"",
)
buildConfigField(
    "String",
    "GITHUB_CLIENT_SECRET",
    "\"${project.findProperty("CAPTAIN_GITHUB_CLIENT_SECRET") ?: System.getenv("CAPTAIN_GITHUB_CLIENT_SECRET") ?: ""}\"",
)
buildConfigField(
    "String",
    "GOOGLE_CLIENT_ID",
    "\"${project.findProperty("CAPTAIN_GOOGLE_CLIENT_ID") ?: System.getenv("CAPTAIN_GOOGLE_CLIENT_ID") ?: ""}\"",
)
```

This checks `gradle.properties` first, then falls back to environment variables.

### ✅ Solution 3: Set System-Wide Environment Variables

#### On Linux (systemd)
Edit `~/.config/environment.d/android-studio.conf`:
```bash
mkdir -p ~/.config/environment.d
cat > ~/.config/environment.d/android-studio.conf <<EOF
CAPTAIN_GOOGLE_CLIENT_ID=your_google_client_id
CAPTAIN_GITHUB_CLIENT_ID=your_github_client_id
CAPTAIN_GITHUB_CLIENT_SECRET=your_github_client_secret
EOF
```

Then **log out and log back in** (or reboot).

#### On macOS
Edit `~/.zshenv` (loaded by GUI apps):
```bash
# ~/.zshenv
export CAPTAIN_GOOGLE_CLIENT_ID="your_google_client_id"
export CAPTAIN_GITHUB_CLIENT_ID="your_github_client_id"
export CAPTAIN_GITHUB_CLIENT_SECRET="your_github_client_secret"
```

Then **restart** or run:
```bash
launchctl setenv CAPTAIN_GOOGLE_CLIENT_ID "your_google_client_id"
launchctl setenv CAPTAIN_GITHUB_CLIENT_ID "your_github_client_id"
launchctl setenv CAPTAIN_GITHUB_CLIENT_SECRET "your_github_client_secret"
```

#### On Windows
Use **System Properties** → **Environment Variables**:

1. Press `Win + X` → **System** → **Advanced system settings**
2. Click **Environment Variables**
3. Under **User variables**, click **New**
4. Add each variable:
   - `CAPTAIN_GOOGLE_CLIENT_ID`
   - `CAPTAIN_GITHUB_CLIENT_ID`
   - `CAPTAIN_GITHUB_CLIENT_SECRET`
5. Click **OK** and **restart Android Studio**

### ✅ Solution 4: Android Studio Run Configuration

Set environment variables directly in the Run Configuration.

1. In Android Studio, go to **Run** → **Edit Configurations**
2. Select your **app** configuration
3. In the **Environment variables** field, add:
   ```
   CAPTAIN_GOOGLE_CLIENT_ID=your_value;CAPTAIN_GITHUB_CLIENT_ID=your_value;CAPTAIN_GITHUB_CLIENT_SECRET=your_value
   ```
   (Use `;` on Windows, `:` on Linux/macOS as separator)
4. Click **Apply** and **OK**

**Note**: This only affects running the app, not building it. You may still need environment variables for Gradle builds.

## Verifying It Works

### Method 1: Check BuildConfig
After building, check that credentials are populated:

1. Build the project: **Build** → **Make Project** (`Ctrl+F9`)
2. Navigate to: `app/build/generated/source/buildConfig/debug/com/lvark/teamcaptain/BuildConfig.java`
3. Look for:
   ```java
   public static final String GITHUB_CLIENT_ID = "your_value";
   public static final String GOOGLE_CLIENT_ID = "your_value";
   ```

If they're empty (`""`), the environment variables aren't being picked up.

### Method 2: Gradle Task with Echo
Add this to `app/build.gradle.kts` temporarily:
```kotlin
tasks.register("printEnv") {
    doLast {
        println("GITHUB_CLIENT_ID: ${System.getenv("CAPTAIN_GITHUB_CLIENT_ID")}")
        println("GOOGLE_CLIENT_ID: ${System.getenv("CAPTAIN_GOOGLE_CLIENT_ID")}")
    }
}
```

Run from Android Studio terminal:
```bash
./gradlew printEnv
```

## Recommended Workflow

For **daily development**, I recommend:

1. **Option 1** (Launch from Terminal) - Quick and reliable
2. Keep credentials in a **password manager** (not in a text file)
3. Create a **shell script** to launch Android Studio with credentials:

```bash
#!/bin/bash
# ~/bin/start-android-studio.sh

export CAPTAIN_GOOGLE_CLIENT_ID="$(secret-tool lookup app teamcaptain key google_client_id)"
export CAPTAIN_GITHUB_CLIENT_ID="$(secret-tool lookup app teamcaptain key github_client_id)"
export CAPTAIN_GITHUB_CLIENT_SECRET="$(secret-tool lookup app teamcaptain key github_client_secret)"

studio.sh  # or: open -a "Android Studio" on macOS
```

Make it executable:
```bash
chmod +x ~/bin/start-android-studio.sh
```

## Troubleshooting

### "Empty credentials after build"
- **Cause**: Android Studio not picking up environment variables
- **Fix**: Launch Android Studio from terminal OR use `gradle.properties`

### "Invalidate Caches doesn't help"
- **Cause**: Gradle cached the old BuildConfig
- **Fix**:
  ```bash
  ./gradlew clean
  # Delete build folder
  rm -rf app/build
  # Rebuild
  ./gradlew build
  ```

### "Works in terminal but not in IDE"
- **Cause**: IDE launched before environment variables were set
- **Fix**: Close Android Studio completely and launch from terminal with variables set

### "Still not working on macOS"
- **Cause**: macOS doesn't load `~/.bashrc` for GUI apps
- **Fix**: Use `~/.zshenv` or `launchctl setenv`

## Security Reminder

⚠️ **Never commit credentials to Git**, even in `gradle.properties`!

Always verify `.gitignore` contains:
```
gradle.properties
local.properties
```

Run this to check for secrets before committing:
```bash
git diff --cached | grep -i "client_id\|client_secret"
```

If you accidentally commit secrets, **immediately revoke them** and create new ones!
