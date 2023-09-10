package com.upscapesoft.whatssaverapp.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.activities.SavedStatusActivity
import com.upscapesoft.whatssaverapp.activities.VideoPlayerActivity
import com.upscapesoft.whatssaverapp.activities.WABStatusActivity
import com.upscapesoft.whatssaverapp.activities.WAStatusActivity

class VideoPreviewAdapter(var activity: Activity) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.preview_list_item, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewMain)
        val iconPlayer = itemView.findViewById<ImageView>(R.id.iconPlayerMain)
        val shouldShowWhats = activity.intent.getBooleanExtra("shouldShowWhats", false)
        val shouldShowWhatsB = activity.intent.getBooleanExtra("shouldShowWhatsB", false)
        val shouldShowSaved = activity.intent.getBooleanExtra("shouldShowSaved", false)
        when {
            shouldShowWhats -> {
                Glide.with(imageView.context).load(WAStatusActivity.filePathsVideos!![position])
                    .into(imageView)
                imageView.setOnClickListener {
                    val intent = Intent(activity.applicationContext, VideoPlayerActivity::class.java)
                    intent.putExtra("videosVid2", WAStatusActivity.filePathsVideos!![position])
                    activity.startActivity(intent)
                }
            }
            shouldShowWhatsB -> {
                Glide.with(imageView.context).load(WABStatusActivity.filePathsVideos!![position])
                    .into(imageView)
                imageView.setOnClickListener {
                    val intent = Intent(activity.applicationContext, VideoPlayerActivity::class.java)
                    intent.putExtra("videosVid2", WABStatusActivity.filePathsVideos!![position])
                    activity.startActivity(intent)
                }
            }
            shouldShowSaved -> {
                Glide.with(imageView.context).load(SavedStatusActivity.filePathsVideos!![position])
                    .into(imageView)
                imageView.setOnClickListener {
                    val intent = Intent(activity.applicationContext, VideoPlayerActivity::class.java)
                    intent.putExtra("videosVid2", SavedStatusActivity.filePathsVideos!![position])
                    activity.startActivity(intent)
                }
            }
        }
        iconPlayer.visibility = View.VISIBLE
        container.addView(itemView)

        return itemView

    }

    override fun getCount(): Int {
        val shouldShowWhats = activity.intent.getBooleanExtra("shouldShowWhats", false)
        val shouldShowWhatsB = activity.intent.getBooleanExtra("shouldShowWhatsB", false)
        val shouldShowSaved = activity.intent.getBooleanExtra("shouldShowSaved", false)
        return when {
            shouldShowWhats -> {
                WAStatusActivity.filePathsVideos!!.size
            }
            shouldShowWhatsB -> {
                WABStatusActivity.filePathsVideos!!.size
            }
            shouldShowSaved -> {
                SavedStatusActivity.filePathsVideos!!.size
            }
            else -> 0
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

}