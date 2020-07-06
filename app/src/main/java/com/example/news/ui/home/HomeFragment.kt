package com.example.news.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.databinding.FragmentHomeBinding
import com.example.news.repository.network.Status
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ArticlesAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeAdapter()

        binding.viewModel = viewModel

        subscribeUI()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        return binding.root
    }

    private fun subscribeUI() {
        viewModel.posts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(it.status == Status.RUNNING)
        })

        viewModel.refreshState.observe(viewLifecycleOwner, Observer {
            viewModel.isLoading.set(it.status == Status.RUNNING)
        })
    }

    private fun initializeAdapter() {
        adapter = ArticlesAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        val divider = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.articlesList.adapter = adapter
        binding.articlesList.layoutManager = layoutManager
        binding.articlesList.addItemDecoration(divider)


    }
}