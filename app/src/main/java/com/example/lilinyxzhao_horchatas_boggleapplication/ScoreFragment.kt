package com.example.lilinyxzhao_horchatas_boggleapplication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class ScoreFragment : Fragment() {

    companion object {
        fun newInstance() = ScoreFragment()
    }

    //private lateinit var viewModel: ScoreViewModel
    private lateinit var scoreTextView: TextView
    private lateinit var scoreEditText: EditText
    private lateinit var gameSharedViewModel: ScoreViewModel
    private lateinit var newGameButton: Button
    private var currentScore = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_score, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        scoreEditText = view.findViewById(R.id.scoreEditText)
        gameSharedViewModel = ViewModelProvider(requireActivity()).get(ScoreViewModel::class.java)
        Log.d("onViewCreated", "gameSharedViewModel in score frag")
        newGameButton= view.findViewById(R.id.newGameButton)
        newGameButton.setOnClickListener {
            gameSharedViewModel.resetGame()
            Log.d("ScoreFragment, onViewCreated", "reset in score frag")
        }

        gameSharedViewModel = ViewModelProvider(requireActivity()).get(ScoreViewModel::class.java)
        gameSharedViewModel.scoreLiveData.observe(viewLifecycleOwner) { score ->
            scoreEditText.setText(score.toString())
        }


    }

//    private fun onSubmitClicked() {
//        val submittedWord = selectedButtons.joinToString(separator = "") { it.text.toString() }
//        if(submittedWord.isEmpty()) {
//            Toast.makeText(context, "Please select letters to form a word", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        viewModel.validateWord(submittedWord) { isValid, points, message ->
//            if (isValid) {
//                currentScore += points // Add points if the word is valid
//            } else {
//                currentScore += points // Adjust this as necessary for your game rules
//            }
//            scoreTextView.text = "Score: $currentScore" // Update the score display
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//        }
//        resetSelection()
//    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        gameSharedViewModel = ViewModelProvider(this).get(ScoreViewModel::class.java)

    }

}