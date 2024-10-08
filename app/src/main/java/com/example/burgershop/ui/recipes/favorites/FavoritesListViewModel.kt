package com.example.burgershop.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.burgershop.RecipesRepository
import com.example.burgershop.model.Constants
import com.example.burgershop.model.Recipe
import kotlinx.coroutines.launch


class FavoritesListViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository(application.baseContext)

    private val _favoritesUiState = MutableLiveData(FavoritesUiState())
    val favoritesUiState: LiveData<FavoritesUiState> = _favoritesUiState

    fun loadListOfRecipes() {
        val setOfIds = getFavorites().joinToString(",")
        try {
            viewModelScope.launch {
                val recipes = recipesRepository.getRecipesByIds(setOfIds)
                if (recipes.isNotEmpty()) {
                    _favoritesUiState.postValue(
                        _favoritesUiState.value?.copy(
                            listOfFavoriteRecipes = recipes
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MyTag", "Error favorites is null")
            _favoritesUiState.postValue(
                _favoritesUiState.value?.copy(
                    listOfFavoriteRecipes = null
                )
            )
        }
    }


    private fun getFavorites(): MutableSet<String> {
        val sharedPref = application.getSharedPreferences(
            Constants.SHARED_PREF_BURGER_SHOP, Context.MODE_PRIVATE
        )
        return HashSet(
            sharedPref?.getStringSet(Constants.SET_ID, HashSet<String>()) ?: mutableSetOf()
        )
    }

    data class FavoritesUiState(
        val listOfFavoriteRecipes: List<Recipe>? = emptyList(),
    )
}