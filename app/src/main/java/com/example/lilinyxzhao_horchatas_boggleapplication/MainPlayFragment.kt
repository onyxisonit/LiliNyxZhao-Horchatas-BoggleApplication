package com.example.lilinyxzhao_horchatas_boggleapplication

import android.content.res.Resources
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat

class MainPlayFragment : Fragment() {

    companion object {
        fun newInstance() = MainPlayFragment()
    }
    private lateinit var gestureDetector: GestureDetector
    private var selectedButtons = mutableListOf<Button>()
    private lateinit var viewModel: MainPlayViewModel
    private lateinit var gameSharedViewModel: ScoreViewModel
    private lateinit var submitButton: Button
    private lateinit var clearButton: Button
    private lateinit var playModeGridLayout: GridLayout
    private lateinit var userEditText: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MainPlayFragment", "onCreateView")
        return inflater.inflate(R.layout.fragment_main_play, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playModeGridLayout = view.findViewById(R.id.playModeGridLayout)
        viewModel = ViewModelProvider(this).get(MainPlayViewModel::class.java)
        gameSharedViewModel = ViewModelProvider(requireActivity()).get(ScoreViewModel::class.java)
        Log.d("MainPlayFragment", "onViewCreated")
        Log.d("onViewCreated", "gameSharedViewModel in main play")
        userEditText = view.findViewById(R.id.userEditText)


        initializeGridLayout(playModeGridLayout)
//        populateGridWithRandomLetters()
//        Log.d("onViewCreated", "populating grid with letters")
        viewModel.gridLiveData.observe(viewLifecycleOwner) { grid ->
            updateGridLayoutWithLetters(grid)
        }
        viewModel.prepareGameGrid()


        // Setup gesture detection
        gestureDetector = GestureDetector(context, GestureListener())
        playModeGridLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true // Consume the event
        }
        Log.d("onViewCreated", "gesture")

        submitButton = view.findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            onSubmitClicked()
        }
        clearButton = view.findViewById(R.id.clearButton)
        clearButton.setOnClickListener {
            onClearClicked()
        }
        Log.d("MainPlayFragment", "onViewCreated, setting up observers")
        gameSharedViewModel.gameResetEvent.observe(viewLifecycleOwner) { shouldReset ->
            Log.d("MainPlayFragment", "gameResetEvent observed: $shouldReset")
            if (shouldReset) {
                Log.d("MainPlayFragment", "reset main play")
                resetGameBoard()
                gameSharedViewModel.onResetGameHandled()
            }
//            Log.d("onViewCreated", "reset main play")
            Log.d("MainPlayFragment", "reset main play")
            resetGameBoard()
        }


    }
    private fun initializeGridLayout(gridLayout: GridLayout) {
        // Assuming each child of the gridLayout is a Button
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
                true // Consume the event
            }
        }
    }
    private fun updateGridLayoutWithLetters(grid: Array<CharArray>) {
        for (i in 0 until playModeGridLayout.childCount) {
            val button = playModeGridLayout.getChildAt(i) as? Button
            val row = i / 4
            val col = i % 4
            button?.text = grid[row][col].toString()
        }
    }

//    fun generateRandomLetter(): Char {
//        val vowels = "AEIOU"
//        val consonants = "BCDFGHJKLMNPQRSTVWXYZ"
//        // Adjust the ratio if needed to ensure a good mix of vowels and consonants
//        return if ((1..5).random() > 2) vowels.random() else consonants.random()
//    }
//
//    fun populateGridWithRandomLetters() {
//        for (i in 0 until playModeGridLayout.childCount) {
//            (playModeGridLayout.getChildAt(i) as? Button)?.text = generateRandomLetter().toString()
//        }
//
//    }
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // Reset the background color of previously selected buttons
            selectedButtons.forEach { it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.aaure_blue)) }
            selectedButtons.clear()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val gridLayout: GridLayout = view?.findViewById(R.id.playModeGridLayout) ?: return false

            // Logic to determine which button is at the position of e2 (the current motion event)
            for (i in 0 until gridLayout.childCount) {
                val button = gridLayout.getChildAt(i) as Button
                if (isMotionEventInsideView(e2, button)) {
                    if (!selectedButtons.contains(button)) {
                        selectedButtons.add(button)
                        button.setBackgroundColor(Color.DKGRAY)

                        val currentText = userEditText.text.toString()
                        userEditText.setText(currentText + button.text.toString())
                    }
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

    private fun isMotionEventInsideView(e: MotionEvent, view: View): Boolean {
        val viewCoords = IntArray(2)
        view.getLocationOnScreen(viewCoords)
        val x = e.rawX.toInt()
        val y = e.rawY.toInt()

        return x >= viewCoords[0] && x <= (viewCoords[0] + view.width) &&
                y >= viewCoords[1] && y <= (viewCoords[1] + view.height)
    }


    private fun onSubmitClicked() {
        selectedButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.aaure_blue))
        }
        val submittedWord = selectedButtons.joinToString(separator = "") { it.text.toString() }
        if(submittedWord.isEmpty()) {
            Toast.makeText(context, "Please select letters to form a word", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.validateWord(submittedWord) { isValid, points, message ->
            gameSharedViewModel.addScore(points)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        userEditText.setText("")
        selectedButtons.clear()
    }

    private fun onClearClicked(){
        selectedButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.aaure_blue))
        }
        userEditText.setText("")
        selectedButtons.clear()
    }

    private fun resetGameBoard() {
        viewModel.prepareNewGameGrid()
        userEditText.setText("")
        selectedButtons.clear()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainPlayViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()
        Log.d("MainPlayFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainPlayFragment", "onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d("MainPlayFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainPlayFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MainPlayFragment", "onDestroyView")
    }


}


