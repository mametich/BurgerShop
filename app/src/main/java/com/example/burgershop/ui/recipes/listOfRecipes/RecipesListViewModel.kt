package com.example.burgershop.ui.recipes.listOfRecipes

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.burgershop.RecipesRepository
import com.example.burgershop.model.Category
import com.example.burgershop.model.Recipe
import kotlinx.coroutines.launch

class RecipesListViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository(application.baseContext)

    private val _listOfRecipesUiState = MutableLiveData(RecipesUiState())
    val listOfRecipesUiState: LiveData<RecipesUiState> = _listOfRecipesUiState

    fun openRecipesByCategoryId(category: Category) {
        viewModelScope.launch {
            val recipes = recipesRepository.getRecipes(category.id)
            if (recipes.isNotEmpty()) {
                _listOfRecipesUiState.postValue(
                    _listOfRecipesUiState.value?.copy(
                        listOfRecipes = recipes
                    )
                )
            } else {
                _listOfRecipesUiState.postValue(
                    _listOfRecipesUiState.value?.copy(
                        listOfRecipes = null
                    )
                )
            }
        }

        viewModelScope.launch {
            _listOfRecipesUiState.postValue(
                _listOfRecipesUiState.value?.copy(
                    titleOfCategories = category.title,
                    imageUrl = category.imgUrl,
                    categoryImage = category.title
                )
            )

        }
    }

    data class RecipesUiState(
        val listOfRecipes: List<Recipe>? = emptyList(),
        val categoryImage: String? = null,
        val titleOfCategories: String? = "",
        val imageUrl: String? = "",
    )
}
