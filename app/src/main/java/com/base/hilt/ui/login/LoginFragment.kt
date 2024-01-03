package com.base.hilt.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.base.hilt.R
import com.base.hilt.base.FragmentBase
import com.base.hilt.base.ToolbarModel
import com.base.hilt.databinding.FragmentLoginBinding
import com.base.hilt.network.GraphQLResponseHandler
import com.base.hilt.utils.MyPreference
import com.base.hilt.utils.PrefKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : FragmentBase<LoginViewModel, FragmentLoginBinding>() {

    @Inject
    lateinit var myPreference: MyPreference

    override fun getLayoutId(): Int {
        return R.layout.fragment_login
    }

    override fun setupToolbar() {
        viewModel.setToolbarItems(ToolbarModel(true, "Login", true))
    }

    override fun initializeScreenVariables() {

        getDataBinding().viewModel = viewModel
        observeData()
    }

    private fun observeData() {
        viewModel.loginResponse.observe(this, Observer {
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
                    myPreference.setValueString(PrefKey.EMAIL, it.value!!.data!!.login!!.email!!)

                    myPreference.setValueString(
                        PrefKey.ACCESS_TOKEN,
                        it.value.data?.login?.token!!
                    )

                    myPreference.setValueString(PrefKey.ID, it.value.data?.login?.id!!)

                    myPreference.setValueBoolean(PrefKey.IS_LOGGED_IN, true)

                    Toast.makeText(requireContext(), "Login Successfully", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())



//                    dataBinding.root.findNavController().navigate(R.id.navigation_home)
                }
            }
        })
    }

    override fun getViewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java


}