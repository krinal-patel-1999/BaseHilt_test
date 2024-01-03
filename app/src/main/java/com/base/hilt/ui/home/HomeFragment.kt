package com.base.hilt.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.base.hilt.DemoListQuery
import com.base.hilt.R
import com.base.hilt.base.FragmentBase
import com.base.hilt.base.ToolbarModel
import com.base.hilt.bind.GenericRecyclerViewAdapter
import com.base.hilt.databinding.FragmentHomeBinding
import com.base.hilt.databinding.HomeListBinding
import com.base.hilt.network.GraphQLResponseHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :  FragmentBase<HomeViewModel, FragmentHomeBinding>() {

    private var arrayList = arrayListOf<DemoListQuery.Launch>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    override fun setupToolbar() {
        viewModel.setToolbarItems(ToolbarModel(true, "Home", true))
    }

    override fun initializeScreenVariables() {

        getDataBinding().viewModel = viewModel
        observeData()
    }

    private fun observeData() {

        viewModel.demoListResponse.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            when (it) {
                is GraphQLResponseHandler.Loading -> {
                    viewModel.showProgressBar(true)
                }
                is GraphQLResponseHandler.Error -> {
                    viewModel.showProgressBar(false)
                    httpFailedHandler(it.code!!, it.message,null)
                }
                is GraphQLResponseHandler.Success -> {
                    viewModel.showProgressBar(false)
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()

                    arrayList =
                        it.value!!.data!!.launches.launches as ArrayList<DemoListQuery.Launch>
                    loadData()
                }
            }
        })
    }

    private fun loadData() {
        val mAdapter = object :
            GenericRecyclerViewAdapter<DemoListQuery.Launch,HomeListBinding>(
                requireContext(),
                arrayList
            ) {
            override val layoutResId: Int
                get() = R.layout.home_list

            override fun onBindData(
                model: DemoListQuery.Launch,
                position: Int,
                dataBinding: HomeListBinding
            ) {
                dataBinding.model = model
                dataBinding.executePendingBindings()
            }

            override fun onItemClick(model: DemoListQuery.Launch, position: Int) {

            }
        }

        getDataBinding().recyclerView.apply {
            adapter = mAdapter
        }
    }

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java


}