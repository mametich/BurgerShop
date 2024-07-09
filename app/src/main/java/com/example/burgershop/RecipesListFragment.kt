package com.example.burgershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.burgershop.databinding.FragmentListRecipesBinding

class RecipesListFragment : Fragment() {

    private var _binding: FragmentListRecipesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

    private var categoryId: Int? = null
    private var categoryName: String? = null
    private var categoryUrlImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecipesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireArguments().let {
            categoryId = it.getInt(ARG_CATEGORY_ID)
            categoryName = it.getString(ARG_CATEGORY_NAME)
            categoryUrlImage = it.getString(ARG_CATEGORY_IMAGE_URL)
        }
        initRecyclerViewRecipes()
    }

    private fun initRecyclerViewRecipes() {
        val recipeListAdapter = categoryId?.let { STUB.getRecipesByCategoryId(it) }
            ?.let { RecipesListAdapter(it) }

        recipeListAdapter?.setOnRecipeClickListener(object : RecipesListAdapter.OnRecipeClickListener{
            override fun onItemClick(recipeId: Int) {
               openRecipeByRecipeId()
            }
        })
        binding.rvRecipes.adapter = recipeListAdapter
    }

    private fun openRecipeByRecipeId() {
//        val recipeName = STUB.getRecipesByCategoryId(recipeId)[recipeId].title
//        val recipeImgUrl = STUB.getRecipesByCategoryId(recipeId)[recipeId].imageUrl
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipeFragment>(R.id.mainContainer)

        }
    }
}