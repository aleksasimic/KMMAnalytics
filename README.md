# KMMAnalytics

KMMAnalytics is a Kotlin Multiplatform library that lets you send **Firebase Analytics events** from both iOS and Android in a unified, type-safe way.  

---

## 🚀 Features

- Single API for logging analytics events on Android (via Firebase Analytics SDK) and iOS (via Firebase for iOS).  
- Support for event parameters (integer, string).   
- Events are auto-generated, making sure the event name and event properties are unified across platforms

---

## 📦 Modules & Structure

| Module / Package | Responsibility |
|------------------|----------------|
| `library` | Core, platform-agnostic definition of event models auto-generated from Event.json file|
| `samples/ios` | Example iOS app integrating KMMAnalytics via SPM |
| `samples/android` | Example Android app integrating KMMAnalytics -> To Be Added |

---

## 📥 Installation

Add KMMAnalytics to your project via Gradle:

**1. Kotlin Multiplatform**

```kotlin
kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation("com.example:kmmanalytics:<version>")
      }
    }
  }
}
```

**2. iOS**

- Open iOS project
- Tap on `File -> Add Package Dependencies` 
- In `Search or Enter Package URL type` -> https://github.com/aleksa-aetherius/KMMAnalytics
- Tap `Add Package`

**3. Android**

- To Be Added

## 📖 Step by step guide for adding a new event

1. Add the new event in `Events.json` file. This is an example of that file content:
```
[
  {
    "name": "home_screen_viewed",
    "params": [
      { "name": "screen_name", "type": "string" },
      { "name": "timestamp", "type": "int" }
    ]
  },
  {
    "name": "purchase_completed",
    "params": [
      { "name": "product_id", "type": "string" },
      { "name": "price", "type": "int" }
    ]
  },
  {
    "name": "cart_added",
    "params": [
      { "name": "amount", "type": "int" }
    ]
  }
]
```
2. Run the `generateEvents` script inside `build.gradle.kts`
3. Observe that the events genereted inside `AnalyticsEvents.kt`:
```
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

// AUTO-GENERATED FILE. DO NOT EDIT.
fun home_screen_viewed(screen_name: String, timestamp: Int) {
    Firebase.analytics.logEvent(
        name = "home_screen_viewed",
        parameters = mapOf(
            "screen_name" to screen_name,
            "timestamp" to timestamp
        )
    )
}

fun purchase_completed(product_id: String, price: Int) {
    Firebase.analytics.logEvent(
        name = "purchase_completed",
        parameters = mapOf(
            "product_id" to product_id,
            "price" to price
        )
    )
}

fun cart_added(amount: Int) {
    Firebase.analytics.logEvent(
        name = "cart_added",
        parameters = mapOf(
            "amount" to amount
        )
    )
}
```

4. Publish the new version of the KMMAnalaytics SDK for iOS
- Generate the `XCFramework` by running -> `./gradlew assembleKMMAnalyticsXCFramework`
- ZIP(compress) the generated `XCFramework`
- Get the checksum by running -> `swift package compute-checksum KMMAnalytics.xcframework.zip`
- Upload the zipped `XCFramework` to GitHub
- Update the `Package.swift` file with the new link to the `XCFramework` and the new checksum:
```
// swift-tools-version:5.9
import PackageDescription

let package = Package(
  name: "KMMAnalytics",
  platforms: [
   .iOS(.v14),
  ],
  products: [
   .library(name: "KMMAnalytics", targets: ["KMMAnalytics"])
  ],
  targets: [
   .binaryTarget(
     name: "KMMAnalytics",
     url: "https://github.com/aleksa-aetherius/KMMAnalytics/releases/download/1.0.4/KMMAnalytics.xcframework.zip (NEW XCFRAMEWORK URL)",
     checksum:"c8d21d43490f94ecd442bad114e7684f2898d9624f757a78d13d09dc06fca885 (NEW CHECKSUM)"
    )
  ]
)
```
- Publish a new GitHub release with the new tag for `KMMAnalytics` repository
- Right click the `KMMAnalytics` package in your iOS app and tap on `Update Package`

## 📱 Usage

**1. iOS**

- Import the added KMM library to the Swfft file from which you want to trigger sending an event
`import KMMAnalytics`

- Trigger the event from the KMM library ->
```
  AnalyticsEventsKt.home_screen_viewed(
    screen_name: "home_screen",
    timestamp: Int32(20)
  )
```
**2. Android**
- To be added


