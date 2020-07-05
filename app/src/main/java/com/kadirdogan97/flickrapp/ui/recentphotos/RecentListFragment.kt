package com.kadirdogan97.flickrapp.ui.recentphotos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kadirdogan97.flickrapp.R
import com.kadirdogan97.flickrapp.common.EndlessScrollListener
import com.kadirdogan97.flickrapp.common.Status
import com.kadirdogan97.flickrapp.common.observeNonNull
import com.kadirdogan97.flickrapp.common.runIfNull
import com.kadirdogan97.flickrapp.databinding.FragmentRecentListBinding
import com.kadirdogan97.flickrapp.model.PhotoItem
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Kadir Doğan on 7/5/2020.
 */
class RecentListFragment: Fragment(){
    private val viewModel: VMRecentList by viewModel()
    private var recentListAdapter = RecentListAdapter().apply { }

    private lateinit var _binding: FragmentRecentListBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentListBinding.inflate(inflater, container, false)
        observeAll()
        viewModel.contents_.value.runIfNull {
            fetchRecentPhotos(1)
        }
        initPopularTVShowsRecyclerView()
        return binding.root
    }


    private fun observeAll(){
        viewModel.contents_.observeNonNull(this){
            renderRecents(it)
        }
        viewModel.status_.observeNonNull(this){
            renderStatusResult(it)
        }

    }

    private fun initPopularTVShowsRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context,2)
        binding.recyclerView.apply {
            adapter = recentListAdapter.apply {
                listener = object : RecentListAdapter.ItemClickListener {
                    override fun onItemClick(view: View, photoUrl: String) {
                        findNavController().navigate(
                            R.id.action_recentListFragment_to_recentDetailFragment
                            , bundleOf("PhotoUrlObject" to photoUrl)
                        )
                    }
                }
            }
            layoutManager = gridLayoutManager
            addOnScrollListener(viewModel.scrollListener)
        }
    }
    private fun fetchRecentPhotos(page: Int) {
        viewModel.fetchRecentPhotos(page)
    }

    private fun renderRecents(contents: List<PhotoItem>) {
        recentListAdapter.setRecentPhoto(contents)
    }

    private fun renderStatusResult(statusViewState: RecentStatusViewState) {
        binding.viewState = statusViewState
        binding.executePendingBindings()
    }

}