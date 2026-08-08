package com.example.loginproject.ui.category

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.data.remote.response.CategoryDto
import com.example.loginproject.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        getCategories()
    }

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
    }

    fun getCategories() {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                val response = repository.getProducts()

                if (response.isSuccessful) {

                    response.body()?.results
                        ?.map { it.category }
                        ?.distinctBy { it.id }
                        ?.let {
                            _categories.value = it
                        }

                } else {

                    _error.value = "خطا در دریافت دسته بندی"

                }

            } catch (e: Exception) {

                _error.value = e.message

            }

            _isLoading.value = false

        }



    }

}