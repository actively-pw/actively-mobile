package com.actively.statistics

import androidx.lifecycle.ViewModel
import com.actively.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatisticsViewModel : ViewModel() {

    private val _state = MutableStateFlow(initState())
    val state = _state.asStateFlow()

    fun onSelectTab(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    private fun initState() = StatisticsState(
        tabs = listOf(
            StatTab(sport = R.string.cycling),
            StatTab(sport = R.string.running),
            StatTab(sport = R.string.nordic_waling)
        ),
        isLoading = true
    )

}
