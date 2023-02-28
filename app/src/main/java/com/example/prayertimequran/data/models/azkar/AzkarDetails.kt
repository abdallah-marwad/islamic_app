package com.example.prayertimequran.data.models.azkar

class AzkarDetails() {
    var zekr: String? = null
    var category: String? = null
    var description: String? = null
    var reference: String? = null
    var count: String? = null

    constructor(
        zekr: String?,
        category: String?,
        description: String?,
        reference: String?,
        count: String?,

        ) : this() {
        this.zekr = zekr
        this.category = category
        this.description = description
        this.reference = reference
        this.count = count
    }
}