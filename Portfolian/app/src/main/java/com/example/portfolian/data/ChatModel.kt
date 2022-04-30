package com.example.portfolian.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ChatModel (val message: String, val roomId: String, val sender: String, val receiver: String, val date: LocalDateTime)