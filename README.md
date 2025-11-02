# ⚽ TeamCaptain

An Android app designed to help junior football coaches manage their teams, track attendance, schedule matches, and build lineups.

## 📱 About

TeamCaptain is a personal learning project exploring modern Android development with Kotlin and Jetpack Compose. Built to serve a real-world purpose: simplifying team management for junior football coaches in the Netherlands.

## ✨ Features

- **Team Management**: Add and organize player profiles with preferred positions
- **Attendance Tracking**: Mark player attendance for scheduled matches
- **Game Scheduling**: Schedule matches with date, time, opponent, and location
- **Lineup Builder**: Select starting players (5 field + 1 goalkeeper) and manage substitutions
- **Match Blocks**: Configure substitution blocks and timing (inspired by CoachAmigo)
- **Coach Dashboard**: Quick overview of team status, upcoming games, and attendance

## 🛠️ Tech Stack

- **Language**: Kotlin (with Java interoperability)
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Database**: Room (local persistence)
- **Navigation**: Jetpack Navigation Component
- **Async**: Kotlin Coroutines & Flow
- **Build System**: Gradle with Kotlin DSL
- **Code Quality**: ktlint
- **Testing**: JUnit 5, MockK, Espresso

### Requirements

- **JDK**: OpenJDK 21 (Temurin recommended)
- **Android SDK**: API 35 (Android 15)
- **Minimum SDK**: API 35
- **Gradle**: 8.7+ (uses wrapper)

## 🚀 Getting Started

### Prerequisites

1. **Install Android Studio** (latest stable version)
   - Download from: https://developer.android.com/studio

2. **Install JDK 21**
   ```bash
   # On Ubuntu/Debian
   sudo apt install openjdk-21-jdk

   # On macOS (using Homebrew)
   brew install openjdk@21
   ```

3. **Set JAVA_HOME** (if not already set)
   ```bash
   # Add to ~/.bashrc or ~/.zshrc
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64  # Ubuntu/Debian
   export JAVA_HOME=/opt/homebrew/opt/openjdk@21        # macOS
   ```

4. **Configure Android SDK**
   - Set `ANDROID_HOME` environment variable
   - Or create `local.properties` file in project root:
     ```properties
     sdk.dir=/path/to/your/Android/Sdk
     ```

### Clone and Build

```bash
# Clone the repository
git clone <repository-url>
cd captain

# Build the project
./gradlew build

# Or clean build
./gradlew clean build
```

### Running the App

#### Option 1: Using Android Studio

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an Android device via USB or start an emulator
4. Click the "Run" button (▶️) or press `Shift+F10`

#### Option 2: Using Command Line

```bash
# Install debug build on connected device/emulator
./gradlew installDebug

# Run the app
adb shell am start -n com.lvark.teamcaptain.debug/.MainActivity
```

#### Option 3: Build APK

```bash
# Build debug APK
./gradlew assembleDebug

# APK location: app/build/outputs/apk/debug/app-debug.apk

# Build release APK (requires signing configuration)
./gradlew assembleRelease
```

### Setting Up an Emulator

If you don't have a physical device:

```bash
# List available system images
sdkmanager --list | grep system-images

# Install Android 15 system image (x86_64)
sdkmanager "system-images;android-35;google_apis;x86_64"

# Create an emulator (AVD)
avdmanager create avd -n Pixel_7_API_35 -k "system-images;android-35;google_apis;x86_64" -d "pixel_7"

# Start the emulator
emulator -avd Pixel_7_API_35
```

Or use Android Studio's **AVD Manager** (Tools → Device Manager).

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run tests with coverage
./gradlew testDebugUnitTestCoverage

# Run a specific test class
./gradlew test --tests "com.lvark.teamcaptain.YourTestClass"
```

## 🔍 Code Quality

```bash
# Check code style with ktlint
./gradlew ktlintCheck

# Auto-fix code style issues
./gradlew ktlintFormat

# Run Android lint
./gradlew lint
```

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/lvark/teamcaptain/
│   │   │   ├── ui/              # UI layer (Compose screens, navigation, theme)
│   │   │   ├── viewmodel/       # ViewModels for state management
│   │   │   ├── data/            # Data layer (repositories, DAOs, database)
│   │   │   ├── model/           # Data models and entities
│   │   │   ├── domain/          # Domain layer (use cases, business logic)
│   │   │   └── di/              # Dependency injection modules
│   │   └── res/                 # Android resources (layouts, strings, etc.)
│   ├── test/                    # Unit tests
│   └── androidTest/             # Instrumentation tests
└── build.gradle.kts             # App module build configuration
```

## 🎨 Design

- **UI Style**: Clean, modern, no-nonsense interface
- **Theme**: Material Design 3 with dark mode support
- **Color Customization**: Theme colors can be customized to match club colors

## 🤝 Contributing

This is a personal learning project, but suggestions and feedback are welcome! Feel free to:
- Open issues for bugs or feature requests
- Submit pull requests for improvements
- Share ideas for better UX/UI

## 📝 Development Notes

### Built with Claude Code

This project is being developed using Claude Code (claude.ai/code) for:
- Real-time code suggestions and refactoring
- Architecture guidance and best practices
- Kotlin idiom recommendations
- Debugging assistance

### Kotlin Conventions

- Use `data classes` for models
- Prefer `val` over `var` for immutability
- Use coroutines for async operations
- Follow official Kotlin coding conventions
- Handle nullable types explicitly with safe calls (`?.`) or Elvis operator (`?:`)

### Compose Conventions

- Composable functions use PascalCase naming (suppressed in ktlint)
- Use `remember` and `rememberSaveable` for state management
- Follow unidirectional data flow pattern
- Extract reusable components into separate composables

## 📄 License

[Specify your license here]

## 👤 Author

Built by a Java software engineer learning Kotlin and modern Android development.

## 🙏 Acknowledgments

- Inspired by real coaching needs in junior football
- Design principles influenced by CoachAmigo
- Built with guidance from Claude Code

---

**Note**: This project targets Android 15+ (API 35). For wider compatibility, adjust `minSdk` in `app/build.gradle.kts`.
