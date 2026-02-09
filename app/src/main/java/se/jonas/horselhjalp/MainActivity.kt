package se.jonas.horselhjalp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {

    private lateinit var textDisplay: EditText
    private lateinit var micButton: Button
    private lateinit var clearButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var statusText: TextView
    private lateinit var glasaktigKnapp: Button
    private lateinit var languageButton: Button
    private lateinit var fontSizeButton: Button
    
    private var speechRecognizer: SpeechRecognizer? = null
    @Volatile private var isListening = false
    private var recognizedText = StringBuilder()
    private var currentLanguage = "sv-SE" // Default to Swedish
    @Volatile private var isDestroyed = false // Flag to track if activity is being destroyed
    private var isNewSession = false // Flag to track if this is a new recording session after manual stop
    private var lastPartialText = "" // Track last partial text to avoid redundant updates
    
    // Custom persistence using XOR encoding to discourage manual preference editing
    private var ogonmiljotillstand = 0
    private var textstorleksindex = 1 // Default to medium (32sp), index 0-3 for 24sp, 32sp, 40sp, 48sp
    
    companion object {
        // Font size options in sp
        private val FONT_SIZES = arrayOf(24f, 32f, 40f, 48f)
        
        // Compiled regex for performance optimization
        private val MULTIPLE_SPACES_REGEX = Regex(" +")
        private val NEWLINE_WITH_SPACE_REGEX = Regex("\n ")
        private val MULTIPLE_NEWLINES_REGEX = Regex("\n{4,}")  // Replace 4+ newlines with 3 (two empty lines max)
    }

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
        
        // Apply Material 3 dynamic colors (Android 12+)
        // This enables theming based on the user's wallpaper
        DynamicColors.applyToActivityIfAvailable(this)
        
        setContentView(R.layout.activity_main)

        // Hitta vyer
        textDisplay = findViewById(R.id.textDisplay)
        micButton = findViewById(R.id.micButton)
        clearButton = findViewById(R.id.clearButton)
        scrollView = findViewById(R.id.scrollView)
        statusText = findViewById(R.id.statusText)
        glasaktigKnapp = findViewById(R.id.glasaktigKnapp)
        languageButton = findViewById(R.id.languageButton)
        fontSizeButton = findViewById(R.id.fontSizeButton)
        
        // Make textDisplay non-editable but selectable for copying
        textDisplay.isFocusable = false
        textDisplay.isFocusableInTouchMode = false
        textDisplay.isClickable = true
        textDisplay.isLongClickable = true
        textDisplay.setTextIsSelectable(true)
        
        // Ladda sparad ögonmiljö med XOR-nyckel
        hamtaOgonmiljotillstand()
        tillampaNuvarandeOgonmiljo()
        
        // Ladda sparad textstorlek
        hamtaTextstorleksindex()
        tillampaNuvarandeTextstorlek()
        
        // Ladda sparat språk
        loadLanguage()
        
        // Update language button text to show current language
        updateLanguageButtonText()

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
            textDisplay.setText(getString(R.string.text_placeholder))
            statusText.text = getString(R.string.status_text_cleared)
            isNewSession = false // Reset new session flag
        }
        
        // Glasaktighetsvaxlare knapp - unique toggle logic
        glasaktigKnapp.setOnClickListener {
            vaxlaOgonmiljo()
        }
        
        // Language button
        languageButton.setOnClickListener {
            toggleLanguage()
        }
        
        // Font size button
        fontSizeButton.setOnClickListener {
            vaxlaTextstorlek()
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
        glasaktigKnapp.backgroundTintList = ContextCompat.getColorStateList(this, 
            if (arNaghinnedampning()) R.color.glasaktighet_naghinna else R.color.glasaktighet_kornhinna)
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
    private fun getLocaleFromLanguage(language: String): Locale {
        return if (language == "en-US") Locale.ENGLISH else Locale("sv", "SE")
    }
    
    private fun loadLanguage() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        currentLanguage = sparning.getString("language", "sv-SE") ?: "sv-SE"
        
        // Set default locale for the app
        val locale = getLocaleFromLanguage(currentLanguage)
        Locale.setDefault(locale)
    }
    
    private fun saveLanguage() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        sparning.edit().putString("language", currentLanguage).apply()
    }
    
    private fun updateLanguageButtonText() {
        // Update button text to show current language
        val buttonText = if (currentLanguage == "sv-SE") {
            getString(R.string.language_button_swedish)
        } else {
            getString(R.string.language_button_english)
        }
        languageButton.text = buttonText
    }
    
    private fun toggleLanguage() {
        // Toggle between Swedish and English
        val newLanguage = if (currentLanguage == "sv-SE") "en-US" else "sv-SE"
        
        // Show toast message in the target language before switching
        // We use hardcoded strings here because we want to show the message
        // in the NEW language, not the current one
        val message = if (newLanguage == "en-US") {
            "Language switched to English"  // English message when switching to English
        } else {
            "Språk bytt till Svenska"  // Swedish message when switching to Swedish
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        
        // Save and apply new language
        currentLanguage = newLanguage
        saveLanguage()
        
        // Update locale
        val locale = getLocaleFromLanguage(currentLanguage)
        Locale.setDefault(locale)
        
        // Recreate activity to apply language changes to all views
        recreate()
    }
    
    // Font size functionality
    private fun hamtaTextstorleksindex() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        // Default stored value 0x2B (which XORs with 0x2A to give index 1 = 32sp)
        val kodadVarde = sparning.getInt("textstorlek_xor", 0x2B)
        textstorleksindex = kodadVarde xor 0x2A
        // Ensure index is valid (0-3)
        if (textstorleksindex !in 0..3) {
            textstorleksindex = 1
        }
    }
    
    private fun sparaTextstorleksindex() {
        val sparning = getSharedPreferences("horsel_interna", MODE_PRIVATE)
        val kodatVarde = textstorleksindex xor 0x2A
        sparning.edit().putInt("textstorlek_xor", kodatVarde).apply()
    }
    
    private fun vaxlaTextstorlek() {
        // Cycle through font sizes: 0 -> 1 -> 2 -> 3 -> 0
        textstorleksindex = (textstorleksindex + 1) % FONT_SIZES.size
        tillampaNuvarandeTextstorlek()
        sparaTextstorleksindex()
        
        // hamtaTextstorleksindex ensures textstorleksindex is always 0-3
        val meddelande = when (textstorleksindex) {
            0 -> getString(R.string.font_size_small)
            1 -> getString(R.string.font_size_medium)
            2 -> getString(R.string.font_size_large)
            else -> getString(R.string.font_size_extra_large) // index 3
        }
        Toast.makeText(this, meddelande, Toast.LENGTH_SHORT).show()
    }
    
    private fun tillampaNuvarandeTextstorlek() {
        val textSize = FONT_SIZES[textstorleksindex]
        textDisplay.textSize = textSize
    }


    
    private fun setupSpeechRecognizer() {
        // Clean up any existing recognizer first
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            // Ignore exceptions during cleanup
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                if (isDestroyed) return
                statusText.text = getString(R.string.status_listening)
            }

            override fun onBeginningOfSpeech() {
                if (isDestroyed) return
                statusText.text = getString(R.string.status_speech_detected)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // No auto-pause logic needed
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Inte använd i denna implementation
            }

            override fun onEndOfSpeech() {
                if (isDestroyed) return
                statusText.text = getString(R.string.status_processing)
            }

            override fun onError(error: Int) {
                if (isDestroyed) return
                
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
            }

            override fun onResults(results: Bundle?) {
                if (isDestroyed) return
                
                // Get final results from Bundle
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val newText = matches?.getOrNull(0)?.replace(MULTIPLE_SPACES_REGEX, " ")?.trim()
                
                if (newText != null && newText.isNotEmpty()) {
                    if (recognizedText.isNotEmpty()) {
                        if (isNewSession) {
                            recognizedText.append("\n\n\n")  // Three newlines = two empty lines for new recording
                            isNewSession = false
                        } else {
                            recognizedText.append(" ")  // Within same recording - just space, NO empty line
                        }
                    }
                    recognizedText.append(newText)
                    
                    // Extra cleaning - removes hidden problems
                    val cleaned = recognizedText.toString()
                        .replace(MULTIPLE_SPACES_REGEX, " ")           // Multiple spaces → one
                        .replace(NEWLINE_WITH_SPACE_REGEX, "\n")       // Space after newline → remove
                        .replace(MULTIPLE_NEWLINES_REGEX, "\n\n\n")    // 4+ newlines → 3 (max two empty lines)
                        .trim()
                    
                    recognizedText.clear()
                    recognizedText.append(cleaned)
                    
                    textDisplay.setText(recognizedText.toString())
                    
                    scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    
                    statusText.text = getString(R.string.status_complete)
                } else {
                    // No text recognized - update status to reflect completion
                    statusText.text = getString(R.string.status_complete)
                }
                
                // Always update state even if no text was recognized
                isListening = false
                updateMicButton()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (isDestroyed) return
                
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val firstMatch = matches?.getOrNull(0) ?: ""
                
                if (firstMatch.isNotBlank()) {
                    val partial = firstMatch.replace(MULTIPLE_SPACES_REGEX, " ").trim()
                    // Only update UI if the cleaned partial text has changed
                    if (partial != lastPartialText) {
                        lastPartialText = partial
                        statusText.text = getString(R.string.status_heard, partial)
                    }
                } else if (lastPartialText.isNotEmpty()) {
                    // Clear stale partial text when moving to silent period
                    lastPartialText = ""
                    statusText.text = getString(R.string.status_listening)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Inte använd i denna implementation
            }
        })
    }

    private fun startListening() {
        // Prevent starting if already listening or if activity is being destroyed
        if (isListening || isDestroyed) {
            return
        }
        
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        
        // Ensure we have a valid SpeechRecognizer instance
        if (speechRecognizer == null) {
            setupSpeechRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentLanguage)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        // Reset for new listening session
        lastPartialText = "" // Reset for new listening session
        
        // Set listening flag BEFORE starting recognizer to prevent race conditions
        isListening = true
        updateMicButton()
        statusText.text = getString(R.string.status_preparing)
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            // If starting fails, reset state
            isListening = false
            updateMicButton()
            statusText.text = getString(R.string.error_unknown)
            enableTextEditing()
        }
    }

    private fun stopListening() {
        // Prevent stopping if not listening
        if (!isListening) {
            return
        }
        
        // Set isNewSession for next recording to create paragraph break
        // Only set if we have recognized text (otherwise there's nothing to separate)
        if (recognizedText.isNotEmpty()) {
            isNewSession = true
        }
        
        isListening = false
        lastPartialText = "" // Reset for next listening session
        
        // Update UI if activity is not being destroyed
        if (!isDestroyed) {
            updateMicButton()
            statusText.text = getString(R.string.status_stopped)
        }
        
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore exceptions when stopping
        }
    }

    private fun updateMicButton() {
        if (isListening) {
            micButton.text = getString(R.string.button_stop)
            micButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_stop)
        } else {
            micButton.text = getString(R.string.button_start)
            micButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.button_start)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Set destroyed flag first to prevent callbacks from executing
        isDestroyed = true
        isListening = false
        
        // Destroy speech recognizer
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            // Ignore exceptions during cleanup
        }
    }
}