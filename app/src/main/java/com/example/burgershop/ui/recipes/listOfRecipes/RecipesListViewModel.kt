package com.example.burgershop.ui.recipes.listOfRecipes

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.burgershop.RecipesRepository
import com.example.burgershop.model.Recipe

class RecipesListViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val _listOfRecipesUiState = MutableLiveData(RecipesUiState())
    val listOfRecipesUiState: LiveData<RecipesUiState> = _listOfRecipesUiState

    fun openRecipesByCategoryId(categoryId: Int) {
        try {
            recipesRepository.getRecipesById(categoryId) { recipes ->
                if (recipes.isNotEmpty()) {
                    _listOfRecipesUiState.postValue(
                        RecipesUiState(
                            listOfRecipes = recipes,
                        )
                    )
                } else {
                    Toast.makeText(application.baseContext, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                    _listOfRecipesUiState.postValue(
                        null
                    )
                }
            }
            recipesRepository.getAllCategories { categories ->
                if (categories.isNotEmpty()) {
                    _listOfRecipesUiState.value = listOfRecipesUiState.value?.copy(
                        titleOfCategories = categories[categoryId].title,
                        imageUrl = categories[categoryId].imgUrl,
                        categoryImage = Drawable.createFromStream(categories[categoryId].imgUrl.let { imgUrl ->
                            application.assets?.open(imgUrl)
                        }, null)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MyTag", "Error listOfRecipes is null")
            _listOfRecipesUiState.postValue(null)
        }
    }

    data class RecipesUiState(
        val listOfRecipes: List<Recipe>? = emptyList(),
        val categoryImage: Drawable? = null,
        val titleOfCategories: String = "",
        val imageUrl: String = "",
    )
}