package com.example.lilinyxzhao_horchatas_boggleapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope


class MainPlayViewModel : ViewModel() {
    private var wordSet = HashSet<String>()
    private val usedWords = HashSet<String>()
    private val _gridLiveData = MutableLiveData<Array<CharArray>>()
    val gridLiveData: LiveData<Array<CharArray>> = _gridLiveData

    // Hardcoded boards from your example
    private val CombOne = arrayListOf("ARELSC", "TABIYL", "EDNSWO", "BIOFXR", "MCDPAE", "IHFYEE", "KTDNUO", "MOQAJB", "ESLUPT", "INVTGE", "ZNDVAE", "UKGELY", "OCATAI", "ULGWIR", "SPHEIN", "MSHARO")
    private val CombTwo = arrayListOf("ASPFFK", "NUIHMQ", "OBJOAB", "LNHNRZ", "AHSPCO", "RYVDEL", "IOTMUC", "LREIXD", "TERWHV", "TSTIYD", "WNGEEH", "ERTTYL", "OWTOAT", "AEANEG", "EIUNES", "TOESSI")
    private val Boards4x4 = arrayListOf(CombOne, CombTwo)
    init {
        downloadDictionary()
    }

    private fun downloadDictionary() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
            var dictionaryContent = ""
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    dictionaryContent = it.readText()
                }
            }
            val wordList = dictionaryContent.split("\n")
            //filter for only 4 letter words as per the game instructions
            val validWords = wordList.filter { it.length >= 4 }.toSet()
            wordSet.addAll(validWords)

            Log.d("GameViewModel", "Filtered wordSet size: ${wordSet.size}")
            wordSet.take(10).forEach { Log.d("GameViewModel", it) }
        }
    }



    fun validateWord(submittedWord: String, callback: (Boolean, Int, String) -> Unit) {
        // Initial checks for specific rules

        if (submittedWord.length < 4) {
            callback(false, -10, "Word must be at least 4 letters long and must be contiguous, -10")
            return
        }

        if (submittedWord.count { it in "AEIOUaeiou" } < 2) {
            callback(false, -10, "Word must contain at least two vowels, -10")
            return
        }

        if (submittedWord in usedWords) {
            callback(false, 0, "This word has already been used")
            return
        }

        val isValidWord = wordSet.contains(submittedWord.lowercase())
        if (!isValidWord) {
            callback(false, -10, "This is not a valid word, -10")
            return
        }


        val points = calculatePointsForWord(submittedWord)
        usedWords.add(submittedWord) // Add to used words if valid
        callback(true, points, "That's correct, +$points")
    }


    private fun calculatePointsForWord(word: String): Int {
        var points = 0
        var basePoints = 0
        word.forEach { char ->
            points += when (char.uppercaseChar()) {
                in "AEIOU" -> 5
                else -> 1
            }
        }
        basePoints = if (word.any { it.uppercaseChar() in "SZPXQ" }) points * 2 else points

        return basePoints
    }
    fun prepareGameGrid() {
        viewModelScope.launch(Dispatchers.Default) {
            val selectedBoard = Boards4x4.random()
            val grid = Array(4) { CharArray(4) }

            selectedBoard.forEachIndexed { index, cube ->
                val row = index / 4
                val col = index % 4
                grid[row][col] = cube.random()
            }

            withContext(Dispatchers.Main) {
                _gridLiveData.value = grid
            }
        }
    }

    fun prepareNewGameGrid(){
        prepareGameGrid()
        Log.d("prepareNewGameGrid", "code reached here and is almost done")

    }

//    fun prepareGameGrid() {
//        // Randomly select one of the predefined boards
//        val selectedBoard = Boards4x4.random()
//
//        // Now selectedBoard is an ArrayList<String> where each string represents a cube with possible letters
//        // You can proceed to place these letters on the grid
//        val grid = Array(4) { CharArray(4) }
//
//        // Populate the grid
//        selectedBoard.forEachIndexed { index, cube ->
//            val row = index / 4
//            val col = index % 4
//            grid[row][col] = cube.random() // Randomly select one letter from the cube
//        }
//
//        // After this, grid is ready and can be passed to LiveData to be observed by the Fragment
//        _gridLiveData.value = grid
//    }


    fun generatePathForWord(word: String, gridSize: Int): List<Int> {
        val path = mutableListOf<Int>()
        val visited = mutableSetOf<Int>()
        var currentPos = (0 until gridSize * gridSize).random()
        path.add(currentPos)
        visited.add(currentPos)

        for (i in 1 until word.length) {
            val possibleMoves = getPossibleMoves(currentPos, gridSize).filter { it !in visited }
            if (possibleMoves.isEmpty()) {
                // No valid moves; consider backtracking or restarting
                return emptyList()
            }
            currentPos = possibleMoves.random()
            path.add(currentPos)
            visited.add(currentPos)
        }

        return path
    }

    // Returns a list of valid next positions from the current position
    fun getPossibleMoves(pos: Int, gridSize: Int): List<Int> {
        val moves = mutableListOf<Int>()
        val row = pos / gridSize
        val col = pos % gridSize

        // Example moves: Right and then down
        listOf(-1, 0, 1).forEach { dr ->
            listOf(-1, 0, 1).forEach { dc ->
                if (dr == 0 && dc == 0) return@forEach
                val newRow = row + dr
                val newCol = col + dc
                if (newRow in 0 until gridSize && newCol in 0 until gridSize) {
                    moves.add(newRow * gridSize + newCol)
                }
            }
        }

        return moves
    }

}
