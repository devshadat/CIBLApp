package com.devshadat.ciblapp.models

data class Receipt(
    var title: String,
    var imgId: Int,
    var name: String,
    var number: String,
    var amount: String,
    var narration: String,
    var location: String,
    var time: String
)
