package com.example.PropertyFinder.Utils

fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}
fun isPasswordValid(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
    return passwordRegex.matches(password)
}

fun stringParser(input: String): Boolean {
    val trimmed = input.trim()
    return trimmed.isNotEmpty() && trimmed[0].isDigit()
}