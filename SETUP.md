# AmiMatch Setup Guide

This guide will help you set up the AmiMatch project for development.

## Prerequisites

- Android Studio (latest version recommended)
- Java Development Kit (JDK) 8 or higher
- Android SDK
- Git

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd AmiMatch
```

### 2. Configure Local Properties

1. Copy `local.properties.sample` to `local.properties`
2. Edit `local.properties` and set your Android SDK path:
   - **Windows**: `sdk.dir=C\\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk`
   - **macOS**: `sdk.dir=/Users/YourUsername/Library/Android/sdk`
   - **Linux**: `sdk.dir=/home/YourUsername/Android/Sdk`

### 3. Configure Firebase

1. Create a new Firebase project at https://console.firebase.google.com/
2. Add an Android app to your Firebase project
3. Use package name: `com.mini.amimatch`
4. Download the `google-services.json` file
5. Place it in the `app/` directory (replace the sample file)

### 4. Open in Android Studio

1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the AmiMatch directory and select it
4. Wait for Gradle sync to complete

### 5. Build and Run

1. Connect an Android device or start an emulator
2. Click the "Run" button or use `Shift + F10`

## Firebase Services Used

This project uses the following Firebase services:
- Authentication
- Firestore Database
- Storage
- Analytics
- Messaging

Make sure to enable these services in your Firebase project console.

## Troubleshooting

### Common Issues

1. **Gradle sync fails**: Make sure your `local.properties` file has the correct SDK path
2. **Google Services plugin error**: Ensure your `google-services.json` file is correctly placed in the `app/` directory
3. **Build errors**: Clean and rebuild the project (`Build > Clean Project`, then `Build > Rebuild Project`)

### Getting Help

If you encounter issues:
1. Check the Android Studio logs
2. Ensure all Firebase services are properly configured
3. Verify your development environment meets the prerequisites

## Development Notes

- This is a Beta version of the application
- The project uses AndroidX libraries
- Kotlin is the primary programming language
- The app targets university students for matchmaking functionality