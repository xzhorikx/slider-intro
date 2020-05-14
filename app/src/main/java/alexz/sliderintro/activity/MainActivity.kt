package alexz.sliderintro.activity

import alexz.sliderintro.R
import alexz.sliderintro.adapter.SliderAdapter
import alexz.sliderintro.fragment.SliderFragment
import alexz.sliderintro.viewmodel.MainAppActivityViewModel
import alexz.sliderintro.viewmodel.MainAppActivityViewModelFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT_ID)
        mainAppActivityViewModelFactory =
            MainAppActivityViewModelFactory(
                application
            )
        initObservers()
    }

    /**
     * Initializes all necessary LiveData observers
     */
    private fun initObservers() {
        mainAppActivityViewModel.fragmentTypeListLiveData.observe(this, Observer {
            it?.let { fragmentTypeList: List<SliderFragment.ScreenType> ->
                // Once we get fragment list to be added to view pager, we initialize 'vpIntro'
                val sliderAdapter = SliderAdapter(
                    supportFragmentManager,
                    fragmentTypeList
                )
                vpIntro?.apply {
                    adapter = sliderAdapter
                }
            }
        })

    }

}