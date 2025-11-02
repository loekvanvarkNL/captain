# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

# ⚽ TeamCaptain: Android App for Junior Football Coaches

## 🎯 Project Goals and Motivation

TeamCaptain is a personal learning project designed to explore modern Android development using Kotlin and Claude-Code. As a Java software engineer with growing experience in Kotlin, I aim to deepen my understanding of mobile app architecture, Kotlin best practices, and Claude-powered coding workflows. The app will serve a real-world purpose: helping junior football coaches in my hometown in the Netherlands.

## 👥 Target Users and Use Case

The primary user is a junior football (soccer) coach managing a small team of young players. The app is designed to simplify common coaching tasks such as organizing players, tracking attendance, planning matches, and selecting starting lineups. It’s built for usability, speed, and clarity — ideal for coaches who need quick access to team data during practice or game day.

## 🧩 Planned Features and User Flows

- **Team Management**
  - Add/edit player profiles. This can be as simple as assemble a team of players with name and maybe 2 or 3 preferred positions
  - View team roster

- **Attendance Tracking**
  - For a planned match I want to be able to mark attendance.

- **Game Scheduling**
  - Add upcoming matches with date, time, and opponent
  - Link games to attendance and lineup planning

- **Lineup Builder**
  - Select 6 starting players (5 field + 1 goalkeeper)
  - Assign field positions visually
  - Manage substitutes and bench order
  - A user is able to set 'match blocks' like in CoachAmigo, that essentially sets the amount of substitiution blocks and the time it takes.

- **Coach Dashboard**
  - Quick overview of team status, upcoming games, and attendance

## Look and feel

The UI should feel no-nonsense, clean and modern. Preferrably have a darkmode available.
A nice to have feature is to choose a theme color in the app as a user, so it can look a but like your own club's colours.

Use the most modern Android 17 design priniciples.

## 🛠️ Tech Stack and Architecture Overview

- **Language**: Kotlin (latest LTS), with Java interoperability
- **Frameworks**:
  - Android Jetpack (ViewModel, LiveData, Navigation)
  - Spring Boot 4 (for optional backend or cloud sync)
- **Build Tools**: Gradle
- **JDK**: OpenJDK 21 (Temurin)
- **UI**: Material Design 3, Jetpack Compose (optional)
- **Data Storage**: Room (local), optional Firebase or REST backend
- **Testing**: JUnit5, Espresso, MockK

It should run on Android 16.

Use git with a .gitignore suitable for modern Kotlin Android code made by LLM's.

## 🤖 Claude-Code Integration

- **Vibe Coding**: Using Claude for real-time code suggestions, refactoring ideas, and architectural guidance
- **Inline Claude Chat**: Triggered via VS Code for design decisions, Kotlin idioms, and debugging help
- **Safe Tooling**:
  - Allowed: `git`, `npm`, `mvn`, `gradle`
  - Disallowed: `bash(rm:*)`, `bash(sudo:*)`, `bash(chmod:*)`
- **Claude Plugins**:
  - Claude-Code Helper for Kotlin and Android insights
  - Claude Refactor Assistant for safe code restructuring

---

## 🔧 Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run on connected device/emulator
./gradlew installDebug

# Clean build
./gradlew clean build
```

### Testing
```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew test --tests "com.example.teamcaptain.YourTestClass"

# Run tests with coverage
./gradlew testDebugUnitTestCoverage
```

### Linting and Code Quality
```bash
# Run Kotlin linter
./gradlew ktlintCheck

# Auto-fix linting issues
./gradlew ktlintFormat

# Run Android lint
./gradlew lint
```

## 🏗️ Architecture and Code Structure

### Project Organization
- **app/**: Main Android application module
  - **src/main/kotlin/**: Kotlin source files
    - **ui/**: UI layer (Activities, Fragments, Compose screens)
    - **viewmodel/**: ViewModels for UI state management
    - **data/**: Data layer (repositories, data sources)
    - **domain/**: Domain layer (use cases, business logic)
    - **model/**: Data models and entities
    - **di/**: Dependency injection setup
  - **src/main/res/**: Android resources (layouts, drawables, strings)
  - **src/test/**: Unit tests
  - **src/androidTest/**: Instrumentation tests

### Architecture Pattern
- **MVVM (Model-View-ViewModel)**: Separating UI logic from business logic
- **Repository Pattern**: Abstracting data sources (Room DB, network, etc.)
- **Single Activity Architecture**: Using Navigation Component with Fragments/Compose

### Key Components
- **Room Database**: Local data persistence for players, games, attendance
- **LiveData/Flow**: Reactive data streams for UI updates
- **Navigation Component**: Handling in-app navigation
- **Material Design 3**: UI components and theming

### Kotlin Conventions
- Use data classes for models
- Prefer `val` over `var` for immutability
- Use coroutines for async operations
- Follow official Kotlin coding conventions
- Use nullable types (`?`) explicitly and handle with safe calls (`?.`) or Elvis operator (`?:`)

### Testing Strategy
- **Unit Tests**: ViewModels, repositories, use cases (MockK for mocking)
- **Integration Tests**: Database operations with Room
- **UI Tests**: User flows with Espresso or Compose Testing

---

This project is both a practical tool and a learning playground — built with modern tools, guided by Claude, and inspired by real coaching needs.

