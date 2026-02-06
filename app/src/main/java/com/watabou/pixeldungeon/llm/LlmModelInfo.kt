package com.watabou.pixeldungeon.llm

data class LlmModelInfo(
    val id: String,
    val displayName: String,
    val description: String,
    val downloadUrl: String,
    val fileSizeMb: Int,
    val fileName: String
)
