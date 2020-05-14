package alexz.sliderintro.fragment

import alexz.sliderintro.BitmapHelper
import alexz.sliderintro.R
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sldier.*

/**
 * Reference for layout inflated by fragment
 */
private const val LAYOUT_ID = R.layout.fragment_sldier
/**
 * Fragment that is displayed in intro slider
 */
class SliderFragment : Fragment() {

    /**
     * Enumeration for all types of screens that need to be displayed in adapter
     *
     * @param backgroundColor background color reference for the type of screen. Note that this color
     * is unresolved and needs to be processed using ContextCompat.getColor() method.
     */
    enum class ScreenType(val backgroundColor: Int){
        /**
         * First screen of the intro slider
         */
        SCREEN_FIRST(backgroundColor = R.color.colorIntroFirst),
        /**
         * Second screen of the intro slider
         */
        SCREEN_SECOND(backgroundColor = R.color.colorIntroSecond),
        /**
         * Third screen of the intro slider
         */
        SCREEN_THIRD(backgroundColor =  R.color.colorIntroThird)
    }

    companion object {
        private const val KEY_PAGE_TYPE: String = "page_type"

        /**
         * Creates new instance of fragment of provided screen type
         *
         * @param screenType one of the [ScreenType] enumeration values
         */
        fun newInstance(screenType: ScreenType): SliderFragment {
            val frag = SliderFragment()
            val args = Bundle()
            args.putSerializable(KEY_PAGE_TYPE, screenType)
            frag.arguments = args
            return frag
        }
    }

    /**
     * Reference to the screen type of the current fragment. If null, then screen type hasn't yet
     * been set from fragment's arguments or fragment wasn't created using [newInstance] method
     */
    private var screenType: ScreenType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(LAYOUT_ID, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        screenType = savedInstanceState?.getSerializable(KEY_PAGE_TYPE) as? ScreenType?
            ?: arguments?.getSerializable(KEY_PAGE_TYPE) as? ScreenType?
        screenType?.let {it: ScreenType ->
            tvIntroTitle?.text = getTitle(it, requireContext())
            tvIntroBody?.text = getBody(it, requireContext())
            ivIntroIcon?.setImageBitmap(getIconBitmap(it, requireContext()))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Saving screen type value
        outState.putSerializable(KEY_PAGE_TYPE, screenType)
    }

    /**
     * Gets bitmap icon that needs to be displayed in the middle of the fragment
     *
     * @param screenType screen type enum value
     * @param context application context
     * @return Resolved [Bitmap] or null if image byte stream could not be read from assets
     */
    private fun getIconBitmap(screenType: ScreenType, context: Context): Bitmap? {
        val iconFormat = "png"
        val iconAssetName: String = when(screenType){
            ScreenType.SCREEN_FIRST -> "strawberry"
            ScreenType.SCREEN_SECOND -> "pineapple"
            ScreenType.SCREEN_THIRD -> "orange"
        }
        val iconAssetFileName = "$iconAssetName.$iconFormat"
        return BitmapHelper.getIconBitmap(
            iconAssetFileName,
            context
        )
    }

    /**
     * Gets title text of intro fragment
     *
     * @param screenType screen type enum value
     * @param context application context
     * @return resolved title string
     */
    private fun getTitle(screenType: ScreenType, context: Context): String {
        @StringRes val titleRes: Int = when(screenType){
            ScreenType.SCREEN_FIRST -> R.string.strawberry
            ScreenType.SCREEN_SECOND -> R.string.pineapple
            ScreenType.SCREEN_THIRD -> R.string.orange
        }
        return context.getString(titleRes)
    }


    /**
     * Gets body text of intro fragment
     *
     * @param screenType screen type enum value
     * @param context application context
     * @return resolved title string
     */
    private fun getBody(screenType: ScreenType, context: Context): String {
        @StringRes val titleRes: Int = when(screenType){
            ScreenType.SCREEN_FIRST -> R.string.strawberry_body
            ScreenType.SCREEN_SECOND -> R.string.pineapple_body
            ScreenType.SCREEN_THIRD -> R.string.orange_body
        }
        return context.getString(titleRes)
    }
}
