package dev.mforcen.marshgallery

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        loadSettings()
        findViewById<Button>(R.id.settings_save_button).setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        val host = sharedPref.getString("marshgallery-host", "")
        val user = sharedPref.getString("marshgallery-user", "")
        val pass = sharedPref.getString("marshgallery-pass", "")

        findViewById<EditText>(R.id.settingsHostEdit).setText(host)
        findViewById<EditText>(R.id.settingsUserEdit).setText(user)
        findViewById<EditText>(R.id.settingsPassEdit).setText(pass)
    }

    private fun saveSettings() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )

        val hostEdit = findViewById<EditText>(R.id.settingsHostEdit).text.toString()
        val userEdit = findViewById<EditText>(R.id.settingsUserEdit).text.toString()
        val passEdit = findViewById<EditText>(R.id.settingsPassEdit).text.toString()

        with(sharedPref.edit()){
            putString("marshgallery-host", hostEdit)
            putString("marshgallery-user", userEdit)
            putString("marshgallery-pass", passEdit)
            apply()
        }

        finish()
    }
}