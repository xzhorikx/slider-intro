package alexz.sliderintro.activity

import alexz.sliderintro.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

private const val LAYOUT_ID: Int = R.layout.activity_main
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_ID)
    }
}