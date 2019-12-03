package com.example.chat_app_youtube.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String): Parcelable {
    constructor() : this("","","")
}