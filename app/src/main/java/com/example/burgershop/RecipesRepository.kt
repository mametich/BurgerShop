package com.example.burgershop


import android.content.Context
import com.example.burgershop.data.CategoriesDao
import com.example.burgershop.data.CategoryDatabase
import com.example.burgershop.data.RecipesDao
import com.example.burgershop.data.api.RecipeApiService
import com.example.burgershop.model.Category
import com.example.burgershop.model.Constants
import com.example.burgershop.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


class RecipesRepository(
    context: Context
) {
    private val contentType = Constants.CONTENT_TYPE.toMediaType()

    private val logger = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(Json.asConverterFactory(contentType))
        .client(client)
        .build()

    private val serviceApi: RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    private val categoryDao: CategoriesDao =
        CategoryDatabase.getDatabase(context.applicationContext).categoriesDao()
    private val recipesDao: RecipesDao =
        CategoryDatabase.getDatabase(context.applicationContext).recipesDao()

    suspend fun getCategories(): List<Category> {
        var categories = getAllCategoriesFromCache()
        if (categories.isEmpty()) {
            categories = getAllCategoriesFromApi()
            categoryDao.addCategory(categories)
        }
        return categories
    }

    private suspend fun getAllCategoriesFromCache(): List<Category> {
        return categoryDao.getAllCategories()
    }

    private suspend fun getAllCategoriesFromApi(): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val responseCall: Call<List<Category>> = serviceApi.getCategories()
                val categoryResponse: Response<List<Category>>? = responseCall.execute()
                if (categoryResponse?.isSuccessful == true && categoryResponse.body() != null) {
                    categoryResponse.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getRecipes(categoryId: Int): List<Recipe> {
        var recipes = getRecipesFromCache(categoryId)
        if (recipes.isEmpty()) {
            recipes = getRecipesByIdFromApi(categoryId)
            if (recipes.isNotEmpty()) {
                recipes = recipes.map { it.copy(categoryId = categoryId) }
                recipesDao.addRecipes(recipes)
            }
        }
        return recipes
    }

    private suspend fun getRecipesFromCache(categoryId: Int): List<Recipe> {
        return recipesDao.getRecipesById(categoryId)
    }

    private suspend fun getRecipesByIdFromApi(categoryId: Int): List<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val recipesCall = serviceApi.getRecipesById(categoryId)
                val recipesResponse = recipesCall.execute()
                if (recipesResponse.isSuccessful && recipesResponse.body() != null) {
                    recipesResponse.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getRecipesByFavorites(isFavorites: Boolean): List<Recipe> {
        return recipesDao.getFavoritesRecipes(isFavorites)
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipesDao.updateRecipe(recipe)
    }

    suspend fun getRecipeByIdFromCache(id: Int): Recipe {
        return recipesDao.getRecipeById(id)
    }
}



