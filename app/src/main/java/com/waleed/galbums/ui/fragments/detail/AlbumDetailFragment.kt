package com.waleed.galbums.ui.fragments.detail

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.waleed.galbums.R
import com.waleed.galbums.databinding.FragmentAlbumDetailBinding
import com.waleed.galbums.models.Album
import com.waleed.galbums.ui.fragments.albums.AlbumsFragment
import com.waleed.galbums.ui.fragments.detail.adapters.MediasAdapter
import com.waleed.galbums.utils.extensions.toSerializable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MediasAdapter

    companion object {
        fun newInstance() = AlbumDetailFragment()
    }

    private val viewModel: AlbumDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initArguments()
        initViews()
        initAdapter()
        initClickListeners()
        viewModel.fetchAlbumContentById()
    }

    private fun initViews() = with(binding) {
        tvTitle.text = viewModel.selectedAlbum.name
    }

    private fun initAdapter() = with(binding) {
        adapter = MediasAdapter {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    it.uri
                )
            )
        }

        lifecycleScope.launch {
            viewModel.albumContent
                .collectLatest {
                    binding.albumDetailRecycler.adapter = adapter
                    adapter.submitList(it)
                }
        }
    }

    private fun initClickListeners() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnSwitchView.setOnClickListener {
            val p = albumDetailRecycler.computeVerticalScrollOffset()
            adapter.switchView()
            albumDetailRecycler.layoutManager =
                if (adapter.isGrid()) GridLayoutManager(
                    requireContext(),
                    2
                ) else LinearLayoutManager(requireContext())

            if (adapter.isGrid()) btnSwitchView.setImageResource(R.drawable.ic_list) else btnSwitchView.setImageResource(
                R.drawable.ic_grid
            )
            albumDetailRecycler.scrollToPosition(p)


        }
    }

    private fun initArguments() {
        viewModel.selectedAlbum =
            Json.decodeFromString<Album>(requireArguments().getString(AlbumsFragment.SELECTED_ALBUM_PARAM)!!)
    }
}