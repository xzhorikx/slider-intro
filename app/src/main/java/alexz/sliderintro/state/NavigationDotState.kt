package alexz.sliderintro.state

/**
 * State of navigation dot view.
 *
 * @param position position of navigation dot
 * @param alpha RGBA alpha value from 0.0 to 1.0
 * @param scale scale of element
 */
data class NavigationDotState(
    val position: Int,
    val alpha: Float,
    val scale: Float
) {
    companion object {
        /**
         * Minimum alpha value
         */
        const val ALPHA_MIN = 0.5f
        /**
         * Maximum alpha value
         */
        const val ALPHA_MAX = 1.0f
        /**
         * Maximum scale value
         */
        const val SCALE_MAX = 1.0f
        /**
         * Minimum scale value
         */
        const val SCALE_MIN = 0.5f
    }
}