package com.example.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudoku.game.SudokuGame

// class untuk view model sudoku
class SudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}