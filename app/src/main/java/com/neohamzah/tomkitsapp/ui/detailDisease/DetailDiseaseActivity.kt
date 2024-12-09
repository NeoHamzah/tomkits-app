package com.neohamzah.tomkitsapp.ui.detailDisease

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.neohamzah.tomkitsapp.databinding.ActivityDetailDiseaseBinding
import java.io.File

class DetailDiseaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDiseaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val confidence = intent.getStringExtra(EXTRA_CONFIDENCE)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val diseaseName = intent.getStringExtra(EXTRA_DISEASE_NAME)
//        val productList = intent.getStringExtra(EXTRA_PRODUCT_LIST)
        val solution = intent.getStringExtra(EXTRA_SOLUTION)

        val imagePath = intent.getStringExtra(EXTRA_IMAGE)
        val imageFile = imagePath?.let { File(it) }

// Load the image into the ImageView using Glide or Picasso
        Glide.with(this)
            .load(imageFile)
            .into(binding.ivDisease) // Assuming you have an ImageView with id ivDisease

        binding.tvDetailDisease.text = diseaseName
        binding.tvConfidence.text = confidence
        binding.tvDetailDescription.text = description
        binding.tvDetailSolution.text = solution

    }

    companion object {
        const val EXTRA_CONFIDENCE = "extra_confidence"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_DISEASE_NAME = "extra_disease_name"
        const val EXTRA_IMAGE = "extra_image"
//        const val EXTRA_PRODUCT_LIST
        const val EXTRA_SOLUTION = "extra_solution"

    }
}