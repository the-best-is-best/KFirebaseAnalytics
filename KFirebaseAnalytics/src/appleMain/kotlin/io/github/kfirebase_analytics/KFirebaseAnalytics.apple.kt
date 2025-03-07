package io.github.kfirebase_analytics

import io.github.native.kfirebaseanalytics.FIRAnalytics
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class KFirebaseAnalytics actual constructor() {
    actual fun logEvent(
        eventName: String,
        params: Map<String, Any>
    ) {
        val paramsMapAny = convertMap(params)

        FIRAnalytics.logEventWithName(eventName, parameters = paramsMapAny)


    }

    actual fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(name, value)
    }

    actual fun setUserId(value: String) {
        FIRAnalytics.setUserID(value)
    }

    actual fun setAnalyticsCollectionEnabled(value: Boolean) {
        FIRAnalytics.setAnalyticsCollectionEnabled(value)
    }

    actual fun setSessionTimeoutDuration(milliseconds: Long) {
        FIRAnalytics.setSessionTimeoutInterval(milliseconds / 1000.0)
    }

    actual fun setDefaultEventParameters(params: Map<String, Any>) {
        val paramsMapAny = convertMap(params)

        FIRAnalytics.setDefaultEventParameters(paramsMapAny)
    }

    actual fun resetAnalyticsData() {
        FIRAnalytics.resetAnalyticsData()
    }

    actual suspend fun getAppInstanceId():String? {
        return FIRAnalytics.appInstanceID()
    }



    actual fun setUserProperties(properties: Map<String, String>) {
        properties.forEach { (key, value) ->
            FIRAnalytics.setUserPropertyString(value, forName = key)
        }

    }
}

private fun convertMap(params: Map<String, Any>): Map<Any?, *> {
    return params.map { (key, value) ->
        key to value
    }.toMap()
}