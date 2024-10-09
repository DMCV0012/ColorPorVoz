package com.example.microfono

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val REQUEST_RECORD_PERMISSION = 100
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tvResultado: TextView
    private lateinit var tvCodigoColor: TextView
    private lateinit var btnGrabar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las referencias a los elementos del layout
        btnGrabar = findViewById(R.id.btnGrabar)
        tvResultado = findViewById(R.id.tvResultado)
        tvCodigoColor = findViewById(R.id.tvCodigoColor)  // TextView para el código del color

        // Inicializa el reconocimiento de voz
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        // Definir el reconocimiento
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        // Listener para el evento de reconocimiento de voz
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                Toast.makeText(this@MainActivity, "Escuchando...", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray) {}

            override fun onEndOfSpeech() {
                Toast.makeText(this@MainActivity, "Procesando...", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "Error al reconocer la voz", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val textoReconocido = matches[0]

                    // Lógica para detectar y mostrar el color
                    val colorCode = obtenerCodigoColor(textoReconocido)
                    if (colorCode != null) {
                        tvCodigoColor.text = colorCode  // Mostrar el código del color
                        tvCodigoColor.setTextColor(Color.parseColor(colorCode))  // Cambiar color del texto

                        // Usar GradientDrawable para cambiar el color manteniendo la forma circular
                        val background = tvResultado.background as GradientDrawable
                        background.setColor(Color.parseColor(colorCode))  // Cambiar color del fondo manteniendo la forma
                    } else {
                        tvCodigoColor.text = "No se detectó color"
                        tvResultado.setBackgroundColor(Color.TRANSPARENT)  // Quitar color de fondo si no se detecta
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle) {}

            override fun onEvent(eventType: Int, params: Bundle) {}
        })

        // Cuando se presiona el botón
        btnGrabar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Solicitar permiso de grabación en tiempo de ejecución
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_PERMISSION)
            } else {
                // Iniciar el reconocimiento de voz
                speechRecognizer.startListening(intent)
            }
        }
    }

    // Método para obtener el código de color del texto reconocido
    private fun obtenerCodigoColor(texto: String): String? {
        // Mapa de colores extendido
        val coloresMap = mapOf(
            "rojo" to "#FF0000",
            "celeste" to "#80BFFF",
            "verde" to "#008000",
            "azul" to "#0000FF",
            "negro" to "#000000",
            "blanco" to "#FFFFFF",
            "naranja" to "#FFA500",
            "morado" to "#6F00FF",
            "lila" to "#6F00FF",
            "purpura" to "#800080",
            "marron" to "#800000",
            "fucsia" to "#FF00FF",
            "lima" to "#00FF00",
            "plomo" to "#555555",
            "plateado" to "#C0C0C0",
            "amarillo" to "#FFFF00",
            "gris" to "#808080",
            "turquesa" to "#40E0D0",
            "rosa" to "#FFC0CB",
            "vino" to "#8B0000",
            "coral" to "#FF7F50",
            "dorado" to "#FFD700",
            "esmeralda" to "#50C878",
            "lavanda" to "#E6E6FA",
            "oliva" to "#808000",
            "salmon" to "#FA8072",
            "cian" to "#00FFFF",
            "beige" to "#F5F5DC",
            "crema" to "#FFFDD0",
            "chocolate" to "#D2691E",
            "menta" to "#98FF98",
            "marfil" to "#FFFFF0",
            "caramelo" to "#A0522D",
            "azul marino" to "#000080",
            "verde esmeralda" to "#50C878",
            "verde oliva" to "#6B8E23",
            "rojo oscuro" to "#8B0000",
            "anaranjado" to "#FF4500",
            "violeta" to "#8A2BE2",
            "verde limón" to "#ADFF2F"
        )

        // Buscar la coincidencia de un color en el texto
        for ((color, codigo) in coloresMap) {
            if (texto.contains(color, ignoreCase = true)) {
                return codigo
            }
        }

        // Verificar si el texto tiene un código hexadecimal de color
        val regex = Regex("#[0-9a-fA-F]{6}")
        val match = regex.find(texto)
        return match?.value
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permiso otorgado, iniciar el reconocimiento de voz
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            speechRecognizer.startListening(intent)
        } else {
            Toast.makeText(this, "Permiso de grabación no otorgado", Toast.LENGTH_SHORT).show()
        }
    }
}
