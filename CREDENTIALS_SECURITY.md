# Secure Credentials Management

TeamCaptain stores OAuth credentials **outside the project directory** for maximum security.

## 🔐 Security Philosophy

**Your credentials are stored at**: `~/.teamcaptain/credentials.properties`

This approach ensures:
- ✅ **Never in your project directory** - No risk of accidental git commits
- ✅ **Never in source code** - Credentials are external to the codebase
- ✅ **Secure file permissions** - Only you can read the file (chmod 600)
- ✅ **Centralized location** - One credentials file for all TeamCaptain projects
- ✅ **Easy to audit** - You know exactly where your secrets are
- ✅ **Easy to revoke** - Just delete one file to remove all credentials

## 🚀 Quick Setup

### Option 1: Interactive Setup Script (Recommended)

```bash
./setup-credentials.sh
```

This script will:
1. Create `~/.teamcaptain/` directory with secure permissions (700)
2. Prompt you for each credential
3. Save them to `~/.teamcaptain/credentials.properties`
4. Set secure file permissions (600 - only you can read/write)

### Option 2: Manual Setup

```bash
# Create directory
mkdir -p ~/.teamcaptain
chmod 700 ~/.teamcaptain

# Create credentials file
cat > ~/.teamcaptain/credentials.properties <<EOF
CAPTAIN_GOOGLE_CLIENT_ID=your_google_client_id_here
CAPTAIN_GITHUB_CLIENT_ID=your_github_client_id_here
CAPTAIN_GITHUB_CLIENT_SECRET=your_github_client_secret_here
EOF

# Secure the file
chmod 600 ~/.teamcaptain/credentials.properties
```

## 📂 File Structure

```
~/.teamcaptain/                    # Outside any project
  └── credentials.properties       # Your OAuth credentials (chmod 600)

/home/loek/dev/captain/           # Your project directory
  ├── app/                        # NO credentials here!
  ├── .git/                       # NO credentials here!
  └── setup-credentials.sh        # Setup script
```

**Credentials are NEVER inside your project directory!**

## 🔍 How It Works

### Build-Time Loading

The `app/build.gradle.kts` reads credentials from `~/.teamcaptain/credentials.properties`:

```kotlin
val credentialsFile = File(System.getProperty("user.home"), ".teamcaptain/credentials.properties")
val credentials = java.util.Properties()
if (credentialsFile.exists()) {
    credentialsFile.inputStream().use { credentials.load(it) }
}

buildConfigField(
    "String",
    "GITHUB_CLIENT_ID",
    "\"${credentials.getProperty("CAPTAIN_GITHUB_CLIENT_ID") ?: ""}\"",
)
```

### Priority Order

The build system checks sources in this order:

1. **External credentials file** (`~/.teamcaptain/credentials.properties`) ← **Recommended**
2. **Environment variables** (`CAPTAIN_GOOGLE_CLIENT_ID`, etc.)
3. **Empty string** (fallback)

## ✅ Verifying Your Setup

### Check File Exists and Permissions

```bash
ls -la ~/.teamcaptain/credentials.properties
```

Expected output:
```
-rw------- 1 loek loek 245 Nov 26 10:30 /home/loek/.teamcaptain/credentials.properties
```

The `-rw-------` means **only you** can read and write this file.

### View Your Credentials

```bash
cat ~/.teamcaptain/credentials.properties
```

### Test the Build

```bash
./gradlew clean build
```

### Check BuildConfig

After building, verify credentials are loaded:

```bash
# Check the generated BuildConfig
cat app/build/generated/source/buildConfig/debug/com/lvark/teamcaptain/BuildConfig.java | grep CLIENT_ID
```

You should see your credentials (not empty strings).

## 🛡️ Security Best Practices

### ✅ DO

- Store credentials in `~/.teamcaptain/credentials.properties`
- Use the setup script for interactive credential entry
- Keep file permissions at 600 (only you can read/write)
- Use different credentials for development and production
- Rotate credentials periodically
- Delete credentials when you stop working on the project: `rm -rf ~/.teamcaptain`

### ❌ DON'T

- ~~Store credentials in the project directory~~
- ~~Commit credentials to Git (impossible with this setup!)~~
- ~~Share your personal credentials file~~
- ~~Give the credentials file world-readable permissions~~
- ~~Email or chat credentials in plain text~~

## 🔄 Managing Credentials

### Update Credentials

```bash
# Option 1: Re-run setup script
./setup-credentials.sh

# Option 2: Edit file directly
nano ~/.teamcaptain/credentials.properties
```

### View Current Credentials

```bash
cat ~/.teamcaptain/credentials.properties
```

### Remove All Credentials

```bash
rm -rf ~/.teamcaptain
```

