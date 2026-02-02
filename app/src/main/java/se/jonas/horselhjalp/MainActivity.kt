package se.jonas.horselhjalp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            webView.reload()  // Ladda om sidan efter att tillstånd getts
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skapa och konfigurera WebView
        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            webViewClient = WebViewClient()

            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest) {
                    if (request.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            request.grant(request.resources)
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            request.deny()  // Nekas tills tillstånd getts
                        }
                    } else {
                        request.grant(request.resources)
                    }
                }
            }
        }

        // Sätt WebView som innehåll
        setContentView(webView)

        // Välj rätt HTML-fil baserat på enhetens språk
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
        val lang = locale?.language ?: java.util.Locale.getDefault().language
        val assetIndex = when (lang) {
            "en" -> "file:///android_asset/www/index_en.html"
            "sv" -> "file:///android_asset/www/index.html"
            else -> "file:///android_asset/www/index.html" // fallback to Swedish/default
        }
        
        // Ladda den valda sidan
        webView.loadUrl(assetIndex)

        // Be om mikrofon-tillstånd direkt (om det behövs)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        // Modern hantering av back-knappen (tar bort deprecated-varning)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false  // Tillåt standard back-beteende (stäng appen)
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
}