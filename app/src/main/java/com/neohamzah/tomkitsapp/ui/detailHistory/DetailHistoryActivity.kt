package com.neohamzah.tomkitsapp.ui.detailHistory

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.neohamzah.tomkitsapp.ViewModelFactory
import com.neohamzah.tomkitsapp.databinding.ActivityDetailHistoryBinding
import com.neohamzah.tomkitsapp.utils.Result
import com.neohamzah.tomkitsapp.utils.filterString
import com.neohamzah.tomkitsapp.utils.isNetworkAvailable

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding

    private val viewModel by viewModels<DetailHistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(EXTRA_ID)

        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getSession().observe(this) { session ->
                if (session.token.isNotEmpty()) {
                    if (id != null) {
                        viewModel.getHistoryDetail(id, session.token).observe(this) { result ->
                            when (result) {
                                is Result.Error -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@DetailHistoryActivity,
                                        "Failed to load story details",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is Result.Loading -> {
                                    binding.progressBar.visibility = View.VISIBLE
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    val data = result.data

                                    Glide.with(this@DetailHistoryActivity)
                                        .load(data.imageLink)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .listener(object : RequestListener<Drawable> {

                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: Target<Drawable>,
                                                isFirstResource: Boolean,
                                            ): Boolean {
                                                binding.progressBar.visibility = View.GONE
                                                Toast.makeText(
                                                    this@DetailHistoryActivity,
                                                    "Could not load image, check your internet connection",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable,
                                                model: Any,
                                                target: Target<Drawable>?,
                                                dataSource: DataSource,
                                                isFirstResource: Boolean,
                                            ): Boolean {
                                                binding.progressBar.visibility = View.GONE
                                                return false
                                            }
                                        })
                                        .into(binding.ivDisease)

                                    binding.tvDetailDisease.text = data.diseaseName?.let {
                                        filterString(
                                            it
                                        )
                                    }
                                    binding.tvConfidence.text = data.confidence
                                    binding.tvDetailDescription.text = data.description
                                    binding.tvDetailSolution.text = data.solution

                                    if (data.productList.isNullOrEmpty()) {
                                        binding.tvRecommendation.visibility = View.GONE
                                        binding.recyclerView.visibility = View.GONE
                                    } else {
                                        val productAdapter = ProductAdapter { product ->
                                            val productLink = product.productLink
                                            if (!productLink.isNullOrBlank()) {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(productLink))
                                                    startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(
                                                        this@DetailHistoryActivity,
                                                        "Cannot open product link",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            } else {
                                                Toast.makeText(
                                                    this@DetailHistoryActivity,
                                                    "No product link available",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        binding.recyclerView.layoutManager = LinearLayoutManager(
                                            this,
                                            LinearLayoutManager.VERTICAL,
                                            false
                                        )
                                        binding.recyclerView.isNestedScrollingEnabled = false
                                        binding.recyclerView.adapter = productAdapter

                                        productAdapter.submitList(data.productList.mapNotNull { it })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
