package com.illis.bookfinderapp.util

import androidx.recyclerview.widget.DiffUtil
import com.illis.bookfinderapp.data.model.VolumeInfo

class DataDiffUtil<ItemType>(private val oldData: List<ItemType?>?, private val newData: List<ItemType?>?) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldData?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newData?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldData != null && newData != null) {
            val oldItem = oldData[oldItemPosition]
            val newItem = newData[newItemPosition]

            if (oldItem != null && newItem != null) {
                return when(oldItem) {
                    is VolumeInfo ->
                        oldItem.title == (newItem as VolumeInfo).title
                    else -> oldItem == newItem
                }
            }
        }

        return false
    }

    // false : 전체 갱신, true : 전체 미갱신
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldData != null && newData != null) {
            val oldItem = oldData[oldItemPosition]
            val newItem = newData[newItemPosition]
            if (oldItem != null && newItem != null) {
                return oldItem == newItem
            }
        }
        return true
    }
}