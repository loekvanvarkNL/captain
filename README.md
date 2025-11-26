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

# Set up OAuth credentials (REQUIRED - stored OUTSIDE project directory)
./setup-credentials.sh

# Build the project
./gradlew build
```

**🔐 Credentials are stored securely**:
- Location: `~/.teamcaptain/credentials.properties` (outside your project!)
- Never committed to Git
- Secure file permissions (chmod 600)
- See **[CREDENTIALS_SECURITY.md](CREDENTIALS_SECURITY.md)** for full details

**📚 Additional Setup Guides**:
- **[OAUTH_SETUP.md](OAUTH_SETUP.md)** - How to create OAuth apps
- **[CREDENTIALS_SECURITY.md](CREDENTIALS_SECURITY.md)** - Security best practices

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

## 📊 Current Implementation Status

### ✅ Completed Features

#### 1. Authentication System
- **Login Screen** (`LoginScreen.kt`)
  - OAuth 2.0 OpenID Connect authentication
  - Google Sign-In integration
  - GitHub Sign-In integration
  - Error handling with Snackbar notifications
  - Loading states during authentication
  - **Authentication Management**: `AuthManager.kt` and `AuthStateManager.kt` handle token management and session state

#### 2. Dashboard
- **Dashboard Screen** (`DashboardScreen.kt`)
  - Overview of team statistics (player count)
  - Next upcoming match display with date/time formatting
  - List of upcoming matches (top 3)
  - Quick navigation to team and matches sections
  - Logout functionality
  - Material Design 3 cards with proper elevation and colors

#### 3. Team Management
- **Team List Screen** (`TeamListScreen.kt`)
  - Display all players in a scrollable list
  - Player cards showing:
    - Player name with jersey number (if assigned)
    - Preferred positions with abbreviations (GK, DF, MF, FW)
  - Swipe-to-delete functionality for players
  - Empty state with helpful message when no players exist
  - Floating action button to add new players
  - Player count in top bar

- **Add Player Screen** (`AddPlayerScreen.kt`)
  - Form fields:
    - First name (required)
    - Surname (required)
    - Jersey number (optional, numeric only)
    - Preferred foot selection (Left/Right/Both) with radio buttons
    - Position selection (up to 2 positions) using filter chips
  - Field validation with error messages
  - Save functionality that navigates back on success

- **Player Detail Screen** (`PlayerDetailScreen.kt`)
  - View and edit existing player information
  - All fields from Add Player screen
  - Delete player with confirmation dialog
  - Form pre-populated with player data
  - Save changes functionality

#### 4. Match Management
- **Match List Screen** (`MatchListScreen.kt`)
  - Tab-based navigation:
    - Upcoming matches tab
    - Past matches tab (based on current date/time)
  - Match cards displaying:
    - Opponent name
    - Date and time (formatted: "Wed, Jan 15 at 14:00")
    - Location (if provided)
  - Swipe-to-delete functionality for matches
  - Empty states for both tabs
  - Floating action button to add new matches

- **Add Match Screen** (`AddMatchScreen.kt`)
  - Form fields:
    - Opponent name (required)
    - Date picker with Material Design 3 DatePicker dialog
    - Time picker with Material Design 3 TimePicker dialog
    - Location (optional)
    - Home/Away toggle switch
    - **Total match duration** (e.g., 40 minutes for 2x20 or 4x10)
    - Number of substitution blocks (numeric)
    - Block duration in minutes (numeric)
  - Default values: 40 min total, 4 blocks of 10 minutes each
  - Date/time formatting in input fields
  - Save functionality
  - **Match Format section** with helpful hints for coaches

- **Match Detail Screen** (`MatchDetailScreen.kt`)
  - Tab-based navigation:
    - Attendance tab (embedded `AttendanceScreen`)
    - Lineup tab (navigates to `LineupBuilderScreen`)
  - Match information in top bar (opponent name)
  - Delete match with confirmation dialog
  - Attendance count shown in tab label

#### 5. Attendance Tracking
- **Attendance Screen** (`AttendanceScreen.kt`)
  - List of all players with attendance status
  - Three-state attendance system:
    - Present (green primary container)
    - Absent (red error container)
    - Unknown (default neutral)
  - Filter chips for each status option
  - "Mark All Present" button for quick bulk update
  - Real-time status updates
  - Empty state when no players exist
  - Integrates with Match Detail Screen

#### 6. Lineup Builder (NEW!)
- **Lineup Builder Screen** (`LineupBuilderScreen.kt`)
  - **Visual Football Pitch** with green background and position markers
  - **Drag-and-Drop Player Management**:
    - Drag players from bench to field positions
    - Drag players from field back to bench
    - 6 field positions: 1 GK, 2 DF, 1 MF, 2 FW
  - **Block Navigation**:
    - Previous/Next buttons to move between substitution blocks
    - Current block indicator (e.g., "Block 2 of 4")
    - Save current block button
  - **Auto-Rotation Feature**:
    - "Auto-Generate Equal Playing Time" button
    - Intelligent algorithm distributes players fairly across all blocks
    - Ensures everyone gets approximately equal playing time
  - **Player Chips**:
    - Circular chips showing player number and name
    - Visual feedback during drag operations
  - **Bench Section**:
    - Shows all substitute players
    - Player count indicator
  - **Table Chart Button**: Quick access to Block View overview
  - **Save All**: Persists lineup to database

#### 7. Block View Overview (NEW!)
- **Block View Screen** (`BlockViewScreen.kt`)
  - **Match Summary Card**:
    - Total match duration
    - Number of blocks
    - Block duration
  - **Block Cards**: Schematic view of each block showing:
    - Block number
    - Duration in minutes
    - Number of players assigned
  - **Playing Time Summary**:
    - Complete list of all players
    - Blocks played for each player (e.g., "Blocks: 1, 3, 4")
    - Total minutes played (prominently displayed)
    - Sorted by playing time (most → least)
    - Player initials in circular avatar
  - **At-a-Glance View**: See all players' playing time without pitch visualization
  - Perfect for quickly ensuring fair distribution of playing time

### 🏗️ Data Models & Architecture

#### Database Entities
- **Player** (`Player.kt`)
  - Fields: id, firstName, surname, number, preferredFoot, preferredPositions, createdAt, updatedAt
  - Computed property: `fullName`
  - Supports 4 positions: GK, DF, MF, FW
  - Preferred foot: LEFT, RIGHT, BOTH

- **Match** (`Match.kt`)
  - Fields: id, opponent, dateTime, location, isHome, **totalMatchDurationMinutes**, numberOfBlocks, blockDurationMinutes, createdAt, updatedAt
  - Configurable match blocks for substitution management
  - Computed property: `totalMatchTimeFormatted` (e.g., "2x20 min")

- **Attendance** (`Attendance.kt`)
  - Fields: id, playerId, matchId, status, createdAt, updatedAt
  - Foreign keys with CASCADE delete to Player and Match
  - Status enum: PRESENT, ABSENT, UNKNOWN
  - Indexed on playerId and matchId for query performance

- **User** (`User.kt`) - For authentication

- **LineupAssignment** (`LineupAssignment.kt`) - **Now fully implemented!**
  - Fields: id, matchId, playerId, blockNumber, position, isStarting, createdAt, updatedAt
  - Foreign keys to Player and Match with CASCADE delete
  - Indexed for efficient queries
  - Stores complete lineup configuration for all blocks

#### Repository Pattern
- **PlayerRepository** - CRUD operations for players
- **MatchRepository** - CRUD operations for matches
- **AttendanceRepository** - Attendance management with player join queries
- **LineupRepository** - **Fully implemented!**
  - `saveLineupForBlock()` - Saves lineup for specific block
  - `getLineupForMatch()` - Retrieves all lineup assignments
  - `getLineupForBlock()` - Block-specific lineup queries
  - Transaction support for atomic updates

#### ViewModels (MVVM Architecture)
- **AuthViewModel** - Handles authentication flow
- **DashboardViewModel** - Aggregates data for dashboard
- **TeamViewModel** - Manages player list
- **AddPlayerViewModel** / **PlayerDetailViewModel** - Player form logic
- **MatchListViewModel** - Separates upcoming/past matches
- **AddMatchViewModel** / **MatchDetailViewModel** - Match form and detail logic
- **AttendanceViewModel** - Manages attendance state with PlayerAttendance data class
- **LineupBuilderViewModel** - **NEW!** Complete lineup management
  - Present players detection (filters by attendance)
  - Block navigation and state management
  - Drag-and-drop position assignments
  - Auto-rotation algorithm for equal playing time
  - Block lineup persistence
- **BlockViewViewModel** - **NEW!** Playing time analytics
  - Aggregates player playing time across all blocks
  - Calculates total minutes per player
  - Block-by-block summaries

#### Dependency Injection (Hilt)
- **DatabaseModule** - Provides Room database and DAOs
- **RepositoryModule** - Provides repository instances

#### Navigation
- Type-safe navigation using sealed `Screen` class
- Navigation routes for all screens with parameter passing
- Deep linking support ready

### 🔨 Technical Implementation Details

#### UI/UX Patterns Used
- **Swipe-to-dismiss**: Players and matches can be swiped left to delete
- **Material Design 3**: Full MD3 theming with color system
- **Responsive layouts**: Using Column, Row, LazyColumn for efficiency
- **Form validation**: Real-time error feedback on required fields
- **Empty states**: Helpful messages when lists are empty
- **Confirmation dialogs**: For destructive actions (delete player/match)
- **Loading states**: Circular progress indicator during auth

#### State Management
- `collectAsStateWithLifecycle()` for observing StateFlow from ViewModels
- `remember` and `mutableStateOf` for local UI state
- `derivedStateOf` for computed values (formatted dates/times)
- Unidirectional data flow pattern

#### Date/Time Handling
- Uses `SimpleDateFormat` with locale-aware formatting
- Calendar API for date/time manipulation
- Millisecond timestamps for database storage
- User-friendly display formats

### ❌ Not Yet Implemented

#### High Priority Features
1. ~~**Lineup Builder**~~ - **✅ COMPLETE!**
2. ~~**Match Block Visualization**~~ - **✅ COMPLETE (Block View)!**

#### Medium Priority Features
3. **Player Statistics**
   - Attendance percentage
   - Games played
   - Positions played history

4. **Team Customization**
   - Theme color picker (club colors)
   - Team name and logo
   - Coach profile

5. **Match Results**
   - Score tracking
   - Match notes
   - Performance tracking

#### Low Priority / Future Enhancements
6. **Data Sync & Backup**
   - Cloud sync (Firebase/REST backend)
   - Export/import team data
   - Share lineups with parents

7. **Notifications**
   - Upcoming match reminders
   - Attendance request notifications

8. **Advanced Features**
   - Training session tracking
   - Player development notes
   - Parent communication portal

### 🐛 Known Issues & Limitations
- App requires Android 15+ (API 35) - high minimum SDK requirement
- No data backup - data loss risk on uninstall
- No multi-team support - single team per installation
- ~~Lineup screen is placeholder only~~ - **✅ FIXED - Fully functional!**
- No offline sync - purely local database
- Authentication tokens not persisted across app restarts (needs implementation)
- Drag-and-drop in Lineup Builder is basic (uses gesture detection, not full DnD APIs)

### 📋 Recommended Next Steps

#### Immediate Priorities
1. ~~**Implement Lineup Builder**~~ - **✅ COMPLETE!**
   - ✅ Visual football pitch with drag-and-drop
   - ✅ Position assignment logic
   - ✅ Auto-rotation algorithm for equal playing time
   - ✅ Block View for playing time overview
   - ✅ Full integration with match blocks

2. **Lower Minimum SDK** - Expand device compatibility
   - Review API 35-specific features used
   - Consider supporting API 28+ (Android 9+) for broader adoption

3. **Persist Auth State** - Improve user experience
   - Store auth tokens securely (EncryptedSharedPreferences)
   - Auto-login on app restart
   - Token refresh mechanism

#### Secondary Priorities
4. **Add Unit Tests** - Improve code quality
   - ViewModel tests
   - Repository tests
   - Data transformation tests

5. **Theme Color Picker** - Fulfill club colors feature
   - Add settings screen
   - Implement dynamic theming
   - Color persistence

6. **Data Export** - Reduce data loss risk
   - Export to CSV/JSON
   - Backup restoration
   - Share functionality

## 🎨 Design

- **UI Style**: Clean, modern, no-nonsense interface
- **Theme**: Material Design 3 with dark mode support
- **Color Customization**: Theme colors can be customized to match club colors (planned feature)

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