### Backup Credentials (Securely)

```bash
# Encrypt and backup
gpg --encrypt --recipient your@email.com ~/.teamcaptain/credentials.properties > credentials-backup.gpg

# Restore from backup
gpg --decrypt credentials-backup.gpg > ~/.teamcaptain/credentials.properties
chmod 600 ~/.teamcaptain/credentials.properties
```

## 🖥️ Multiple Projects / Environments

### Shared Credentials

Since credentials are stored in `~/.teamcaptain/`, they're automatically shared across all TeamCaptain clones on your machine:

```
~/dev/captain/          # Uses ~/.teamcaptain/credentials.properties
~/projects/teamcaptain/ # Uses ~/.teamcaptain/credentials.properties (same file!)
```

### Per-Project Credentials

If you need different credentials per project, use environment variables:

```bash
# Project A
cd ~/dev/captain-dev
export CAPTAIN_GOOGLE_CLIENT_ID="dev_credentials"
./gradlew build

# Project B
cd ~/dev/captain-prod
export CAPTAIN_GOOGLE_CLIENT_ID="prod_credentials"
./gradlew build
```

Environment variables **override** the credentials file.

## 🤝 Team Setup

### Onboarding New Developers

Share these instructions with your team:

1. Clone the repository
2. Run `./setup-credentials.sh`
3. Enter your OAuth credentials (they create their own apps)
4. Build the project: `./gradlew build`

**Everyone uses their own credentials stored outside the project!**

### Shared Development Credentials

If your team uses shared development credentials:

1. Share credentials via secure channel (password manager, encrypted file)
2. Each developer runs `./setup-credentials.sh` and enters the shared credentials
3. Credentials stay in `~/.teamcaptain/` on each developer's machine

**Still never committed to Git!**

## 🔐 CI/CD Setup

For continuous integration, use environment variables (not the external file):

### GitHub Actions

```yaml
name: Build
on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    env:
      CAPTAIN_GOOGLE_CLIENT_ID: ${{ secrets.GOOGLE_CLIENT_ID }}
      CAPTAIN_GITHUB_CLIENT_ID: ${{ secrets.GITHUB_CLIENT_ID }}
      CAPTAIN_GITHUB_CLIENT_SECRET: ${{ secrets.GITHUB_CLIENT_SECRET }}
    steps:
      - uses: actions/checkout@v3
      - name: Build
        run: ./gradlew build
```

Add secrets in **Settings → Secrets and variables → Actions**.

### Jenkins / GitLab CI

Set environment variables in your CI configuration:

```yaml
build:
  script:
    - export CAPTAIN_GOOGLE_CLIENT_ID="${GOOGLE_CLIENT_ID}"
    - export CAPTAIN_GITHUB_CLIENT_ID="${GITHUB_CLIENT_ID}"
    - export CAPTAIN_GITHUB_CLIENT_SECRET="${GITHUB_CLIENT_SECRET}"
    - ./gradlew build
```

## 🐛 Troubleshooting

### "Credentials file not found"

```bash
# Check if file exists
ls -la ~/.teamcaptain/credentials.properties

# If not, run setup
./setup-credentials.sh
```

### "Empty credentials in BuildConfig"

```bash
# Verify credentials file has values
cat ~/.teamcaptain/credentials.properties

# Should see non-empty values:
# CAPTAIN_GOOGLE_CLIENT_ID=123456...
```

### "Permission denied reading credentials"

```bash
# Fix permissions
chmod 600 ~/.teamcaptain/credentials.properties
```

### "Android Studio not picking up credentials"

The external file is read at **build time**, so it works in Android Studio automatically!

Just click **Run 'app'** - no special configuration needed.

If you update credentials, do: **Build → Rebuild Project**

## 🔒 Security Audit

### Check What Files Could Contain Secrets

```bash
# In project directory
git ls-files | xargs grep -l "client_id\|client_secret" || echo "✅ No secrets in git"

# Check .gitignore is working
git status --ignored | grep credentials || echo "✅ No credentials tracked"
```

### Verify File Permissions

```bash
# Should be -rw------- (600)
ls -l ~/.teamcaptain/credentials.properties
```

### Search for Hardcoded Secrets

```bash
# Should find ZERO results
grep -r "ghp_\|gho_" . 2>/dev/null || echo "✅ No GitHub tokens found"
```

## 📖 Summary

**Your credentials are:**
- ✅ Stored in `~/.teamcaptain/` (outside project)
- ✅ Never committed to Git
- ✅ Protected by file permissions (chmod 600)
- ✅ Easy to manage and rotate
- ✅ Shared across all project clones on your machine
- ✅ Completely separated from your source code

**This is the most secure way to manage credentials for local development!** 🔐
