package com.uibuilder.app.presentation.properties

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.uibuilder.app.presentation.properties.tabs.AnimationTabFragment
import com.uibuilder.app.presentation.properties.tabs.BackgroundTabFragment
import com.uibuilder.app.presentation.properties.tabs.CornersEffectsTabFragment
import com.uibuilder.app.presentation.properties.tabs.DimensionsTabFragment
import com.uibuilder.app.presentation.properties.tabs.PaddingMarginTabFragment
import com.uibuilder.app.presentation.properties.tabs.TextTabFragment

class PropertiesPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TAB_TITLES.size

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> TextTabFragment()
        1 -> BackgroundTabFragment()
        2 -> DimensionsTabFragment()
        3 -> PaddingMarginTabFragment()
        4 -> CornersEffectsTabFragment()
        5 -> AnimationTabFragment()
        else -> TextTabFragment()
    }

    companion object {
        val TAB_TITLES = arrayOf(
            "Text",
            "Background",
            "Dimensions",
            "Padding/Margin",
            "Corners & Effects",
            "Animation"
        )
    }
}
