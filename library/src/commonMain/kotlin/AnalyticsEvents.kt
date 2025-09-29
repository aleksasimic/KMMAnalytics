package io.github.kotlin.fibonacci

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

// AUTO-GENERATED FILE. DO NOT EDIT.
fun home_screen_viewed(screen_name: String, timestamp: Int) {
    Firebase.analytics.logEvent(
        name = "home_screen_viewed",
        parameters = mapOf(
            "screen_name" to screen_name,
            "timestamp" to timestamp
        )
    )
}

fun purchase_completed(product_id: String, price: Int) {
    Firebase.analytics.logEvent(
        name = "purchase_completed",
        parameters = mapOf(
            "product_id" to product_id,
            "price" to price
        )
    )
}

fun cart_added(amount: Int) {
    Firebase.analytics.logEvent(
        name = "cart_added",
        parameters = mapOf(
            "amount" to amount
        )
    )
}

