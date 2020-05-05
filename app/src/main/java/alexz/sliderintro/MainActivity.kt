package alexz.sliderintro

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*

private const val LAYOUT_ID: Int = R.layout.activity_main
private val VIEW_MODEL: Class<MainAppActivityViewModel> = MainAppActivityViewModel::class.java
class MainActivity : AppCompatActivity() {
    /**
     * View model factory reference that will be initialized when activity is created
     */
    private lateinit var mainAppActivityViewModelFactory: MainAppActivityViewModelFactory

    /**
     * Main activity view model that contains state of displayed elements
     */
    private val mainAppActivityViewModel: MainAppActivityViewModel by lazy {
        ViewModelProvider(this, mainAppActivityViewModelFactory).get(VIEW_MODEL)
    }

    /**
     * Mutable list that keeps references to views that represent navigation dots on the screen.
     * These views are generated dynamically so we need to save reference to them in order to
     * change their properties later
     */
    private val mIntroDotViewList: MutableList<View> = mutableListOf()

    /**
     * Listener that is called when View Pager transition is ongoing (i.e. user swipes between
     * fragments)
     */
    private val onPageChangeListener: ViewPager.OnPageChangeListener by lazy {
        return@lazy object :  ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {/*ignore*/}
            override fun onPageSelected(position: Int) {/*ignore*/}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // there are cases when offset is assigned a value that falls out of [0, 1] range
                // we don't need to process it
                if(positionOffset < 0.0f || positionOffset > 1.0f){
                    return
                }
                // Calculating new offset sum and saving it as new state of view pager transition
                val offsetSum: Float = position + positionOffset
                val viewPagerScrollState = ViewHolderScrollState(offsetSum = offsetSum)
                mainAppActivityViewModel.updateViewPagerScrollState(viewPagerScrollState)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_ID)
        mainAppActivityViewModelFactory = MainAppActivityViewModelFactory(application)
        initObservers()
    }

    /**
     * Initializes all necessary LiveData observers
     */
    private fun initObservers() {
        mainAppActivityViewModel.fragmentLiveData.observe(this, Observer {
            it?.let { sliderFragmentList: List<SliderFragment> ->
                // Once we get fragment list to be added to view pager, we initialize 'vpIntro'
                val sliderAdapter = SliderAdapter(supportFragmentManager, sliderFragmentList)
                vpIntro?.apply {
                    clearOnPageChangeListeners()
                    addOnPageChangeListener(onPageChangeListener)
                    adapter = sliderAdapter
                }

                // Initializing navigation dots and saving view references
                mIntroDotViewList.clear()
                mIntroDotViewList.addAll(initNavigationDots(size = sliderFragmentList.size, context = this))

                initListeners(fragmentCount = sliderFragmentList.size, viewPager = vpIntro)
            }
        })

        mainAppActivityViewModel.backgroundColorLiveData.observe(this, Observer {
            it?.let {backgroundColor: Int ->
                vpIntro?.setBackgroundColor(backgroundColor)
            }
        })

        mainAppActivityViewModel.navigationDotStateLiveData.observe(this, Observer {
            it?.let { navigationDotStateList: List<NavigationDotState> ->
                navigationDotStateList.forEach { navigationDotState: NavigationDotState ->
                    val dotIndex: Int = navigationDotState.position
                    // Altering necessary attributes of dot views
                    mIntroDotViewList.forEachIndexed { viewIndex: Int, view: View ->
                        if(dotIndex == viewIndex) {
                            view.alpha = navigationDotState.alpha
                            view.scaleX = navigationDotState.scale
                            view.scaleY = navigationDotState.scale
                        }
                    }
                }
            }
        })
        mainAppActivityViewModel.navigationTextStateLiveData.observe(this, Observer {
            it?.let { navigationTextState: NavigationTextState ->
                tvIntroSkip?.alpha = navigationTextState.alphaSkip
                tvIntroNext?.alpha = navigationTextState.alphaNext
                tvIntoFinish?.alpha = navigationTextState.alphaFinish

                // Altering visibility of the views whose alpha is close to zero. This is done so
                // that some views won't overlap each other even though their alpha visibility might
                // be zero.
                val rangeMin = 0.0f
                val rangeMax = 0.005f
                val tvIntroSkipVisibility: Int = if (navigationTextState.alphaSkip in rangeMin..rangeMax) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                val tvIntroNextVisibility: Int = if (navigationTextState.alphaNext in rangeMin..rangeMax) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                val tvIntroFinishVisibility: Int = if (navigationTextState.alphaFinish in rangeMin..rangeMax) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                tvIntroSkip?.visibility = tvIntroSkipVisibility
                tvIntroNext?.visibility = tvIntroNextVisibility
                tvIntoFinish?.visibility = tvIntroFinishVisibility
            }
        })
    }

    /**
     * Initializes navigation dot views at the bottom of the screen. This method also attaches
     * created views to [containerIntroDots] view group.
     *
     * @param size amount of dots to create
     * @param context context
     * @return list of views referencing dot views in the same order they were created
     */
    private fun initNavigationDots(size: Int, context: Context): List<View> {
        val resultList: MutableList<View> = mutableListOf()
        containerIntroDots?.removeAllViews()
        for(i: Int in  0 until size){
            val dotView = View(context)
            val sizePx: Int = resources.getDimensionPixelSize(R.dimen.navigation_dot_size)
            val leftRightMarginPx: Int = resources.getDimensionPixelSize(R.dimen.navigation_dot_horizontal_padding)
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                sizePx,
                sizePx
            )
            val alpha: Float = if(i == 0){1.0f} else {0.5f}
            layoutParams.setMargins(leftRightMarginPx, 0, leftRightMarginPx, 0)
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            dotView.layoutParams = layoutParams
            dotView.setBackgroundResource(R.drawable.circle_white)
            dotView.alpha = alpha
            try {
                val background: GradientDrawable = dotView.background as GradientDrawable
                val bgColorRes: Int = R.color.colorWhite
                background.setColor(ContextCompat.getColor(context, bgColorRes))
            } catch (ignore: Exception) { }
            containerIntroDots?.addView(dotView)
            resultList.add(dotView)
        }
        return resultList
    }

    /**
     * Initializes all listeners for view elements in this activity
     *
     * @param fragmentCount total amount of fragments displayed in view pager
     * @param viewPager view pager reference
     */
    private fun initListeners(fragmentCount: Int, viewPager: ViewPager?) {
        // Click on Finish and Skip buttons should perform the same action so we are saving
        // on click listener in the common variable
        val finishIntroListener: View.OnClickListener = View.OnClickListener {
            val intent = Intent(this, IntroFinishedActivity::class.java)
            startActivity(intent)
        }
        tvIntoFinish?.setOnClickListener(finishIntroListener)
        tvIntroSkip?.setOnClickListener(finishIntroListener)
        tvIntroNext?.setOnClickListener {
            viewPager?.currentItem?.let {currentItem: Int ->
                // Next button should navigate forward on all screens except for last one
                if(currentItem < fragmentCount - 1){
                    viewPager.currentItem = currentItem + 1
                }
            }
        }
    }
}