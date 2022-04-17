package com.example.sudoku.game

// class yang mengatur papan sudoku untuk keperluan mengambil cell
class Board(val size: Int, val cells:List<Cell>) {
    fun getCell(row:Int, col:Int) = cells[row * size + col]
}