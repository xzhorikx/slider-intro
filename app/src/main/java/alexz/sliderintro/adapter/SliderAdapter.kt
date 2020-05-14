package alexz.sliderintro.adapter

import alexz.sliderintro.fragment.SliderFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * View pager adapter that displays list of fragments
 *
 * @param fragmentManager fragment manager
 * @param fragmentTypeList list of fragment types to display
 */
class SliderAdapter(
    fragmentManager: FragmentManager,
    private val fragmentTypeList: List<SliderFragment.ScreenType>
): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return SliderFragment.newInstance(fragmentTypeList[position])
    }

    override fun getCount(): Int {
        return fragmentTypeList.size
    }
}
