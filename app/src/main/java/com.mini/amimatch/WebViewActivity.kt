package com.mini.amimatch

import android.content.res.AssetManager
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)

        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        if (fileName != null) {
            loadHtmlFile(fileName)
        } else {
            finish()
        }
    }

    private fun loadHtmlFile(fileName: String) {
        val htmlContent = loadHtmlFromAsset(fileName)
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun loadHtmlFromAsset(fileName: String): String {
        val assetManager: AssetManager = resources.assets
        return try {
            assetManager.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    companion object {
        const val EXTRA_FILE_NAME = "file_name"
    }
}
