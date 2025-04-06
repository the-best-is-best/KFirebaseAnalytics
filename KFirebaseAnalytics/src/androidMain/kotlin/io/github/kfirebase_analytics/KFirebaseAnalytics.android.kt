package io.github.kfirebase_analytics

import android.content.Context
import android.os.Bundle
import androidx.startup.Initializer
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.tasks.await

internal lateinit var applicationContext: Context

class ApplicationContextInitializer : Initializer<Context> {
    override fun create(context: Context): Context = context.also {
        applicationContext = it.applicationContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

actual class KFirebaseAnalytics actual constructor() {
    private val firebaseAnalytics =
        FirebaseAnalytics.getInstance(applicationContext)

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