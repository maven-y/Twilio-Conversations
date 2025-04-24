package com.twilio.conversations.app.data.model

data class PointsPackage(
    val id: String,
    val name: String,
    val points: Int,
    val price: Double,
    val razorpayProductId: String
) {
    companion object {
        val packages = listOf(
            PointsPackage(
                id = "points_100",
                name = "100 Points",
                points = 100,
                price = 99.0,
                razorpayProductId = "prod_points_100"
            ),
            PointsPackage(
                id = "points_500",
                name = "500 Points",
                points = 500,
                price = 399.0,
                razorpayProductId = "prod_points_500"
            ),
            PointsPackage(
                id = "points_1000",
                name = "1000 Points",
                points = 1000,
                price = 699.0,
                razorpayProductId = "prod_points_1000"
            )
        )
    }
} 