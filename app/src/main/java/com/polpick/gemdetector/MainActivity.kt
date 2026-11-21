package com.polpick.gemdetector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val captureRequest = 4102
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 36, 24, 24)
            setBackgroundColor(0xFF101F2B.toInt())
        }
        root.addView(TextView(this).apply {
            text = "💎 POLPICK GEM DETECTOR"
            textSize = 23f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "NEXT • ANDROID 10"
            textSize = 13f
            setTextColor(0xFFB9C7D0.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 28)
        })
        root.addView(Button(this).apply {
            text = "DÉMARRER LA SURVEILLANCE"
            setOnClickListener { requestCapturePermission() }
        })
        root.addView(Button(this).apply {
            text = "ARRÊTER LA SURVEILLANCE"
            setOnClickListener {
                stopService(Intent(this@MainActivity, ProjectionForegroundService::class.java))
                status.text = "Surveillance arrêtée."
            }
        })
        status = TextView(this).apply {
            text = "Prêt. Appuie sur Démarrer et accepte l'autorisation Android."
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(4, 24, 4, 16)
        }
        root.addView(status)
        root.addView(TextView(this).apply {
            text = "Une notification permanente apparaîtra pendant la capture. L'application vérifie uniquement les images visibles à l'écran ; elle ne peut pas garantir la découverte de diamants cachés."
            textSize = 12f
            setTextColor(0xFFFFD278.toInt())
        })
        setContentView(root)
    }

    private fun requestCapturePermission() {
        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(manager.createScreenCaptureIntent(), captureRequest)
    }

    @Deprecated("Retained for Android 10 compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != captureRequest) return
        if (resultCode != RESULT_OK || data == null) {
            status.text = "Autorisation refusée ou annulée."
            return
        }
        val serviceIntent = Intent(this, ProjectionForegroundService::class.java).apply {
            putExtra(ProjectionForegroundService.EXTRA_RESULT_CODE, resultCode)
            putExtra(ProjectionForegroundService.EXTRA_RESULT_DATA, data)
        }
        try {
            startForegroundService(serviceIntent)
            status.text = "Demande envoyée. Vérifie la notification de surveillance."
        } catch (e: Exception) {
            status.text = "Impossible de lancer le service : ${e.message ?: "erreur Android"}"
        }
    }
}
