package alexz.sliderintro.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainAppActivityViewModelFactory (
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainAppActivityViewModel::class.java)){
            return MainAppActivityViewModel(
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}