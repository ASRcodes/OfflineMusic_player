package com.example.sangeet
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sangeet.databinding.ActivityDowloadBinding

class dowload : AppCompatActivity() {
    private lateinit var binding: ActivityDowloadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDowloadBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val downloadPath = downloadDirectory.absolutePath
        Log.d("DownloadPath", "Download directory: $downloadPath")

        binding.webView.loadUrl("https://ww-pagalworld.com/")
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
            }
        }
        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
                } else {
                    downloadFile(url)
                }
            }
        }
    }
    private fun downloadFile(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("File Download") // Title of the download notification
            .setDescription("Downloading") // Description of the download notification
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Show notification after download is complete

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Download file
                // If permission is granted after request, initiate download
                downloadFile("URL_OF_YOUR_FILE")
            }
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()){
            binding.webView.goBack()
        }
        else {
            super.onBackPressed()
        }
    }
    companion object {
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1001
    }
}