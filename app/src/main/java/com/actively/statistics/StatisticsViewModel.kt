package com.actively.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actively.R
import com.actively.statistics.usecase.GetStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    getStatisticsUseCase: GetStatisticsUseCase,
    statTabFactory: StatTabFactory
) : ViewModel() {

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val response = getStatisticsUseCase()
            val tabs = response.getOrNull()
            _state.update {
                it.copy(
                    tabs = tabs?.map(statTabFactory::create) ?: it.tabs,
                    isLoading = false,
                    isError = tabs == null
                )
            }
        }
    }

    fun onSelectTab(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    private fun initState() = StatisticsState(
        tabs = listOf(
            StatTab(sport = R.string.cycling),
            StatTab(sport = R.string.running),
            StatTab(sport = R.string.nordic_walking)
        ),
        isLoading = true,
        isError = false
    )
}
