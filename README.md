# Matrix Calculator

A powerful and elegant Android application for performing matrix operations. This calculator supports addition, subtraction, multiplication, and division of matrices of any dimension.

## Features

- Support for matrices of any dimension
- Four key operations: addition, subtraction, multiplication, and division
- Step-by-step workflow for easy matrix input
- Elegant dark-themed UI with a custom color palette
- Native code implementation for high-performance matrix operations

## Technical Implementation

### Architecture

The Matrix Calculator uses a hybrid architecture that combines:

1. **Jetpack Compose UI**: Modern declarative UI toolkit for building the user interface
2. **C++ Native Operations**: High-performance matrix operations using the Eigen library
3. **JNI Bridge**: Connects the Kotlin UI layer with the C++ computation layer

### Key Components

#### User Interface (Jetpack Compose)
- Step-based workflow for intuitive matrix entry
- Responsive grid layouts for matrix visualization
- Custom dark theme with carefully selected color palette

#### Matrix Operations (C++ with Eigen)
- Fast and efficient matrix operations
- Support for all standard matrix operations
- Proper error handling for invalid operations

### Implementation Details

#### Native Code (C++)
The application uses the Eigen C++ library for matrix operations:
- `matrix-operations.cpp` - Implements the native matrix operations
- Uses JNI (Java Native Interface) to bridge between Kotlin and C++

#### UI Components
Implemented using Jetpack Compose:
- `MatrixCalculatorApp` - Main application component
- `DimensionsInputScreen` - For setting matrix dimensions
- `MatrixInputScreen` - For entering matrix values
- `OperationSelectionScreen` - For selecting the operation
- `ResultScreen` - For displaying the calculated result

#### Data Flow
1. User enters matrix dimensions for both matrices
2. User inputs values for Matrix A
3. User inputs values for Matrix B
4. User selects operation (add, subtract, multiply, divide)
5. The app performs validation and calculation using native code
6. Results are displayed with proper formatting

## Project Structure

```
MatrixCalculator/
├── app/
│   ├── build.gradle.kts                 # App-level Gradle build script
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml      # App manifest file
│   │   │   ├── java/com/example/matrixcalculator/
│   │   │   │   ├── MainActivity.kt      # Main activity with UI components
│   │   │   │   ├── MatrixViewModel.kt   # ViewModel for matrix operations
│   │   │   │   └── ui/theme/
│   │   │   │       ├── Color.kt         # Color definitions
│   │   │   │       ├── Theme.kt         # Theme configuration
│   │   │   │       └── Type.kt          # Typography definitions
│   │   │   ├── cpp/
│   │   │   │   ├── CMakeLists.txt       # CMake configuration
│   │   │   │   ├── matrix-operations.cpp # Native matrix operations
│   │   │   │   └── eigen-3.4.0/         # Eigen library (external)
│   │   │   └── res/
│   │   │       ├── values/
│   │   │       │   ├── colors.xml       # Legacy color resources
│   │   │       │   └── themes.xml       # Legacy theme resources
│   │   │       └── ...
│   │   └── test/                        # Unit tests
│   └── ...
├── build.gradle.kts                     # Project-level Gradle build script
├── settings.gradle.kts                  # Gradle settings
└── gradle/                              # Gradle wrapper
```

## Technical Requirements

### For Users
- Android device running Android 8.0 (API 24) or higher

### For Developers
- Android Studio Hedgehog or later
- CMake 3.18+ for native code compilation
- Android NDK
- Eigen library (3.4.0 recommended)

## Color Scheme

The application features a custom dark theme with the following color palette:
- Background: #0F1316 (Dark Background)
- Card Background: #122026 (Dark Text)
- App Title: #23E09C (Light Green)
- Section Headings: #D9EDDF (Light Text)
- Regular Text: #FFFFFF (White)
- Buttons: #D9EDDF (Light Text) with dark text
- Error Messages: #DE5753 (Red)

## Building and Running

1. Clone the repository
2. Open the project in Android Studio
3. Download the Eigen library (version 3.4.0 recommended)
4. Extract it to `app/src/main/cpp/eigen-3.4.0/`
5. Build and run the application

## Implementation Challenges and Solutions

### Matrix Input
- **Challenge**: Managing state for dynamic matrix dimensions
- **Solution**: Used mutable state lists with proper update methods

### Native Code Integration
- **Challenge**: Bridging between Kotlin and C++ with proper data conversion
- **Solution**: Implemented helper methods to convert between Java arrays and Eigen matrices

### UI Design
- **Challenge**: Creating an intuitive interface for matrix operations
- **Solution**: Developed a step-by-step workflow with visual separation between stages

## Future Enhancements

- Support for more matrix operations (determinant, inverse, transpose)
- Matrix saving and loading functionality
- Step-by-step solution display for educational purposes
- Support for complex number matrices
- Export results to various formats (CSV, LaTeX, etc.)

## Credits

- Eigen library for matrix operations: https://eigen.tuxfamily.org/
- Android Jetpack Compose for the UI: https://developer.android.com/jetpack/compose