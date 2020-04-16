package com.example.suntracker.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SunInfoResponse {
    @SerializedName("results")
    @Expose
    var results: Results? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

}