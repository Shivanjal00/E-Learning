package com.app.tensquare.ui.instruction

data class InstructionResponse(
    val `data`: InstructionData,
    val message: String,
    val status: String
)

data class InstructionData(
    val _id: String,
    val instructionType: Int,
    val text: String,
    val paragraphs: List<String>,
    val languageId: String
)
