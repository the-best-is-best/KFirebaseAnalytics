package io.github.kfirebase_analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.tasks.await


actual class KFirebaseAnalytics actual constructor() {
    private val firebaseAnalytics = Firebase.analytics

    actual fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {

        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    actual fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    actual fun setUserId(value: String) {
        firebaseAnalytics.setUserId(value)
    }

    actual fun setAnalyticsCollectionEnabled(value: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(value)
    }

    actual fun setSessionTimeoutDuration(milliseconds: Long) {
        firebaseAnalytics.setSessionTimeoutDuration(milliseconds)
    }

    actual fun setDefaultEventParameters(params: Map<String, Any>) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
        firebaseAnalytics.setDefaultEventParameters(bundle)
    }

    actual fun resetAnalyticsData() {
        firebaseAnalytics.resetAnalyticsData()
    }

    actual suspend  fun getAppInstanceId():String? {
      return firebaseAnalytics.appInstanceId.await()
    }




    actual fun setUserProperties(properties: Map<String, String>) {
        properties.forEach { (key, value) ->
            firebaseAnalytics.setUserProperty(key, value)
        }
    }
}