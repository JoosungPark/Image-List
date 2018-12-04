package com.joosung.imagelist.common

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import com.joosung.imagelist.util.LogUtil

open class BaseFragmentStatePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    protected val DEBUG_TAG = this.javaClass.simpleName

    protected var items = ArrayList<FragmentBundle>()
    protected val fragmentPosition = hashMapOf<Fragment, Int>()

    override fun getItem(position: Int): Fragment {
        val fragment = FragmentFactory.createFragment(items[position])
        fragmentPosition.put(fragment, position)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        fragmentPosition.remove(`object`)
    }

    override fun getCount(): Int = items.count()

    open fun rx(): (List<FragmentBundle>) -> Unit {
        return fun(list: List<FragmentBundle>) {
            items.clear()
            items.addAll(list)
            notifyDataSetChanged()
        }
    }
}