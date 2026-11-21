package com.polpick.gemdetector

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

class MainActivity : Activity() {
    private val captureRequest = 4102
    private lateinit var status: TextView
    private lateinit var gridInfo: TextView
    private lateinit var levelSpinner: Spinner

    private data class GridConfig(val name: String, val rows: Int, val columns: Int) {
        val cells: Int get() = rows * columns
    }

    private val gridConfigs = listOf(
        GridConfig("Facile", 10, 3),
        GridConfig("Moyen", 10, 2),
        GridConfig("Difficile", 10, 3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 28, 24, 24)
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
            setPadding(0, 10, 0, 18)
        })

        root.addView(TextView(this).apply {
            text = "CHOISIR LE NIVEAU"
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 4, 0, 6)
        })
        levelSpinner = Spinner(this)
        levelSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            gridConfigs.map { it.name }
        )
        root.addView(levelSpinner)
        gridInfo = TextView(this).apply {
            textSize = 14f
            setTextColor(0xFFB9E7FF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 18)
        }
        root.addView(gridInfo)
        levelSpinner.setSelection(0)
        levelSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val config = gridConfigs[position]
                gridInfo.text = "${config.rows} lignes × ${config.columns} colonnes • ${config.cells} cases"
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                gridInfo.text = ""
            }
        }

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
            text = "Prêt. Choisis un niveau, puis démarre la surveillance."
            textSize = 15f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(4, 20, 4, 14)
        }
        root.addView(status)
        root.addView(TextView(this).apply {
            text = "Configuration de grille enregistrée pour le niveau choisi. L'analyse automatique des cases sera ajoutée dans une prochaine étape. L'application observe uniquement les images visibles à l'écran et ne peut pas garantir la découverte de diamants cachés."
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
