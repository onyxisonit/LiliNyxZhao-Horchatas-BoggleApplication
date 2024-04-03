package com.example.lilinyxzhao_horchatas_boggleapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreViewModel : ViewModel() {
    private val _scoreLiveData = MutableLiveData<Int>()
    val scoreLiveData: LiveData<Int> = _scoreLiveData
    private val _gameResetEvent = MutableLiveData<Boolean>()
    val gameResetEvent: LiveData<Boolean> = _gameResetEvent

    private var currentScore = 0

    fun addScore(points: Int) {
        currentScore += points
        _scoreLiveData.value = currentScore
    }

    fun resetScore() {

        currentScore = 0
        _scoreLiveData.value = currentScore
    }

    fun resetGame() {
        _scoreLiveData.value = 0 // Reset score
        Log.d("resetGame", "$_scoreLiveData.value")
        Log.d("ScoreViewModel", "resetGame() called, setting gameResetEvent to true")
        _gameResetEvent.value = true // Trigger game reset event
        Log.d("ScoreViewModel", "gameResetEvent value set to true")
        Log.d("resetGame", "$_gameResetEvent.value")
    }

       fun onResetGameHandled() {
        _gameResetEvent.value = false
    }
}
