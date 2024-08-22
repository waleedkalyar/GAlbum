package com.waleed.galbums.ui.fragments.albums

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.waleed.galbums.R
import com.waleed.galbums.databinding.FragmentAlbumsBinding
import com.waleed.galbums.ui.activities.main.MainViewModel
import com.waleed.galbums.ui.fragments.albums.adapters.AlbumsAdapter
import com.waleed.galbums.utils.sealed.DataResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class AlbumsFragment : Fragment() {
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val SELECTED_ALBUM_PARAM = "selected-album-param"
        fun newInstance() = AlbumsFragment()
    }

    private val viewModel: AlbumsViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    lateinit var adapter: AlbumsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAlbums()
        initClickListeners()
    }

    private fun initClickListeners() = with(binding) {
        btnSwitchView.setOnClickListener {
            val position = if (adapter.isGrid()) (recyclerAlbums.layoutManager as GridLayoutManager).findFirstVisibleItemPosition() else (recyclerAlbums.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            adapter.switchView()
            recyclerAlbums.layoutManager =
                if (adapter.isGrid()) GridLayoutManager(
                    requireContext(),
                    2
                ) else LinearLayoutManager(requireContext())

            if (adapter.isGrid()) btnSwitchView.setImageResource(R.drawable.ic_list) else btnSwitchView.setImageResource(
                R.drawable.ic_grid
            )
            recyclerAlbums.scrollToPosition(position)

        }

        btnRetry.setOnClickListener {
            mainViewModel.intiAlbums()
        }
    }

    private fun initAlbums() = with(binding) {
         adapter = AlbumsAdapter {
            findNavController().navigate(
                R.id.action_albumsFragment_to_albumDetailFragment, bundleOf(
                    SELECTED_ALBUM_PARAM to Json.encodeToString(it)
                )
            )
        }
        recyclerAlbums.adapter = adapter

        lifecycleScope.launch {
            mainViewModel.albumsData.collectLatest {
                when(it){
                    is DataResult.Loading -> {
                        constLoading.isVisible = true
                        constError.isVisible = false
                    }

                    is DataResult.Error -> {
                        constLoading.isVisible = false
                        constError.isVisible = true
                    }
                    is DataResult.Success -> {
                        constLoading.isVisible = false
                        constError.isVisible = false
                        adapter.submitList(it.data)
                    }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}