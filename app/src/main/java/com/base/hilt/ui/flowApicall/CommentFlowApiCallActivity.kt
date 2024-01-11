package com.base.hilt.ui.flowApicall

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.base.hilt.databinding.ActivityCommentFlowApiCallBinding
import com.base.hilt.network.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentFlowApiCallActivity : AppCompatActivity() {


    private lateinit var viewModel: CommentViewModel


    private lateinit var binding: ActivityCommentFlowApiCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityCommentFlowApiCallBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(this).get(CommentViewModel::class.java)


        // Listen for the button click event to search
        binding.button.setOnClickListener {


            if (binding.searchEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, "Query Can't be empty", Toast.LENGTH_SHORT).show()
            } else {

                viewModel.getNewComment(binding.searchEditText.text.toString().toInt())
            }
        }

        lifecycleScope.launch {

            viewModel.commentState.collect {


                when (it.status) {


                    Status.LOADING -> {
                        binding.progressBar.isVisible = true
                    }

                    Status.SUCCESS -> {
                        binding.progressBar.isVisible = false


                        it.data?.let { comment ->
                            binding.commentIdTextview.text = comment.id.toString()
                            binding.nameTextview.text = comment.name
                            binding.emailTextview.text = comment.email
                            binding.commentTextview.text = comment.comment
                        }
                    }

                    else -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(this@CommentFlowApiCallActivity, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
