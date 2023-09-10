package com.upscapesoft.whatssaverapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upscapesoft.whatssaverapp.R
import com.upscapesoft.whatssaverapp.activities.ImagePreviewActivity
import com.upscapesoft.whatssaverapp.activities.SavedStatusActivity
import com.upscapesoft.whatssaverapp.fragments.SavedPhotosFragment
import com.upscapesoft.whatssaverapp.helper.AdController
import java.io.File

class SavedPhotosAdapter(var activity: Activity, var fragment: SavedPhotosFragment) :
    RecyclerView.Adapter<SavedPhotosAdapter.ViewHolderKlasse>() {

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
                    SavedStatusActivity.filePathsPhotosChecked!![adapterPosition] = "1"
                } else {
                    iv_selected.isChecked = false
                    itemStateArray.put(adapterPosition, false)
                    SavedStatusActivity.filePathsPhotosChecked!![adapterPosition] = "0"
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
            viewHolderKlasse.iv_play.visibility = View.GONE
            viewHolderKlasse.iv_thumbnail.setImageBitmap(
                (viewHolderKlasse.itemView.context as SavedStatusActivity).getBitmapOnAndroidQ(
                    Uri.parse(
                        SavedStatusActivity.filePathsPhotos!![i]
                    )
                )
            )
        } else {
            viewHolderKlasse.iv_play.visibility = View.GONE
            Glide.with(viewHolderKlasse.iv_thumbnail.context).load(
                File(
                    SavedStatusActivity.filePathsPhotos!![i]
                )
            ).into(viewHolderKlasse.iv_thumbnail)
        }
        viewHolderKlasse.itemView.setOnClickListener { v: View? ->
            AdController.adCounter++
            if (AdController.adCounter == AdController.adDisplayCounter) {
                AdController.showInterAd(activity, null, 0)
            } else {
                Log.e("click", "click")
                val intent = Intent(activity.applicationContext, ImagePreviewActivity::class.java)
                intent.putExtra("imagesPho", SavedStatusActivity.filePathsPhotos!![i])
                intent.putExtra("position", i)
                intent.putExtra("shouldShowSaved", true)
                activity.startActivityForResult(intent, 10)
            }

        }

        viewHolderKlasse.bind(i)

    }

    override fun getItemCount(): Int {
        return SavedStatusActivity.filePathsPhotos!!.size
    }

}