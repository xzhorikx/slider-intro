package alexz.sliderintro.state
/**
 * State of navigation text
 *
 * @param alphaSkip "Skip" text alpha
 * @param alphaNext "Next" text alpha
 * @param alphaFinish "Finish" text alpha
 */
data class NavigationTextState(
    val alphaSkip: Float,
    val alphaNext: Float,
    val alphaFinish: Float
){
    companion object {
        const val ALPHA_MIN = 0.0f
        const val ALPHA_MAX = 1.0f
    }
}