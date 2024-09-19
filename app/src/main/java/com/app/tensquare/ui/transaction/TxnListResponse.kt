package com.app.tensquare.ui.transaction

data class TxnListResponse(
    val `data`: TxnData,
    val message: String,
    val status: String
)

data class TxnData(
    val length: Int,
    val pageNo: Int,
    val transactions: List<Transaction>
)

data class Transaction(
    val _id: String,
    val amount: String,
    val createdAt: String,
    val paymentFor: Int,
    val paymentMethod: Int,
    val paymentType: Int
)