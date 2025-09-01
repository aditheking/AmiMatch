# AmiMatch 💕

> A university-focused connectivity (social) application designed to help students connect and build meaningful relationships.

[![Version](https://img.shields.io/badge/version-1.0_Beta-orange.svg)]()
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)]()
[![Language](https://img.shields.io/badge/language-Kotlin-blue.svg)]()
[![Firebase](https://img.shields.io/badge/backend-Firebase-yellow.svg)]()

## 📱 About AmiMatch

AmiMatch is a modern, intuitive application specifically designed for university students. Built with the latest Android technologies and powered by Firebase, it provides a secure and engaging platform for students to discover and connect with like-minded peers within their academic community.

### ✨ Key Features

- **🎯 University-Focused Matching**: Tailored specifically for the university environment
- **🔥 Smart Matching Algorithm**: Advanced matching system to find compatible connections
- **💬 Real-time Messaging**: Instant messaging with matched users
- **📸 Rich Profiles**: Comprehensive profile system with photos and interests
- **🎨 Modern UI/UX**: Beautiful, intuitive interface with smooth animations
- **🔒 Secure & Private**: Built with privacy and security in mind
- **⚡ Real-time Updates**: Live notifications and instant updates

### 🎬 App Highlights

- **Card Stack Interface**: Swipe-based matching system with smooth animations
- **Lottie Animations**: Beautiful, engaging animations throughout the app
- **Material Design**: Modern Android design principles
- **Offline Support**: Core functionality available offline
- **Push Notifications**: Stay connected with real-time notifications

## 🛠️ Technology Stack

### Frontend
- **Kotlin** - Primary programming language
- **Android Jetpack** - Modern Android development components
- **AndroidX Libraries** - Latest Android support libraries
- **Material Design Components** - Google's design system
- **Lottie** - Beautiful animations
- **Glide** - Image loading and caching
- **CardStackView** - Swipe-based card interface

### Backend & Services
- **Firebase Authentication** - Secure user authentication
- **Cloud Firestore** - Real-time NoSQL database
- **Firebase Storage** - File and image storage
- **Firebase Messaging** - Push notifications
- **Firebase Analytics** - App analytics and insights

### Build Tools & Development
- **Gradle** - Build automation
- **Kotlin DSL** - Modern Gradle configuration
- **Android Studio** - Primary IDE
- **Git** - Version control

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (latest version recommended)
- **JDK 8** or higher
- **Android SDK** (API level 23 or higher)
- **Git** for version control

### Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/AmiMatch.git
   cd AmiMatch
   ```

2. **Set up local configuration**
   ```bash
   cp local.properties.sample local.properties
   # Edit local.properties with your Android SDK path
   ```

3. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name: `com.mini.amimatch`
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication, Firestore, Storage, and Messaging services

4. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the AmiMatch directory
   - Wait for Gradle sync to complete

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's run button

For detailed setup instructions, see [SETUP.md](SETUP.md).

## 📁 Project Structure

```
AmiMatch/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mini/amimatch/     # Kotlin source files
│   │   │   ├── res/                        # Resources (layouts, drawables, etc.)
│   │   │   ├── assets/                     # App assets (animations, etc.)
│   │   │   └── AndroidManifest.xml         # App manifest
│   │   └── test/                           # Unit tests
│   ├── build.gradle.kts                    # App-level Gradle config
│   └── google-services.json.sample        # Firebase config sample
├── gradle/                                 # Gradle wrapper
├── build.gradle.kts                       # Project-level Gradle config
├── settings.gradle.kts                    # Gradle settings
├── gradle.properties                      # Project properties
├── local.properties.sample               # Local config sample
├── SETUP.md                              # Detailed setup guide
├── .gitignore                            # Git ignore rules
└── README.md                             # This file
```

## 🎯 Core Features Implementation

### Matching System
- **Card Stack View**: Implemented using `yuyakaido:cardstackview`
- **Swipe Gestures**: Left (reject), Right (like), Up/Down navigation
- **Smooth Animations**: Lottie animations for enhanced user experience

### Real-time Chat
- **Firebase Integration**: Real-time messaging using Firestore
- **Message Status**: Delivery and read receipts
- **Media Sharing**: Photo and file sharing capabilities

### Profile Management
- **Rich Profiles**: Multiple photos, interests, and detailed information
- **Privacy Controls**: Granular privacy settings
- **Profile Verification**: University email verification

### Notifications
- **Push Notifications**: Firebase Cloud Messaging integration
- **In-app Notifications**: Real-time updates and alerts
- **Notification Preferences**: Customizable notification settings

## 🧪 Current Status: Beta Version

AmiMatch is currently in **Beta** stage. This means:

- ✅ Core functionality is implemented and working
- ⚠️ Some features may be incomplete or unstable
- 🐛 Bug reports and feedback are highly appreciated
- 🚀 Active development is ongoing

### Known Limitations
- Some UI elements may need refinement
- Advanced matching algorithms are being optimized
- Additional features are planned for future releases

## 🤝 Contributing

We welcome contributions to AmiMatch! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add some amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines

- Follow Kotlin coding conventions
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed
- Ensure Firebase security rules are maintained

## 📝 License

This project is currently in development. License terms will be specified in future releases.

## 🙏 Acknowledgments

### Template Attribution
This project was built upon a pre-made template forked from a GitHub project. While the original template repository link is no longer available (the project was created some time ago), we acknowledge the foundational work that helped kickstart this project. The template has been significantly modified and enhanced to create the current AmiMatch application.

### Special Thanks
- **Firebase Team** - For providing an excellent backend-as-a-service platform
- **Android Team** - For the comprehensive development tools and libraries
- **Open Source Community** - For the amazing libraries and tools used in this project
- **Beta Testers** - For their valuable feedback and bug reports

### Libraries & Dependencies
- [CardStackView](https://github.com/yuyakaido/CardStackView) - Swipe card interface
- [Lottie Android](https://github.com/airbnb/lottie-android) - Beautiful animations
- [Glide](https://github.com/bumptech/glide) - Image loading and caching
- [Firebase](https://firebase.google.com/) - Backend services
- [Material Components](https://github.com/material-components/material-components-android) - UI components

## 📞 Support & Contact

For support, bug reports, or feature requests:

- 📧 Open an issue on GitHub
- 💬 Join our community discussions
- 📱 Follow the project for updates

---

<div align="center">
  <strong>Made with ❤️ for university students</strong>
  <br>
  <sub>AmiMatch - Connecting Hearts, Building Futures</sub>
</div>