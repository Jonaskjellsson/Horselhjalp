package se.jonas.horselhjalp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var textDisplay: TextView
    private lateinit var micButton: Button
    private lateinit var clearButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var statusText: TextView
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var recognizedText = StringBuilder()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Mikrofon-tillstånd beviljat", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Mikrofon-tillstånd krävs för att använda denna app", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hitta vyer
        textDisplay = findViewById(R.id.textDisplay)
        micButton = findViewById(R.id.micButton)
        clearButton = findViewById(R.id.clearButton)
        scrollView = findViewById(R.id.scrollView)
        statusText = findViewById(R.id.statusText)

        // Kontrollera om taligenkänning är tillgänglig
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this, 
                "Taligenkänning är inte tillgänglig på denna enhet", 
                Toast.LENGTH_LONG
            ).show()
            micButton.isEnabled = false
            return
        }

        // Be om mikrofon-tillstånd
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        // Initiera taligenkänning
        setupSpeechRecognizer()

        // Mikrofon-knapp
        micButton.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        }

        // Radera-knapp
        clearButton.setOnClickListener {
            recognizedText.clear()
            textDisplay.text = ""
            statusText.text = "Text raderad"
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                statusText.text = "Lyssnar..."
            }

            override fun onBeginningOfSpeech() {
                statusText.text = "Tal upptäckt"
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Kan användas för att visa ljudnivå
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Inte använd i denna implementation
            }

            override fun onEndOfSpeech() {
                statusText.text = "Bearbetar..."
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Ljudfel"
                    SpeechRecognizer.ERROR_CLIENT -> "Klientfel"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Saknar behörigheter"
                    SpeechRecognizer.ERROR_NETWORK -> "Nätverksfel"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Nätverkstimeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Inget tal hittades"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Igenkännaren är upptagen"
                    SpeechRecognizer.ERROR_SERVER -> "Serverfel"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Inget tal upptäckt"
                    else -> "Okänt fel"
                }
                statusText.text = errorMessage
                isListening = false
                updateMicButton()
                
                // Starta om automatiskt efter "inget tal"-fel
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || 
                    error == SpeechRecognizer.ERROR_NO_MATCH) {
                    startListening()
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    recognizedText.append(text).append(" ")
                    textDisplay.text = recognizedText.toString()
                    
                    // Scrolla ner automatiskt
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    
                    statusText.text = "Klart! Tryck på mikrofonen för att fortsätta"
                }
                isListening = false
                updateMicButton()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    statusText.text = "Hörde: ${matches[0]}"
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Inte använd i denna implementation
            }
        })
    }

    private fun startListening() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sv-SE")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
        isListening = true
        updateMicButton()
        statusText.text = "Förbereder..."
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
        updateMicButton()
        statusText.text = "Stoppad"
    }

    private fun updateMicButton() {
        if (isListening) {
            micButton.text = "🛑 STOPPA"
            micButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_stop))
        } else {
            micButton.text = "🎤 STARTA TAL"
            micButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_start))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}