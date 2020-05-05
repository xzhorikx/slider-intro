package alexz.sliderintro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * View pager adapter that displays list of fragments
 *
 * @param fragmentManager fragment manager
 * @param fragmentList list of fragments to display
 */
class SliderAdapter(
    fragmentManager: FragmentManager,
    private val fragmentList: List<SliderFragment>
): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}
