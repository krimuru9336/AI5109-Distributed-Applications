package com.da.module1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NameState(
    val name: String = "",
    val lastStoredName: String = "No Name Stored Yet",
)

sealed interface NameEvent {
    data class SetName(val name: String) : NameEvent
    data object StoreName : NameEvent
}

class DatabaseViewModel(
    private val dao: NameDao
) : ViewModel() {

    private val _state = MutableStateFlow(NameState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NameState())

    fun onEvent(event: NameEvent) {
        when (event) {
            is NameEvent.SetName -> {
                _state.update { it.copy(name = event.name) }
            }

            NameEvent.StoreName -> {
                val contact = Name(name = state.value.name)
                viewModelScope.launch { dao.storeName(contact) }
                _state.update { it.copy(lastStoredName = state.value.name) }
            }
        }
    }
}