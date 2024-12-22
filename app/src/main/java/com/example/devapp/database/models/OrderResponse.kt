package com.example.devapp.database.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class OrderResponse(
    val idorder: Int,
    @SerializedName("client_name")
    val clientName: String?,
    @SerializedName("status_name")
    val statusName: String?,
    val orderdate: String?,
    val price: Double
) {
    // Метод для получения отформатированной даты
    val formattedOrderDate: String
        get() {
            return if (orderdate != null) {
                try {
                    // Парсим строку даты с учетом временной зоны UTC
                    val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
                    val dateTime = ZonedDateTime.parse(orderdate, dateTimeFormatter)

                    // Переводим дату в московское время
                    val mskDateTime = dateTime.withZoneSameInstant(ZoneId.of("Europe/Moscow"))

                    // Получаем только дату в формате yyyy-MM-dd
                    mskDateTime.toLocalDate().format(DateTimeFormatter.ISO_DATE)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
            } else {
                ""
            }
        }
}