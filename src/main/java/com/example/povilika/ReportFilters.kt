// ReportFilters.kt
package com.example.povilika

import java.io.Serializable

data class ReportFilters(
    var date: String = "",
    var activityType: String = "",
    var workType: String = "",
    var workPlace: String = "",
    var region: String = "",
    var district: String = ""
) : Serializable

