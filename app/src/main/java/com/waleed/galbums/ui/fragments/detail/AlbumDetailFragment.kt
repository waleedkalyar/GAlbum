package com.waleed.galbums.ui.fragments.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.waleed.galbums.R
import com.waleed.galbums.databinding.FragmentAlbumDetailBinding
import com.waleed.galbums.models.Album
import com.waleed.galbums.ui.fragments.albums.AlbumsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumDetailFragment : Fragment() {
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

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
        initClickListeners()
    }

    private fun initClickListeners() = with(binding) {
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initArguments() {
        // viewModel.selectedAlbum = requireArguments().getSerializable(AlbumsFragment.SELECTED_ALBUM_PARAM, Album::class.java) as Album
    }
}