package alexz.sliderintro

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream

/**
 * Helper class for operations with bitmaps
 */
object BitmapHelper {

    /**
     * Gets bitmap icon from assets
     *
     * @param assetFileName asset file name with extension
     * @param context application context
     * @return Resolved [Bitmap] or null if image byte stream could not be read from assets
     */
    fun getIconBitmap(assetFileName: String, context: Context): Bitmap? {
        return try {
            val assetManager: AssetManager? = context.assets
            val inputStream: InputStream? = assetManager?.open(assetFileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
