# OAuth Setup Guide

This guide explains how to set up OAuth authentication for TeamCaptain using environment variables.

## Why Environment Variables?

OAuth credentials (Client IDs and Client Secrets) should **never** be committed to version control. TeamCaptain uses environment variables to keep these secrets secure while allowing the app to build and run.

## Prerequisites

You need to create OAuth applications with:
1. **Google** (for Google Sign-In)
2. **GitHub** (for GitHub Sign-In)

## Step 1: Create OAuth Applications

### Google OAuth Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create a new project or select an existing one
3. Enable the "Google+ API"
4. Go to **Credentials** → **Create Credentials** → **OAuth 2.0 Client ID**
5. Select **Android** as the application type
6. Enter the package name: `com.lvark.teamcaptain`
7. Get your SHA-1 fingerprint:
   ```bash
   ./gradlew signingReport
   ```
   Copy the SHA-1 from the debug keystore
8. Save the Client ID (you'll need it for the environment variable)

### GitHub OAuth Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click **"New OAuth App"**
3. Fill in the details:
   - **Application name**: TeamCaptain (or your preferred name)
   - **Homepage URL**: `https://github.com/yourusername/captain`
   - **Authorization callback URL**: `com.lvark.teamcaptain://oauth2redirect/github`
4. Click **"Register application"**
5. Copy the **Client ID** and **Client Secret**

## Step 2: Set Environment Variables

### On Linux/macOS

Add these lines to your `~/.bashrc`, `~/.zshrc`, or `~/.profile`:

```bash
export CAPTAIN_GOOGLE_CLIENT_ID="your_google_client_id_here"
export CAPTAIN_GITHUB_CLIENT_ID="your_github_client_id_here"
export CAPTAIN_GITHUB_CLIENT_SECRET="your_github_client_secret_here"
```

Then reload your shell:
```bash
source ~/.bashrc  # or ~/.zshrc
```

### On Windows

#### PowerShell
```powershell
[Environment]::SetEnvironmentVariable("CAPTAIN_GOOGLE_CLIENT_ID", "your_google_client_id_here", "User")
[Environment]::SetEnvironmentVariable("CAPTAIN_GITHUB_CLIENT_ID", "your_github_client_id_here", "User")
[Environment]::SetEnvironmentVariable("CAPTAIN_GITHUB_CLIENT_SECRET", "your_github_client_secret_here", "User")
```

#### Command Prompt
```cmd
setx CAPTAIN_GOOGLE_CLIENT_ID "your_google_client_id_here"
setx CAPTAIN_GITHUB_CLIENT_ID "your_github_client_id_here"
setx CAPTAIN_GITHUB_CLIENT_SECRET "your_github_client_secret_here"
```

**Note**: You'll need to restart your terminal/IDE after setting environment variables on Windows.

### For Android Studio

If the environment variables aren't picked up by Android Studio:

1. **Restart Android Studio** completely
2. Or set them in Android Studio's terminal:
   - Open the Terminal tab in Android Studio
   - Set the variables as shown above for your OS
   - Run the build from that terminal

## Step 3: Verify Setup

1. Check that the environment variables are set:
   ```bash
   echo $CAPTAIN_GOOGLE_CLIENT_ID
   echo $CAPTAIN_GITHUB_CLIENT_ID
   echo $CAPTAIN_GITHUB_CLIENT_SECRET
   ```

2. Build the project:
   ```bash
   ./gradlew clean build
   ```

3. The build should succeed. The credentials will be compiled into `BuildConfig`.

## How It Works

### Build Configuration

The `app/build.gradle.kts` file reads environment variables at build time:

```kotlin
buildConfigField(
    "String",
    "GITHUB_CLIENT_ID",
    "\"${System.getenv("CAPTAIN_GITHUB_CLIENT_ID") ?: ""}\"",
)
buildConfigField(
    "String",
    "GITHUB_CLIENT_SECRET",
    "\"${System.getenv("CAPTAIN_GITHUB_CLIENT_SECRET") ?: ""}\"",
)
buildConfigField(
    "String",
    "GOOGLE_CLIENT_ID",
    "\"${System.getenv("CAPTAIN_GOOGLE_CLIENT_ID") ?: ""}\"",
)
```

### Usage in Code

The credentials are accessed via `BuildConfig`:

```kotlin
val clientId = when (provider) {
    AuthProvider.GOOGLE -> BuildConfig.GOOGLE_CLIENT_ID
    AuthProvider.GITHUB -> BuildConfig.GITHUB_CLIENT_ID
}
```

## Security Notes

✅ **DO:**
- Set environment variables on your development machine
- Share setup instructions (this file) with your team
- Use different credentials for development and production

❌ **DON'T:**
- Commit credentials to Git
- Hardcode credentials in source files
- Share credentials in chat/email

## Troubleshooting

### Build fails with empty credentials

**Problem**: Build succeeds but authentication doesn't work (blank Client ID)

**Solution**:
1. Verify environment variables are set: `echo $CAPTAIN_GOOGLE_CLIENT_ID`
2. Restart your IDE/terminal
3. Clean and rebuild: `./gradlew clean build`

### Android Studio doesn't pick up environment variables

**Problem**: Variables work in terminal but not in Android Studio

**Solution**:
1. Completely quit and restart Android Studio
2. On macOS, launch Android Studio from terminal: `open -a "Android Studio"`
3. Check **File → Invalidate Caches → Invalidate and Restart**

### GitHub OAuth returns "Bad credentials"

**Problem**: GitHub authentication fails

**Solution**:
1. Verify the callback URL in GitHub matches: `com.lvark.teamcaptain://oauth2redirect/github`
2. Check Client ID and Client Secret are correct
3. Ensure Client Secret is set (even though it's not used in mobile OAuth, it's defined in BuildConfig)

## CI/CD Setup

For continuous integration (GitHub Actions, CircleCI, etc.), set the environment variables in your CI configuration:

### GitHub Actions
```yaml
env:
  CAPTAIN_GOOGLE_CLIENT_ID: ${{ secrets.GOOGLE_CLIENT_ID }}
  CAPTAIN_GITHUB_CLIENT_ID: ${{ secrets.GITHUB_CLIENT_ID }}
  CAPTAIN_GITHUB_CLIENT_SECRET: ${{ secrets.GITHUB_CLIENT_SECRET }}
```

Then add the secrets in **Settings → Secrets and variables → Actions**.

## Team Setup

When onboarding a new developer:

1. Share this `OAUTH_SETUP.md` file
2. They create their own OAuth apps (or use shared dev credentials)
3. They set their local environment variables
4. They build the project

No code changes or git commits needed! 🎉
