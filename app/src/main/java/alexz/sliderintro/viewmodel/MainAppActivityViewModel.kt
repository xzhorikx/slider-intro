package alexz.sliderintro.viewmodel

import alexz.sliderintro.fragment.SliderFragment
import alexz.sliderintro.state.NavigationDotState
import alexz.sliderintro.state.NavigationTextState
import alexz.sliderintro.state.ViewHolderScrollState
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * View model component for main activity that is responsible for keeping all LiveData objects
 * that should be observer in Main Activity.
 */
class MainAppActivityViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Ordered list of all fragments that should be displayed in intro slider
     */
    private val fragmentList: List<SliderFragment.ScreenType> by lazy {
        return@lazy SliderFragment.ScreenType.values().toList()
    }
    /**
     * Live data containing ordered list of all fragments that should be displayed in intro slider
     */
    val fragmentTypeListLiveData: LiveData<List<SliderFragment.ScreenType>?> by lazy {
        val mLiveData = MutableLiveData<List<SliderFragment.ScreenType>?>()
        mLiveData.postValue(fragmentList)
        return@lazy mLiveData
    }
}
