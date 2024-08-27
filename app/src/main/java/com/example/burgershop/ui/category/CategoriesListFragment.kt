package com.example.burgershop.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.example.burgershop.ARG_CATEGORY_ID
import com.example.burgershop.ARG_CATEGORY_IMAGE_URL
import com.example.burgershop.ARG_CATEGORY_NAME
import com.example.burgershop.R
import com.example.burgershop.data.STUB
import com.example.burgershop.databinding.FragmentListCategoriesBinding
import com.example.burgershop.ui.recipes.listOfRecipes.RecipesListFragment


class CategoriesListFragment : Fragment() {

    private var _binding: FragmentListCategoriesBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private val categoriesListViewModel: CategoriesListViewModel by viewModels()
    private val categoriesListAdapter = CategoriesListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListCategoriesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesListViewModel.loadListOfCategory()
        initUI()
    }

    private fun initUI() {
        categoriesListViewModel.categoryListUiState.observe(viewLifecycleOwner) { newCategoryListUiState ->
            categoriesListAdapter.dataset = newCategoryListUiState.listOfCategory
        }

        categoriesListAdapter.setOnItemClickListener(object :
            CategoriesListAdapter.OnItemClickListener {
            override fun onItemClick(categoryId: Int) {
                openRecipesByCategoryId(categoryId)
            }
        })
        binding.rvCategories.adapter = categoriesListAdapter
    }

    fun openRecipesByCategoryId(categoryId: Int) {
        val bundle = bundleOf(
            ARG_CATEGORY_ID to categoryId,
            ARG_CATEGORY_NAME to categoriesListViewModel.loadFromMemoryName(categoryId),
            ARG_CATEGORY_IMAGE_URL to categoriesListViewModel.loadFromMemoryImageUrl(categoryId)
        )
        parentFragmentManager.commit {
            replace<RecipesListFragment>(R.id.mainContainer, args = bundle)
            setReorderingAllowed(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

