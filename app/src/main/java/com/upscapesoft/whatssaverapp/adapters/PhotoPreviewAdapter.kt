package com.upscapesoft.whatssaverapp.adapters

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.activities.ImagePreviewActivity
import com.upscapesoft.whatssaverapp.activities.SavedStatusActivity
import com.upscapesoft.whatssaverapp.activities.WABStatusActivity
import com.upscapesoft.whatssaverapp.activities.WAStatusActivity
import java.io.File

class PhotoPreviewAdapter(var activity: Activity) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(activity).inflate(R.layout.preview_list_item, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imageViewMain)
        val iconPlayer = itemView.findViewById<ImageView>(R.id.iconPlayerMain)
        val shouldShowWhats = activity.intent.getBooleanExtra("shouldShowWhats", false)
        val shouldShowWhatsB = activity.intent.getBooleanExtra("shouldShowWhatsB", false)
        val shouldShowSaved = activity.intent.getBooleanExtra("shouldShowSaved", false)
        if (shouldShowWhats) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.parse(
                            WAStatusActivity.filePathsPhotos!![position]
                        )
                    )
                )
            } else {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.fromFile(
                            File(
                                WAStatusActivity.filePathsPhotos!![position]
                            )
                        )
                    )
                )
            }
        } else if (shouldShowWhatsB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.parse(
                            WABStatusActivity.filePathsPhotos!![position]
                        )
                    )
                )
            } else {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.fromFile(
                            File(
                                WAStatusActivity.filePathsPhotos!![position]
                            )
                        )
                    )
                )
            }
        } else if (shouldShowSaved) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.parse(
                            SavedStatusActivity.filePathsPhotos!![position]
                        )
                    )
                )
            } else {
                imageView.setImageBitmap(
                    (itemView.context as ImagePreviewActivity).getBitmapOnAndroidQ(
                        Uri.fromFile(
                            File(
                                SavedStatusActivity.filePathsPhotos!![position]
                            )
                        )
                    )
                )
            }
        }
        iconPlayer.visibility = View.GONE
        container.addView(itemView)

        return itemView

    }

    override fun getCount(): Int {
        val shouldShowWhats = activity.intent.getBooleanExtra("shouldShowWhats", false)
        val shouldShowWhatsB = activity.intent.getBooleanExtra("shouldShowWhatsB", false)
        val shouldShowSaved = activity.intent.getBooleanExtra("shouldShowSaved", false)
        return when {
            shouldShowWhats -> {
                WAStatusActivity.filePathsPhotos!!.size
            }
            shouldShowWhatsB -> {
                WABStatusActivity.filePathsPhotos!!.size
            }
            shouldShowSaved -> {
                SavedStatusActivity.filePathsPhotos!!.size
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