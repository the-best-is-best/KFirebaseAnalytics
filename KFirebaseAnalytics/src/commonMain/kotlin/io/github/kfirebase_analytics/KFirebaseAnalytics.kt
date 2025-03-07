package io.github.kfirebase_analytics

expect class KFirebaseAnalytics() {
    fun logEvent(eventName: String, params: Map<String, Any>)
    fun setUserProperty(name: String, value: String)
    fun setUserId(value: String)
    fun setAnalyticsCollectionEnabled(value: Boolean)
    fun setSessionTimeoutDuration(milliseconds: Long)
    fun setDefaultEventParameters(params: Map<String, Any>)
    fun resetAnalyticsData()
    suspend fun  getAppInstanceId():String?
    fun setUserProperties(properties: Map<String, String>)

}