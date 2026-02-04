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
import java.util.Locale

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

        // Ladda din sida - välj språk baserat på systemspråk
        val locale = Locale.getDefault()
        val assetsPath = if (locale.language == "sv") {
            "file:///android_asset/www/index.html"
        } else {
            // Default to English for all non-Swedish languages
            "file:///android_asset/www-en/index.html"
        }
        webView.loadUrl(assetsPath)

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