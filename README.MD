<h1 align="center">KFirebaseAnalytics</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

<br>

### KFirebaseAnalytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Analytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-analytics)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-analytics)

KFirebaseAnalytics is available on `mavenCentral()`.

## Installation

```kotlin
implementation("io.github.the-best-is-best:kfirebase-analytics:1.2.0")
```

### androidMain

```kotlin
     AndroidKFirebaseCore.initialization(this)
```

### iOS (Using Swift Package Manager - SPM)

Make sure to add Firebase as a dependency using Swift Package Manager (SPM) instead of CocoaPods.

1. Open your Xcode project.
2. Navigate to `File > Add Packages...`.
3. Enter Firebase repository URL: `https://github.com/firebase/firebase-ios-sdk`
4. Choose `FirebaseCore` and add it to your project.

### iosApp

```swift
import Firebase

FirebaseApp.configure()
```

## Usage

```kotlin
KFirebaseAnalytics().logEvent("test1", mapOf("test1" to 1, "test2" to 2))
```

## API

```kotlin
package io.github.kfirebase_analytics

expect class KFirebaseAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun setUserProperty(name: String, value: String)
    fun setUserId(value: String)
    fun setAnalyticsCollectionEnabled(value: Boolean)
    fun setSessionTimeoutDuration(milliseconds: Long)
    fun setDefaultEventParameters(params: Map<String, Any>)
    fun resetAnalyticsData()
    suspend fun getAppInstanceId(): String?
    fun setUserProperties(properties: Map<String, String>)
}
```
