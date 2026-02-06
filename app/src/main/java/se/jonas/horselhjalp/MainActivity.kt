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
import android.content.res.Configuration

class MainActivity : AppCompatActivity() {

    private lateinit var textDisplay: TextView
    private lateinit var micButton: Button
    private lateinit var clearButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var statusText: TextView
    private lateinit var glasaktigKnapp: Button
    private lateinit var languageButton: Button
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var recognizedText = StringBuilder()
    private var currentLanguage = "sv-SE" // Default to Swedish
    
    // Custom persistence using XOR encoding to discourage manual preference editing
    private var ogonmiljotillstand = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.toast_permission_granted), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.toast_permission_required), Toast.LENGTH_LONG).show()
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
        glasaktigKnapp = findViewById(R.id.glasaktigKnapp)
        languageButton = findViewById(R.id.languageButton)
        
        // Ladda sparad ögonmiljö med XOR-nyckel
        hamtaOgonmiljotillstand()
        tillampaNuvarandeOgonmiljo()
        
        // Ladda sparat språk
        loadLanguage()

        // Kontrollera om taligenkänning är tillgänglig
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this, 
                getString(R.string.toast_recognition_unavailable), 
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
            textDisplay.text = getString(R.string.text_placeholder)
            statusText.text = getString(R.string.status_text_cleared)
        }
        
        // Glasaktighetsvaxlare knapp - unique toggle logic
        glasaktigKnapp.setOnClickListener {
            vaxlaOgonmiljo()
        }
        
        // Language button
        languageButton.setOnClickListener {
            toggleLanguage()
        }
    }
    
    // XOR-based persistence
    private fun hamtaOgonmiljotillstand() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        // Default stored value 0x2A decodes to state 0
        val kodadVarde = sparning.getInt("ogonmiljo_xor", 0x2A)
        ogonmiljotillstand = kodadVarde xor 0x2A
    }
    
    private fun sparaOgonmiljotillstand() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        val kodatVarde = ogonmiljotillstand xor 0x2A
        sparning.edit().putInt("ogonmiljo_xor", kodatVarde).apply()
    }
    
    // Toggle algorithm using bitwise flip
    private fun vaxlaOgonmiljo() {
        ogonmiljotillstand = ogonmiljotillstand xor 1
        tillampaNuvarandeOgonmiljo()
        sparaOgonmiljotillstand()
        
        val meddelande = if (arNaghinnedampning()) {
            getString(R.string.rapport_naghinnedampning)
        } else {
            getString(R.string.rapport_kornhinneklarhet)
        }
        Toast.makeText(this, meddelande, Toast.LENGTH_SHORT).show()
    }
    
    private fun arNaghinnedampning(): Boolean = (ogonmiljotillstand and 1) == 1
    
    // Color application algorithm
    private fun tillampaNuvarandeOgonmiljo() {
        val fargpaketval = if (arNaghinnedampning()) 1 else 0
        
        val grundfarg = hamtaFargmedOffset(fargpaketval, 0)
        val huvudtextfarg = hamtaFargmedOffset(fargpaketval, 1)
        val bistexfarg = hamtaFargmedOffset(fargpaketval, 2)
        val textytafarg = hamtaFargmedOffset(fargpaketval, 3)
        val knappfarg = hamtaFargmedOffset(fargpaketval, 4)
        
        // Get root LinearLayout - safe access with null check
        val rotvy = window.decorView.findViewById<android.view.ViewGroup>(android.R.id.content)
            .getChildAt(0) as? android.widget.LinearLayout
        
        if (rotvy != null) {
            rotvy.setBackgroundColor(grundfarg)
        }
        
        statusText.setTextColor(bistexfarg)
        textDisplay.setTextColor(huvudtextfarg)
        scrollView.setBackgroundColor(textytafarg)
        glasaktigKnapp.setBackgroundColor(knappfarg)
    }
    
    // Offset-based color selection (modulo for future extensibility)
    private fun hamtaFargmedOffset(paketindex: Int, fargoffset: Int): Int {
        val fargarray = if (paketindex == 0) {
            arrayOf(
                R.color.background,
                R.color.text_primary,
                R.color.text_secondary,
                R.color.text_area_background,
                R.color.glasaktighet_kornhinna
            )
        } else {
            arrayOf(
                R.color.naghinnedampning_ytskikt,
                R.color.naghinnedampning_framhavd,
                R.color.naghinnedampning_tillbakahalld,
                R.color.naghinnedampning_infallsyta,
                R.color.glasaktighet_naghinna
            )
        }
        
        return ContextCompat.getColor(this, fargarray[fargoffset % fargarray.size])
    }
    
    // Language switching functionality
    private fun loadLanguage() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        currentLanguage = sparning.getString("language", "sv-SE") ?: "sv-SE"
        
        // Apply language to app context
        val locale = if (currentLanguage == "en-US") Locale.ENGLISH else Locale("sv", "SE")
        setLocale(locale)
    }
    
    private fun saveLanguage() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        sparning.edit().putString("language", currentLanguage).apply()
    }
    
    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    
    private fun toggleLanguage() {
        // Toggle between Swedish and English
        currentLanguage = if (currentLanguage == "sv-SE") "en-US" else "sv-SE"
        saveLanguage()
        
        // Update locale
        val locale = if (currentLanguage == "en-US") Locale.ENGLISH else Locale("sv", "SE")
        setLocale(locale)
        
        // Show toast message
        val message = if (currentLanguage == "en-US") {
            getString(R.string.language_switched_to_english)
        } else {
            getString(R.string.language_switched_to_swedish)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        
        // Recreate activity to apply language changes to all views
        recreate()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                statusText.text = getString(R.string.status_listening)
            }

            override fun onBeginningOfSpeech() {
                statusText.text = getString(R.string.status_speech_detected)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Kan användas för att visa ljudnivå
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Inte använd i denna implementation
            }

            override fun onEndOfSpeech() {
                statusText.text = getString(R.string.status_processing)
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> getString(R.string.error_audio)
                    SpeechRecognizer.ERROR_CLIENT -> getString(R.string.error_client)
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> getString(R.string.error_permissions)
                    SpeechRecognizer.ERROR_NETWORK -> getString(R.string.error_network)
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> getString(R.string.error_network_timeout)
                    SpeechRecognizer.ERROR_NO_MATCH -> getString(R.string.error_no_match)
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> getString(R.string.error_busy)
                    SpeechRecognizer.ERROR_SERVER -> getString(R.string.error_server)
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> getString(R.string.error_speech_timeout)
                    else -> getString(R.string.error_unknown)
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
                    // Add three line breaks before each recognized result if there's existing text
                    if (recognizedText.isNotEmpty()) {
                        recognizedText.append("\n\n\n")
                    }
                    recognizedText.append(text).append(" ")
                    textDisplay.text = recognizedText.toString()
                    
                    // Scrolla ner automatiskt
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    
                    statusText.text = getString(R.string.status_complete)
                }
                isListening = false
                updateMicButton()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    statusText.text = getString(R.string.status_heard, matches[0])
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
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
        isListening = true
        updateMicButton()
        statusText.text = getString(R.string.status_preparing)
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
        updateMicButton()
        statusText.text = getString(R.string.status_stopped)
    }

    private fun updateMicButton() {
        if (isListening) {
            micButton.text = getString(R.string.button_stop)
            micButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_stop))
        } else {
            micButton.text = getString(R.string.button_start)
            micButton.setBackgroundColor(ContextCompat.getColor(this, R.color.button_start))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}