package com.example.sudoku.game

// class yang menyimpan atribut-atribut cell
class Cell(
    val row:Int,
    val col:Int,
    var value:Int,
    var isStartingCell: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf()
    )