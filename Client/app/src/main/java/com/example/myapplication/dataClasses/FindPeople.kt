package com.example.myapplication.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class FindPeople(
    val type : String,
    val typeAction : String,
    val tagUserFriend : String
)
