package com.upscapesoft.whatssaverapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.activities.VideoPreviewActivity
import com.upscapesoft.whatssaverapp.activities.WABStatusActivity
import com.upscapesoft.whatssaverapp.fragments.WABVideosFragment
import com.upscapesoft.whatssaverapp.helper.AdController
import java.io.File

class WABVideosAdapter(var activity: Activity, var fragment: WABVideosFragment) :
    RecyclerView.Adapter<WABVideosAdapter.ViewHolderKlasse>() {

    var c: Context? = null
    var itemStateArray = SparseBooleanArray()

    inner class ViewHolderKlasse(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_thumbnail: ImageView
        var iv_play: ImageView
        var iv_selected: CheckBox
        fun bind(position: Int) {
            if (!itemStateArray[position, false]) {
                iv_selected.isChecked = false
            } else {
                iv_selected.isChecked = true
            }
        }

        init {
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail)
            iv_play = itemView.findViewById(R.id.iv_play)
            iv_selected = itemView.findViewById(R.id.iv_checkbox)
            c = iv_play.context
            iv_selected.setOnClickListener {
                val adapterPosition = adapterPosition
                if (!itemStateArray[adapterPosition, false]) {
                    iv_selected.isChecked = true
                    itemStateArray.put(adapterPosition, true)
                    WABStatusActivity.filePathsVideosChecked!![adapterPosition] = "1"
                } else {
                    iv_selected.isChecked = false
                    itemStateArray.put(adapterPosition, false)
                    WABStatusActivity.filePathsVideosChecked!![adapterPosition] = "0"
                }
                fragment.checkFAB()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolderKlasse {
        @SuppressLint("InflateParams") val itemView1 =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item, null)
        return ViewHolderKlasse(itemView1)
    }

    override fun onBindViewHolder(viewHolderKlasse: ViewHolderKlasse, i: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            viewHolderKlasse.iv_play.visibility = View.VISIBLE
            Glide.with(viewHolderKlasse.iv_thumbnail.context)
                .load(WABStatusActivity.filePathsVideos!![i]).into(viewHolderKlasse.iv_thumbnail)
        } else {
            viewHolderKlasse.iv_play.visibility = View.VISIBLE
            Glide.with(viewHolderKlasse.iv_thumbnail.context).load(
                getVideoThumbnail(
                    File(
                        WABStatusActivity.filePathsVideos!![i]
                    )
                )
            ).into(viewHolderKlasse.iv_thumbnail)
        }
        viewHolderKlasse.itemView.setOnClickListener { v: View? ->
            AdController.adCounter++
            if (AdController.adCounter == AdController.adDisplayCounter) {
                AdController.showInterAd(activity, null, 0)
            } else {
                Log.e("click", "click")
                val intent = Intent(activity.applicationContext, VideoPreviewActivity::class.java)
                intent.putExtra("videosVid", WABStatusActivity.filePathsVideos!![i])
                intent.putExtra("position", i)
                intent.putExtra("shouldShowWhatsB", true)
                activity.startActivityForResult(intent, 10)
            }

        }
        viewHolderKlasse.iv_selected.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (WABStatusActivity.filePathsVideosChecked!![i] == "0") {
                viewHolderKlasse.iv_selected.visibility = View.VISIBLE
                WABStatusActivity.filePathsVideosChecked!![i] = "1"
            } else {
                viewHolderKlasse.iv_selected.visibility = View.GONE
                WABStatusActivity.filePathsVideosChecked!![i] = "0"
            }
            fragment.checkFAB()
        }

        viewHolderKlasse.bind(i)

    }

    private fun getVideoThumbnail(path: File): Bitmap? {
        return ThumbnailUtils.createVideoThumbnail(
            path.toString(),
            MediaStore.Images.Thumbnails.MINI_KIND
        )
    }

    override fun getItemCount(): Int {
        return WABStatusActivity.filePathsVideos!!.size
    }

}