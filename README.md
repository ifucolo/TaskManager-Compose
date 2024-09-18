[![Android CI](https://github.com/ifucolo/TaskManager-Compose/actions/workflows/android-ci.yml/badge.svg)](https://github.com/ifucolo/TaskManager-Compose/actions/workflows/android-ci.yml)

# TaskManager - Compose

TaskManager is a simple task management Android application built using Jetpack Compose, Kotlin, Room Database, and Dagger-Hilt for dependency injection. This project is designed to demonstrate modern Android development techniques including MVVM architecture, state management with StateFlow, and more.

## Features

- Create, edit, and delete tasks
- Categorize tasks
- View tasks with real-time updates using Kotlin's Flow
- Simple, modern UI with Jetpack Compose
- Room Database for local persistence
- Dependency Injection with Dagger-Hilt

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Room Database**
- **Dagger-Hilt**
- **Kotlin Coroutines & Flow**
- **MVVM Architecture**
- **Retrofit** (if used for network operations)
- **JUnit** for Unit Testing
- **MockK / Mockito** for mocking in tests

## Project Setup

### Prerequisites

Ensure you have the following installed:

- **Android Studio** (latest stable version)
- **Android SDK 31+**
- **JDK 17**
- **Gradle**

### Cloning the Repository

```bash
git clone https://github.com/yourusername/TaskManager-Compose.git
cd TaskManager-Compose
