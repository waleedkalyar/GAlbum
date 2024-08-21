package com.waleed.galbums.ui.fragments.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.waleed.galbums.R
import com.waleed.galbums.databinding.ItemGalleryGridBinding
import com.waleed.galbums.databinding.ItemGalleryListBinding
import com.waleed.galbums.models.GalleryItem
import com.waleed.galbums.models.enums.MediaType
import com.waleed.galbums.utils.extensions.toFileSize


class MediasAdapter(private val onMediaItemClick: (mediaItem: GalleryItem) -> Unit) :
    ListAdapter<GalleryItem, MediasAdapter.MediaViewHolder>(object :
        DiffUtil.ItemCallback<GalleryItem>() {
        override fun areItemsTheSame(
            oldItem: GalleryItem,
            newItem: GalleryItem,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: GalleryItem,
            newItem: GalleryItem,
        ): Boolean {
            return oldItem.name == newItem.name
        }

    }) {


    private var isGrid = true

    inner class MediaViewHolder(private val binding: ViewBinding) :
        ViewHolder(binding.root) {
        fun bind(mediaItem: GalleryItem, type: ViewType) = with(binding) {
            if (type == ViewType.GRID) {
                (binding as ItemGalleryGridBinding).apply {
                    mediaName.text = mediaItem.name
                    mediaSize.text = mediaItem.size.toFileSize()

                    Glide.with(root.context).load(mediaItem.uri)
                        .override(200, 200)
                        .into(albumImage)

                    if (mediaItem.type == MediaType.IMAGE) ivIcon.setImageResource(R.drawable.ic_photo)
                    else ivIcon.setImageResource(R.drawable.ic_video)
                }
            } else if (type == ViewType.LIST) {
                (binding as ItemGalleryListBinding).apply {
                    tvName.text = mediaItem.name
                    tvSize.text = mediaItem.size.toFileSize()

                    Glide.with(root.context).load(mediaItem.uri)
                        .override(200, 200)
                        .into(ivThumb)

                    if (mediaItem.type == MediaType.IMAGE) ivIcon.setImageResource(R.drawable.ic_photo)
                    else ivIcon.setImageResource(R.drawable.ic_video)
                }
            }
            root.setOnClickListener {
                onMediaItemClick.invoke(mediaItem)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(
            if (ViewType.entries.toTypedArray()[viewType] == ViewType.GRID)
                ItemGalleryGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ) else ItemGalleryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position), if (isGrid) ViewType.GRID else ViewType.LIST)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGrid) ViewType.GRID.ordinal else ViewType.LIST.ordinal
    }

    fun switchView() {
        isGrid = !isGrid
    }

    fun isGrid() = isGrid


   enum class ViewType {
        GRID, LIST
    }


}