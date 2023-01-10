package com.illis.bookfinderapp.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.illis.bookfinderapp.R
import com.illis.bookfinderapp.databinding.FragmentBookDetailBinding

class BookDetailFragment : BaseFragment<FragmentBookDetailBinding>(FragmentBookDetailBinding::inflate) {
    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBookDetail()
    }

    private fun setBookDetail() {
        searchViewModel.selectedBook.observe(viewLifecycleOwner) { book ->
            binding.apply {
                bookDetailTitle.text = book.title
                bookDetailAuthor.text = book.authors?.get(0)
                bookDetailPublishedDate.text = book.publishedDate
                bookDetailDescription.text = book.description?: getString(R.string.detail_no_description)
                Glide.with(bookDetailThumbnail.context)
                    .load(book.imageLinks.thumbnail)
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.img_loading)
                    .fallback(R.drawable.img_loading)
                    .fitCenter()
                    .into(bookDetailThumbnail)

                showBookDetailLink.setOnClickListener {
                    val uri = Uri.parse(book.previewLink)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context?.startActivity(intent)
                }
            }
        }
    }
}