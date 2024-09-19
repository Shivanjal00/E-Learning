package com.app.tensquare.ui.notification

data class NotificationResponse(
    val `data`: NotificationList,
    val message: String,
    val status: String
)

data class NotificationList(
    val count: Int,
    val length: Int,
    val list: List<NotificationData>,
    val pageNo: Int
)

data class NotificationData(
    val createdAt: String,
    val description: String,
    val eventId: String,
    val eventName: String,
    val id: String,
    val image: String,
    val payloadType: String,
    val payloadURL: String,
    val readStatus: Int,
    val title: String,
    val userId: String,
    val isExpanded: Boolean

)