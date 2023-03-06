package com.example.prayertimequran.data.models.azkar

class AzkarDetails() {
    var zekr: String = ""
    var category: String= ""
    var description: String= ""
    var reference: String= ""
    var count: String= ""

    constructor(
        zekr: String,
        category: String,
        description: String,
        reference: String,
        count: String,

        ) : this() {
        this.zekr = zekr
        this.category = category
        this.description = description
        this.reference = reference
        this.count = count
    }
}