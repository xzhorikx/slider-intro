package alexz.sliderintro.viewmodel

import alexz.sliderintro.fragment.SliderFragment
import alexz.sliderintro.state.NavigationDotState
import alexz.sliderintro.state.NavigationTextState
import alexz.sliderintro.state.ViewHolderScrollState
import android.animation.ArgbEvaluator
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlin.math.floor

/**
 * View model component for main activity that is responsible for keeping all LiveData objects
 * that should be observer in Main Activity.
 */
class MainAppActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Ordered list of all fragments that should be displayed in intro slider
     */
    private val fragmentList: List<SliderFragment> by lazy {
        return@lazy SliderFragment.ScreenType
            .values()
            .map { screenType: SliderFragment.ScreenType ->
                SliderFragment.newInstance(screenType)
            }
    }

    /**
     * Live Data containing state of text displayed on screen. All possible states for text fields
     * are listed in [NavigationTextState] class
     */
    val navigationTextStateLiveData: LiveData<NavigationTextState?> by lazy {
        return@lazy Transformations.map(mViewHolderScrollStateLiveData){viewHolderScrollState: ViewHolderScrollState? ->
            if(null == viewHolderScrollState){
                // Non-last page
                return@map NavigationTextState(
                    alphaSkip = NavigationTextState.ALPHA_MAX,
                    alphaNext = NavigationTextState.ALPHA_MAX,
                    alphaFinish = NavigationTextState.ALPHA_MIN
                )
            }

            val offsetSum: Float = viewHolderScrollState.offsetSum
            val position: Int = floor(offsetSum).toInt()
            val offset: Float =  offsetSum - position
            val lastFragmentPosition: Int = fragmentList.size - 1
            val range = lastFragmentPosition - offsetSum
            val skipTextAlpha: Float
            val nextTextAlpha: Float
            val finishTextAlpha: Float

            when {
                lastFragmentPosition == position -> {
                    // Last page displayed
                    skipTextAlpha = NavigationTextState.ALPHA_MIN
                    nextTextAlpha = NavigationTextState.ALPHA_MIN
                    finishTextAlpha = NavigationTextState.ALPHA_MAX
                }
                range in 0.00f..1.0f -> {
                    // Transition to the last page
                    skipTextAlpha = NavigationTextState.ALPHA_MAX - offset
                    nextTextAlpha = skipTextAlpha
                    finishTextAlpha = NavigationTextState.ALPHA_MIN + offset

                }
                else -> {
                    // Transition to any page, but last
                    skipTextAlpha = NavigationTextState.ALPHA_MAX
                    nextTextAlpha = NavigationTextState.ALPHA_MAX
                    finishTextAlpha = NavigationTextState.ALPHA_MIN
                }
            }

            return@map NavigationTextState(
                alphaSkip = skipTextAlpha,
                alphaNext = nextTextAlpha,
                alphaFinish = finishTextAlpha
            )
        }
    }

    /**
     * Live Data containing state of navigation dots
     */
    val navigationDotStateLiveData: LiveData<List<NavigationDotState>?> by lazy {
        return@lazy Transformations.map(mViewHolderScrollStateLiveData){viewHolderScrollState: ViewHolderScrollState? ->
            // Meaning no transition was yet trigerred by user and we need to return initial state
            // for dots. Default state would be following:
            // 1. Amount of dots = fragment list size
            // 2. First dot should have maximum alpha, others should have minimum alpha
            // 3. First dot should have maximum scale, others should have minimum scale
            if(null == viewHolderScrollState){
                return@map fragmentList
                    .mapIndexed { index: Int, _: SliderFragment ->
                        val alpha: Float
                        val scale: Float
                        if(index == 0){
                            alpha = NavigationDotState.ALPHA_MAX
                            scale = NavigationDotState.SCALE_MAX
                        } else {
                            alpha = NavigationDotState.ALPHA_MIN
                            scale = NavigationDotState.SCALE_MIN
                        }
                        return@mapIndexed NavigationDotState(
                            position = index,
                            alpha = alpha,
                            scale = scale
                        )
                    }
            }

            val offsetSum: Float = viewHolderScrollState.offsetSum
            val position: Int = floor(offsetSum).toInt()
            val offset: Float =  offsetSum - position
            return@map fragmentList
                .mapIndexed { fragmentIndex, _ ->

                    val alphaDelta: Float = NavigationDotState.ALPHA_MAX - NavigationDotState.ALPHA_MIN
                    val scaleDelta: Float = NavigationDotState.SCALE_MAX - NavigationDotState.SCALE_MIN

                    val newAlpha: Float
                    val newScale: Float

                    /*
                    * Checking what dots need to be altered during the transaction. The logic is
                    * following:
                    * 1. We have a position of fragment (from view pager state) and its offset,
                    * ranged [0.0, 1.0]
                    *
                    * 2. We accumulate sum of view pager position and its offset to determine which
                    * fragments and dots are participating in transition. So when fragment 0
                    * shifted towards next fragment by 50%, the accumulated sum will be
                    * 0 (position) +   0.5 (offset) = 0.5. When fragment 2 shifted towards next
                    * fragment by 25%, sum will be 2 + 0.25 = 2.25.
                    *
                    * 3. We know the position of dot. Now we need to determine the value of
                    * following equation: (SUM from #2) - (position of dot).
                    * Next it is necessary to check in what range does the value fall.
                    *                       0.0  -   dot of a fragment that occupies 100$ of screen
                    *                (0.0, 1.0)  -   user navigates to next fragment from current one
                    *               (-1.0, 0.0)  -   user navigate to this fragment from another one
                    *   [inf, -1.0], [1.0, inf]  -   dot doesn't participate in transition
                    * */
                    when (offsetSum - fragmentIndex) {
                        0.0f -> {
                            // Dot of the fragment that occupies the whole screen
                            newAlpha = NavigationDotState.ALPHA_MAX
                            newScale = NavigationDotState.SCALE_MAX
                        }
                        in -0.999f .. 0.0f -> {
                            // User is navigating towards the dot. It needs to increase visibility/scale
                            newAlpha = NavigationDotState.ALPHA_MIN + (offset * alphaDelta)
                            newScale = NavigationDotState.SCALE_MIN + (offset * scaleDelta)
                        }
                        in 0.0f .. 0.999f -> {
                            // User is navigating away from the dot. It needs to decrease visibility/scale
                            newAlpha = NavigationDotState.ALPHA_MAX - (offset * alphaDelta)
                            newScale = NavigationDotState.SCALE_MAX - (offset * scaleDelta)
                        }
                        else -> {
                            // Navigation dot does not participate in current transition as it
                            // is too far away from the main displayed fragment
                            newAlpha =  NavigationDotState.ALPHA_MIN
                            newScale = NavigationDotState.SCALE_MIN
                        }
                    }

                   return@mapIndexed NavigationDotState(
                        position = fragmentIndex,
                        alpha = newAlpha,
                        scale = newScale
                    )
                }

        }
    }

    /**
     * Live data containing current background color that needs to be displayed. Depends on
     * view pager transition state in [mViewHolderScrollStateLiveData] to calculate necessary color.
     */
    val backgroundColorLiveData: LiveData<Int?> by lazy {
        val argbEvaluator = ArgbEvaluator()
        return@lazy Transformations.map(mViewHolderScrollStateLiveData) { viewHolderScrollState: ViewHolderScrollState? ->
            return@map viewHolderScrollState?.let {
                // Max offset for 3 fragments is 2.0, min offset is 0.0. By flooring offset sum we
                // can detect which position is currently displayed
                val offsetSum: Float = viewHolderScrollState.offsetSum
                val position: Int = floor(offsetSum).toInt()
                val offset: Float =  offsetSum - position
                val fragmentColor: Int = fragmentList[position].getBackgroundColor()
                return@let if(position < (fragmentList.size - 1)){
                    argbEvaluator
                        .evaluate(
                            offset,
                            ContextCompat.getColor(application, fragmentColor),
                            ContextCompat.getColor(application, fragmentList[position + 1].getBackgroundColor())
                        ) as Int
                } else {
                    ContextCompat.getColor(application, fragmentColor)
                }
            }
        }
    }
    /**
     * Live data containing ordered list of all fragments that should be displayed in intro slider
     */
    val fragmentLiveData: LiveData<List<SliderFragment>?> by lazy {
        val mLiveData = MutableLiveData<List<SliderFragment>?>()
        mLiveData.postValue(fragmentList)
        return@lazy mLiveData
    }

    /**
     * Live data containing state of transition between two fragment in View Pager
     */
    private val mViewHolderScrollStateLiveData: MutableLiveData<ViewHolderScrollState?> by lazy {
        MutableLiveData<ViewHolderScrollState?>()
    }

    /**
     * Updating state of transition between two fragment in View Pager
     */
    fun updateViewPagerScrollState(viewPagerScrollState: ViewHolderScrollState) {
        mViewHolderScrollStateLiveData.postValue(viewPagerScrollState)
    }
}
