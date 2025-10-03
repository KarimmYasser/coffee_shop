# ☕ Coffee Shop Android App

A modern Android coffee shop application built with Kotlin, following Clean Architecture principles and using the latest Android development best practices.

## 📱 App Overview

Coffee Shop is a beautifully designed mobile application that allows users to browse coffee menus, add favorites, place orders, and manage their coffee preferences. The app features a rich user interface with smooth navigation and offline capabilities.

## ✨ Features

### 🏠 Core Features
- **Home Screen**: Browse featured coffees with attractive cards and promotional banners
- **Drink Menu**: Complete catalog of hot and iced coffee beverages
- **Favorites**: Save and manage favorite coffee drinks
- **Order Management**: Place orders, view order history (recent and past orders)
- **User Profile**: Personalized experience with username management
- **Search Functionality**: Find specific coffee drinks quickly
- **Offline Support**: Browse cached coffee data when offline

### 🎨 UI/UX Features
- **Modern Material Design**: Clean, intuitive interface with coffee-themed aesthetics
- **Bottom Navigation**: Easy navigation between main sections
- **Custom Components**: Reusable UI components for consistent design
- **Loading States**: Smooth loading animations and error handling
- **Network Awareness**: Automatic network status monitoring
- **Splash Screen**: Branded app launch experience

## 🏗️ Architecture

This app follows **Clean Architecture** principles with clear separation of concerns:

```
📁 app/src/main/java/com/example/cofee_shop/
├── 📁 adapter/                    # RecyclerView adapters
├── 📁 core/                       # Core utilities and constants
├── 📁 data/                       # Data layer
│   ├── 📁 local/                  # Room database (offline storage)
│   ├── 📁 mappers/                # Data mapping between layers
│   ├── 📁 models/                 # Data models and DTOs
│   ├── 📁 remote/                 # API services and networking
│   └── 📁 repositories/           # Repository implementations
├── 📁 domain/                     # Business logic layer
│   ├── 📁 models/                 # Domain models
│   ├── 📁 repositories/           # Repository interfaces
│   └── 📁 usecases/               # Business use cases
├── 📁 presentation/               # UI layer
│   ├── 📁 activities/             # Activities (MainActivity, SplashActivity, etc.)
│   ├── 📁 fragments/              # Fragments for each screen
│   └── 📁 managers/               # ViewModels
└── CoffeeShopApplication.kt       # Application class with Hilt setup
```

## 🛠️ Technology Stack

### **Languages & Frameworks**
- **Kotlin** - Primary programming language
- **Android SDK** (API 28-36)
- **Kotlin Coroutines** - Asynchronous programming
- **Kotlinx Serialization** - JSON serialization

### **Architecture Components**
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Hilt (Dagger)** - Dependency injection
- **Navigation Component** - Fragment navigation with Safe Args
- **ViewBinding & DataBinding** - Type-safe view binding
- **LiveData & Flow** - Reactive data streams

### **UI/UX Libraries**
- **Material Design Components** - Modern UI components
- **ConstraintLayout** - Flexible layouts
- **RecyclerView** - Efficient list displays
- **ViewPager2** - Swipeable content
- **Core SplashScreen** - Native splash screen API
- **Glide & Coil** - Image loading and caching

### **Data & Networking**
- **Room Database** - Local data persistence
- **Retrofit** - HTTP client for API calls
- **OkHttp** - HTTP client with logging interceptor
- **Gson** - JSON parsing

### **Build Tools**
- **Gradle Kotlin DSL** - Build configuration
- **KSP (Kotlin Symbol Processing)** - Annotation processing
- **Version Catalogs** - Centralized dependency management

## 📋 Prerequisites

- **Android Studio** Arctic Fox (2020.3.1) or later
- **JDK 11** or higher
- **Android SDK** with API level 28-36
- **Kotlin 2.0.21** or compatible version

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd coffee_shop-master
```

### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an existing project"
- Navigate to the cloned directory and select it
- Wait for Gradle sync to complete

### 3. Configure API (Optional)
The app uses the [Sample APIs Coffee API](https://api.sampleapis.com/):
- **Hot Coffee**: `https://api.sampleapis.com/coffee/hot`
- **Iced Coffee**: `https://api.sampleapis.com/coffee/iced`

No API key required - the endpoints are publicly accessible.

### 4. Build and Run
```bash
# Build the project
./gradlew build

# Install on device/emulator
./gradlew installDebug
```

## 📱 App Structure

### **Activities**
- **SplashActivity**: App launch screen with branding
- **UsernameActivity**: Initial user setup for personalization  
- **MainActivity**: Main container with bottom navigation

### **Fragments**
- **HomeFragment**: Featured coffees and promotions
- **DrinkMenuFragment**: Complete coffee catalog with categories
- **FavoritesFragment**: User's saved favorite drinks
- **OrderFragment**: Order history and management
- **CoffeeDetailFragment**: Detailed coffee information
- **PaymentFragment**: Order checkout process

### **Key Components**
- **CoffeeAdapter**: Displays coffee items in lists
- **HomeCoffeeAdapter**: Specialized adapter for home screen
- **OrdersAdapter**: Manages order display
- **FavouriteAdapter**: Handles favorite coffee items

## 🔧 Configuration

### **Application Configuration**
- **Package Name**: `com.example.cofee_shop`
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36 (Android 14)
- **Version**: 1.0

### **Build Variants**
- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

### **Permissions**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🎯 Key Features Detail

### **Offline Capability**
- Room database for local data persistence
- Automatic caching of API responses
- Graceful offline mode with cached data
- Network status monitoring and user feedback

### **User Experience**
- Smooth bottom navigation with custom styling
- Pull-to-refresh functionality
- Loading states and error handling
- Search functionality with real-time filtering
- Favorite management with persistent storage

### **Performance Optimizations**
- Image loading with Glide/Coil caching
- Efficient RecyclerView implementations
- Coroutine-based async operations
- ProGuard optimization for release builds

## 🧪 Testing

The project includes test infrastructure:
```bash
# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

## 📦 Dependencies

Key dependencies (see `gradle/libs.versions.toml` for complete list):

```toml
[versions]
kotlin = "2.0.21"
hilt = "2.51.1"
room = "2.7.2"
retrofit = "2.9.0"
navigation = "2.9.3"
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is for educational and demonstration purposes. See the LICENSE file for details.

## 🐛 Known Issues

- Minor UI adjustments needed for different screen sizes
- API error handling could be enhanced for better user feedback
- Some placeholder data used for demonstration purposes

## 📞 Support

For questions or support, please open an issue in the GitHub repository.

---

**Built with ❤️ and ☕ for coffee lovers everywhere!**
