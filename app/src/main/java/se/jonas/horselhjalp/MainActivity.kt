package se.jonas.horselhjalp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.text.TextWatcher
import android.text.Editable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
import android.content.res.Configuration
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
    private var currentUtterance = StringBuilder() // Holds ONLY the current utterance being spoken
    private var currentLanguage = "sv-SE" // Default to Swedish
    private var isManualEditing = false // Flag to track if user is manually editing
    private var isProgrammaticUpdate = false // Flag to track programmatic text updates
    @Volatile private var isDestroyed = false // Flag to track if activity is being destroyed
    private var isNewRecordingSession = false // Flag to track if this is a new recording session after manual stop
    
    // Auto-pause after silence variables
    private var silenceStartTime: Long? = null
    private var manuallyStopped = false
    private val silenceCheckHandler = Handler(Looper.getMainLooper())
    private val silenceCheckRunnable: Runnable by lazy {
        object : Runnable {
            override fun run() {
                checkSilenceDuration()
                silenceCheckHandler.postDelayed(this, SILENCE_CHECK_INTERVAL_MS)
            }
        }
    }
    private var autoRestartRunnable: Runnable? = null
    
    // Custom persistence using XOR encoding to discourage manual preference editing
    private var ogonmiljotillstand = 0
    private var textstorleksindex = 1 // Default to medium (32sp), index 0-3 for 24sp, 32sp, 40sp, 48sp
    
    companion object {
        private const val SILENCE_CHECK_INTERVAL_MS = 500L
        private const val SILENCE_THRESHOLD_MS = 8000L
        private const val AUTO_RESTART_DELAY_MS = 1000L
        private const val SILENCE_RMS_THRESHOLD_DB = -40f
        
        // Font size options in sp
        private val FONT_SIZES = arrayOf(24f, 32f, 40f, 48f)
        
        // Compiled regex for whitespace normalization (performance optimization)
        private val MULTIPLE_SPACES_REGEX = Regex(" +")
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
        
        // Make textDisplay always selectable for copying
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
            isProgrammaticUpdate = true
            textDisplay.setText(getString(R.string.text_placeholder))
            isProgrammaticUpdate = false
            statusText.text = getString(R.string.status_text_cleared)
            isManualEditing = false // Reset manual editing flag
            isNewRecordingSession = false // Reset new recording session flag
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
        
        // Add TextWatcher to detect manual editing
        textDisplay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Only mark as manual editing if the change was not programmatic
                // We check if the text field has focus and it's not a programmatic update
                if (textDisplay.hasFocus() && !isProgrammaticUpdate) {
                    isManualEditing = true
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
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

    // Helper method to disable text editing during listening
    private fun disableTextEditing() {
        textDisplay.isFocusable = false
        textDisplay.isFocusableInTouchMode = false
        textDisplay.isClickable = true
        textDisplay.isLongClickable = true
        textDisplay.setTextIsSelectable(true)
    }

    // Helper method to enable text editing after listening
    private fun enableTextEditing() {
        textDisplay.isFocusable = true
        textDisplay.isFocusableInTouchMode = true
        textDisplay.setTextIsSelectable(true)
    }
    
    // Check silence duration and auto-pause if needed
    private fun checkSilenceDuration() {
        if (isDestroyed) return
        
        val currentSilenceStart = silenceStartTime
        if (currentSilenceStart != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - currentSilenceStart > SILENCE_THRESHOLD_MS) {
                // More than 5 seconds of silence - auto-pause
                speechRecognizer?.stopListening()
                Toast.makeText(this, getString(R.string.status_silence_paused), Toast.LENGTH_SHORT).show()
                silenceStartTime = null
                
                // Mark as manually stopped to trigger new session behavior
                manuallyStopped = true
                
                // Mark that next recording will be a new session (for separator after silence)
                if (recognizedText.isNotEmpty()) {
                    isNewRecordingSession = true
                }
                
                // Restart automatically after a short delay if not destroyed
                if (!isDestroyed) {
                    val restartRunnable = Runnable {
                        if (!isListening && !isDestroyed) {
                            // Reset manuallyStopped before restarting for continuous operation
                            manuallyStopped = false
                            startListening()
                        }
                    }
                    autoRestartRunnable = restartRunnable
                    silenceCheckHandler.postDelayed(restartRunnable, AUTO_RESTART_DELAY_MS)
                }
            }
        }
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
                // Clear current utterance at the beginning of new speech
                currentUtterance.clear()
                // Reset silence timer
                silenceStartTime = null
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Auto-pause after silence detection
                if (rmsdB < SILENCE_RMS_THRESHOLD_DB) {
                    // Silence detected
                    if (silenceStartTime == null) {
                        silenceStartTime = System.currentTimeMillis()
                    }
                } else {
                    // Sound detected - reset silence timer
                    silenceStartTime = null
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Inte använd i denna implementation
            }

            override fun onEndOfSpeech() {
                if (isDestroyed) return
                statusText.text = getString(R.string.status_processing)
                // Don't clear currentUtterance here - it will be used in onResults()
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
                silenceStartTime = null
                
                // Stop silence check handler
                silenceCheckHandler.removeCallbacks(silenceCheckRunnable)
                autoRestartRunnable?.let { silenceCheckHandler.removeCallbacks(it) }
                
                // Re-enable editing after error
                enableTextEditing()
                
                updateMicButton()
                
                // Starta om automatiskt efter "inget tal"-fel
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || 
                    error == SpeechRecognizer.ERROR_NO_MATCH) {
                    startListening()
                }
            }

            override fun onResults(results: Bundle?) {
                if (isDestroyed) return
                
                // Use currentUtterance which already contains cleaned partial results
                if (currentUtterance.isNotEmpty()) {
                    // Add separator based on whether this is a new recording session
                    if (recognizedText.isNotEmpty()) {
                        if (isNewRecordingSession) {
                            // Add double newline for new recording session
                            recognizedText.append("\n\n")
                            isNewRecordingSession = false
                        } else {
                            // Add single space within same recording session
                            // Check if recognizedText already ends with space to avoid double spaces
                            if (!recognizedText.endsWith(" ")) {
                                recognizedText.append(" ")
                            }
                        }
                    }
                    // Append the current utterance to recognizedText
                    recognizedText.append(currentUtterance.toString())
                    
                    // Normalize all whitespace globally - replace any multiple spaces with single space
                    val normalizedText = recognizedText.toString().replace(MULTIPLE_SPACES_REGEX, " ")
                    recognizedText.clear()
                    recognizedText.append(normalizedText)
                    
                    isProgrammaticUpdate = true
                    textDisplay.setText(recognizedText.toString())
                    isProgrammaticUpdate = false
                    
                    // Scrolla ner automatiskt
                    scrollView.post {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    
                    statusText.text = getString(R.string.status_complete)
                }
                
                // Clear currentUtterance after final results are processed
                currentUtterance.clear()
                
                isListening = false
                silenceStartTime = null
                
                // Stop silence check handler
                silenceCheckHandler.removeCallbacks(silenceCheckRunnable)
                autoRestartRunnable?.let { silenceCheckHandler.removeCallbacks(it) }
                
                // Re-enable editing after final results
                enableTextEditing()
                
                updateMicButton()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (isDestroyed) return

                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0]?.replace(Regex("\\s+"), " ")?.trim() ?: ""
                    if (partialText.isBlank()) return

                    statusText.text = getString(R.string.status_heard, partialText)
                    
                    // Update currentUtterance for onResults to use
                    currentUtterance.clear()
                    currentUtterance.append(partialText)

                    if (!isManualEditing && partialText.isNotEmpty()) {
                        val displayText = buildString {
                            val prev = recognizedText.toString()
                            append(prev)
                            // Lägg till space ENDAST om det behövs (inte alltid)
                            if (prev.isNotEmpty() && !prev.endsWith(" ")) {
                                append(" ")
                            }
                            append(partialText)
                        }
                        isProgrammaticUpdate = true
                        textDisplay.setText(displayText)
                        isProgrammaticUpdate = false

                        // Scrolla ner live
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                    }
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

        // Reset manual editing flag when starting new listening session
        isManualEditing = false
        
        // Clear currentUtterance when starting new listening session
        currentUtterance.clear()
        
        // Reset silence detection
        silenceStartTime = null
        manuallyStopped = false
        
        // Set listening flag BEFORE starting recognizer to prevent race conditions
        isListening = true
        updateMicButton()
        statusText.text = getString(R.string.status_preparing)
        
        // Start silence check handler
        silenceCheckHandler.postDelayed(silenceCheckRunnable, SILENCE_CHECK_INTERVAL_MS)
        
        // Make textDisplay non-editable during listening but keep it selectable for copying
        disableTextEditing()
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            // If starting fails, reset state
            isListening = false
            updateMicButton()
            statusText.text = getString(R.string.error_unknown)
            enableTextEditing()
            silenceCheckHandler.removeCallbacks(silenceCheckRunnable)
        }
    }

    private fun stopListening() {
        // Save destroyed state for checking
        val wasDestroyed = isDestroyed
        
        // Prevent stopping if not listening
        if (!isListening) {
            return
        }
        
        isListening = false
        manuallyStopped = true
        silenceStartTime = null
        
        // Mark that the next recording will be a new session (for separator)
        if (recognizedText.isNotEmpty()) {
            isNewRecordingSession = true
        }
        
        // Stop silence check handler - remove ALL callbacks
        silenceCheckHandler.removeCallbacks(silenceCheckRunnable)
        autoRestartRunnable?.let { silenceCheckHandler.removeCallbacks(it) }
        
        // Only update UI if activity is not being destroyed
        if (!wasDestroyed) {
            // Re-enable editing after listening stops
            enableTextEditing()
            
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
        
        // Remove all pending callbacks before destroying
        silenceCheckHandler.removeCallbacksAndMessages(null)
        
        // Destroy speech recognizer
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            // Ignore exceptions during cleanup
        }
    }
}