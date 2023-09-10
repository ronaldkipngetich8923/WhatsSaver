package com.upscapesoft.whatssaverapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.fragments.WADirectChatFragment
import com.upscapesoft.whatssaverapp.fragments.WABDirectChatFragment
import com.upscapesoft.whatssaverapp.helper.AdController
import java.util.ArrayList

class DirectChatActivity : AppCompatActivity() {

    private var backBtn: ImageView? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    lateinit var tabs: Array<String?>

    private var container: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_chat)


        viewPager = findViewById(R.id.dc_viewPager)
        setupViewPager(viewPager)
        tabs = arrayOfNulls(2)
        tabs[0] = resources.getString(R.string.whatsapp)
        tabs[1] = resources.getString(R.string.whatsapp_business)
        tabLayout = findViewById(R.id.dc_tabLayout)
        tabLayout!!.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout!!.tabCount) {
            val tab = tabLayout!!.getTabAt(i)
            tab!!.customView = getTabViewUn(i)
        }
        setupTabIcons()
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
                val tabs = tabLayout!!.getTabAt(tab.position)
                tabs!!.customView = null
                tabs.customView = getTabView(tab.position)

                AdController.adCounter++
                AdController.showInterAd(this@DirectChatActivity, null, 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabs = tabLayout!!.getTabAt(tab.position)
                tabs!!.customView = null
                tabs.customView = getTabViewUn(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        backBtn = findViewById<View>(R.id.backBtn) as ImageView
        backBtn!!.setOnClickListener {
            onBackPressed()
        }

        /*admob*/
        container = findViewById(R.id.banner_container)
        AdController.loadBannerAd(this@DirectChatActivity, container)
        AdController.loadInterAd(this@DirectChatActivity)

    }

    override fun onBackPressed() {
        AdController.adCounter++
        if (AdController.adCounter == AdController.adDisplayCounter) {
            AdController.showInterAd(this@DirectChatActivity, null, 0)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(WADirectChatFragment(), "WhatsApp")
        adapter.addFragment(WABDirectChatFragment(), "WhatsApp Business")
        viewPager!!.adapter = adapter
    }

    @SuppressLint("InflateParams")
    fun getTabViewUn(pos: Int): View {
        val v = LayoutInflater.from(this@DirectChatActivity).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[pos]
        txt.setTextColor(resources.getColor(R.color.tab_txt_unpressed))
        txt.setBackgroundResource(R.drawable.btn_unpressed_state)
        val tab = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tab
        return v
    }

    @SuppressLint("InflateParams")
    private fun setupTabIcons() {
        val v = LayoutInflater.from(this).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[0]
        txt.setTextColor(resources.getColor(R.color.tab_txt_pressed))
        txt.setBackgroundResource(R.drawable.btn_pressed_state)
        val tabp = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tabp
        val tab = tabLayout!!.getTabAt(0)
        tab!!.customView = null
        tab.customView = v
    }

    @SuppressLint("InflateParams")
    fun getTabView(pos: Int): View {
        val v = LayoutInflater.from(this@DirectChatActivity).inflate(R.layout.custom_tab_lay, null)
        val txt = v.findViewById<TextView>(R.id.tab)
        txt.text = tabs[pos]
        txt.setTextColor(resources.getColor(R.color.tab_txt_pressed))
        txt.setBackgroundResource(R.drawable.btn_pressed_state)
        val tab = FrameLayout.LayoutParams(
            resources.displayMetrics.widthPixels * 438 / 1080,
            resources.displayMetrics.heightPixels * 140 / 1920
        )
        txt.layoutParams = tab
        return v
    }

    internal class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(
        fm!!
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(arg0: Int): Fragment {
            return mFragmentList[arg0]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

}