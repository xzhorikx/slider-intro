package alexz.sliderintro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_finish.*

private const val LAYOUT_ID: Int = R.layout.activity_finish
class IntroFinishedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_ID)
        ivFinishIcon?.setImageBitmap(BitmapHelper.getIconBitmap("apple.png", this))
    }
}
