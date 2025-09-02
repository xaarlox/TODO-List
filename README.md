## TODOList - Android To-Do App
TODOList is a clean, fast Android app for creating and managing tasks. It focuses on a simple UX, reliable local storage, and a modular, testable codebase built with modern Android standards.

### Features
- **Add, edit, delete todos**: Manage tasks with a straightforward flow.
- **Swipe to delete**: Intuitive gesture for removing items.
- **Detail editing**: Dedicated screen for creating/updating tasks.
- **Theming**: App theme built with Material 3 and Compose.
- **Local persistence**: Works offline with Room.


### Tech stack
+ **Language**: Kotlin.
+ **UI framework**: Jetpack Compose.
+ **Architecture & Patterns**: Clean Architecture with multi-module setup (domain, data, app); MVVM and MVI (separate ViewModels by screen).
+ **Async/State**: Coroutines + Flow, AndroidX Lifecycle ViewModel.
+ **Navigation**: AndroidX Navigation.
+ **Dependency Injection**: Hilt.
+ **Persistence**: Room (DAO, entities, database).


### Architecture 
* **Clean Architecture**:
  - `domain` for core models and business rules;
  - `data` for Room and repository implementations;
  - `app` for presentation and DI wiring.
 
* **MVVM & MVI**:
  + MVVM ViewModel for list flow;
  + MVI ViewModel for edit flow (explicit state, events, and effects).


### Testing
- **Unit tests**: Repositories/DAO and core logic (JUnit).
- **Instrumented UI tests**: Compose screens and UI flows.


### Installation
1. Clone the repository.
2. Open in Android Studio.
3. Sync Gradle dependencies.
4. Run on device or emulator.


### License
Personal project for educational and portfolio purposes.
