package com.my.first.app_contactos.utils
fun formatPhoneNumberWithCountryCode(phone: String, defaultCountryCode: String = "+506"): String {
    val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
    if (cleanPhone.isBlank()) return ""
    return if (cleanPhone.startsWith("+")) {
        cleanPhone
    } else {
        "$defaultCountryCode$cleanPhone"
    }
}
