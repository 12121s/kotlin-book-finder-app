package com.illis.bookfinderapp.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.illis.bookfinderapp.R
import com.illis.bookfinderapp.data.model.VolumeInfo
import com.illis.bookfinderapp.databinding.ItemBookGridBinding
import com.illis.bookfinderapp.databinding.ItemLoadingBinding
import com.illis.bookfinderapp.util.DataDiffUtil


class BookListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var items = ArrayList<VolumeInfo?>()
    private var onItemClickListener : ((VolumeInfo) -> Unit)? = null
    var loadComplete = false

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            null -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBookGridBinding.inflate(layoutInflater, parent, false)
                BooksViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BooksViewHolder){
            items[position]?.let { holder.bind(it) }
        } else if (holder is LoadingViewHolder) {
            holder.loading()
        }
    }

    fun setList(newBookList: MutableList<VolumeInfo?>?) {
        loadComplete = true

        newBookList?.add(null) // progressbar
        val diffCallback: DataDiffUtil<VolumeInfo> = DataDiffUtil(items, newBookList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        if (newBookList != null) {
            items = ArrayList()
            items.addAll(newBookList)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteLoading(){
        Log.d("BookFinder", "deleteLoading ${items.lastIndex}")
        val lastItemIndex = items.lastIndex
        items.removeAt(lastItemIndex)

        notifyItemRemoved(lastItemIndex)
    }

    fun setOnItemClickListener(listener : (VolumeInfo) -> Unit){
        onItemClickListener = listener
    }

    inner class BooksViewHolder(private val binding: ItemBookGridBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(books: VolumeInfo){
            binding.title.text = books.title
            binding.author.text = books.authors?.get(0)
            binding.publishedDate.text = books.publishedDate

            Glide.with(binding.bookThumbnail.context)
                .load(books.imageLinks.thumbnail)
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .fallback(R.drawable.img_loading)
                .fitCenter()
                .into(binding.bookThumbnail)

            binding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(books)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun loading() {
            binding.loading.post {
                binding.loading.isIndeterminate = true
            }
        }
    }
}