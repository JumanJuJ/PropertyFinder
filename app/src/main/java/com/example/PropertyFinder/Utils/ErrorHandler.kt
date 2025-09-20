package com.example.PropertyFinder.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException

object ErrorHandler {

    fun <T> handleResponse(context: Context, response: Response<T>, defaultMessage: String = "Errore generico"): Boolean {
        return if (response.isSuccessful) {
            true
        } else {
            val message = when (response.code()) {
                401 -> "Non autorizzato: credenziali errate"
                403 -> "Accesso negato"
                404 -> "Risorsa non trovata"
                500 -> "Errore del server"
                else -> defaultMessage
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun handleFailure(context: Context, throwable: Throwable) {
        val message = when (throwable) {
            is SocketTimeoutException -> "Timeout: il server non ha risposto in tempo"
            is ConnectException -> "Connessione rifiutata: server non raggiungibile"
            is EOFException -> "Risposta vuota: il server ha risposto male"

            else -> "Errore imprevisto: ${throwable.localizedMessage}"
        }

        Log.e("API_FAILURE", "Errore Retrofit: ${throwable::class.java.simpleName} - ${throwable.localizedMessage}", throwable)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }


    fun extractServerError(errorBody: ResponseBody?): String? {
        return try {
            errorBody?.string()?.let { raw ->
                val json = JSONObject(raw)
                json.optString("message", null)
            }
        } catch (e: Exception) {
            null
        }
    }

}