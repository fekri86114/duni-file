package info.fekri.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.fekri.R
import info.fekri.databinding.ActivityMainBinding

// vertical --> 0
// grid --> 1

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        var ourViewType = 0
        var ourSpanCount = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val file = getExternalFilesDir(null)!!
        val path = file.path

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frame_main_container, FileFragment(path))
        transaction.commit()

    }
}