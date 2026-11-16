package com.cryoffear.launcher

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File

class MainActivity : Activity() {

    private val gameFolder = File(Environment.getExternalStorageDirectory(), "xash/cryoffear")
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 80, 60, 60)
        }

        val title = TextView(this).apply {
            text = "CRY OF FEAR\nLAUNCHER"
            textSize = 30f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        statusText = TextView(this).apply {
            text = "Carpeta: /xash/cryoffear\n\nPulsa VERIFICAR para revisar el juego."
            textSize = 16f
            setPadding(0, 40, 0, 40)
        }

        val btnVerificar = Button(this).apply { text = "1. VERIFICAR ARCHIVOS" }
        val btnArreglar = Button(this).apply { text = "2. ARREGLAR CONFIG" }
        val btnJugar = Button(this).apply { text = "3. JUGAR" }

        btnVerificar.setOnClickListener { verificar() }
        btnArreglar.setOnClickListener { arreglarConfig() }
        btnJugar.setOnClickListener { jugar() }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(btnVerificar)
        layout.addView(btnArreglar)
        layout.addView(btnJugar)
        setContentView(layout)

        pedirPermiso()
    }

    private fun pedirPermiso() {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun verificar() {
        if (!gameFolder.exists()) {
            statusText.text = "ERROR: No existe la carpeta\n/xash/cryoffear\n\nCopiala en la memoria interna."
            return
        }
        val faltan = mutableListOf<String>()
        val necesarios = listOf("maps", "models", "sound", "sprites", "gfx")
        for (n in necesarios) {
            if (!File(gameFolder, n).exists()) faltan.add(n)
        }
        if (faltan.isEmpty()) {
            statusText.text = "TODO BIEN ✅\nCarpeta cryoffear completa.\nYa podes darle a ARREGLAR CONFIG y JUGAR."
        } else {
            statusText.text = "FALTAN CARPETAS:\n" + faltan.joinToString("\n") + "\n\nVolvé a copiar el mod de ModDB completo."
        }
    }

    private fun arreglarConfig() {
        try {
            if (!gameFolder.exists()) gameFolder.mkdirs()
            val config = File(gameFolder, "config.cfg")
            val contenido = """
                // Cry of Fear - Config Launcher
                unbindall
                bind "TAB" "+showscores"
                bind "ENTER" "+attack"
                bind "MOUSE1" "+attack"
                bind "MOUSE2" "+attack2"
                bind "SHIFT" "+speed"
                bind "CTRL" "+duck"
                bind "SPACE" "+jump"
                bind "W" "+forward"
                bind "S" "+back"
                bind "A" "+moveleft"
                bind "D" "+moveright"
                bind "F" "+use"
                bind "R" "+reload"
                bind "Q" "invprev"
                bind "E" "invnext"
                gl_texturemode "GL_LINEAR_MIPMAP_LINEAR"
                gl_ansio "1"
                gl_vsync "1"
                gl_dither "1"
                max_shells "100"
                max_smokepuffs "100"
                violence_ablood "1"
                violence_agibs "1"
                violence_hblood "1"
                violence_hgibs "1"
                _snd_mixahead "0.1"
                volume "0.8"
                suitvolume "0.5"
                sensitivity "3.0"
                m_pitch "0.022"
                m_yaw "0.022"
                cl_vsmoothing "0"
                fps_max "60"
                developer "0"
            """.trimIndent()
            config.writeText(contenido)
            Toast.makeText(this, "Config arreglada ✅", Toast.LENGTH_SHORT).show()
            statusText.text = "CONFIG ARREGLADA ✅\nTexturas en GL_LINEAR_MIPMAP_LINEAR\nYa podes JUGAR."
        } catch (e: Exception) {
            statusText.text = "ERROR al escribir config: ${e.message}"
        }
    }

    private fun jugar() {
        val paquetes = listOf("su.xash.engine", "in.celest.xash3d", "com.xash3d.fwgs", "su.xash3d.engine")
        for (pkg in paquetes) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                intent.putExtra("game", "cryoffear")
                intent.putExtra("-game", "cryoffear")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                Toast.makeText(this, "Abriendo Xash en Cry of Fear...", Toast.LENGTH_SHORT).show()
                return
            }
        }
        statusText.text = "No encontré Xash instalado.\nInstalá Xash3D FWGS / Xash New Engine y volvé a intentar."
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/FWGS/xash3d-fwgs")))
        } catch (_: Exception) {}
    }
}
