package com.example.sudoku.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityMainBinding
import com.example.sudoku.game.Cell
import com.example.sudoku.view.custom.SudokuBoardView
import com.example.sudoku.viewmodel.SudokuViewModel



class MainActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    //inisialisasi variabel
    private lateinit var viewModel: SudokuViewModel
    private lateinit var numberButtons: List<Button>

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //binding registerListener
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.sudokuBoardView.registerListener(this)
        setContentView(binding.root)

        // mendapatkan viewModel
        viewModel = ViewModelProvider(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.highlightedKeysLiveData.observe(this, Observer { updateHighlightedKeys(it) })

        // mengisikan variabel sesuai id dari tombol yang ada
        val oneButton:TextView = findViewById(R.id.oneButton)
        val twoButton:TextView = findViewById(R.id.twoButton)
        val threeButton:TextView = findViewById(R.id.threeButton)
        val fourButton:TextView = findViewById(R.id.fourButton)
        val fiveButton:TextView = findViewById(R.id.fiveButton)
        val sixButton:TextView = findViewById(R.id.sixButton)
        val sevenButton:TextView = findViewById(R.id.sevenButton)
        val eightButton:TextView = findViewById(R.id.eightButton)
        val nineButton:TextView = findViewById(R.id.nineButton)

        // membuat list dari button button
        val numberButtons = listOf(oneButton,twoButton,threeButton,fourButton,fiveButton,sixButton,sevenButton,eightButton,nineButton)

        // memberikan aksi saat tombol ditekan
        numberButtons.forEachIndexed { index, button->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index+1) }
        }

        // memberikkan aksi pada tombol notes
        val notesButton:ImageButton = findViewById(R.id.notesButton)
        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState()}

        // memberikan aksi pada tombol delete
        val deleteButton : ImageButton =findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener{viewModel.sudokuGame.delete()}
    }

    // fungsi untuk memperbarui cell
    private  fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    //fungsi untuk memperbarui UI cell yang aktif
    private fun updateSelectedCellUI(cell:Pair<Int,Int>?) = cell?.let {
        binding.sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    // fungsi yang memberikan UI pada note
    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val notesButton:ImageButton = findViewById(R.id.notesButton)
        val color = if (it) {
            ContextCompat.getColor(this, androidx.appcompat.R.color.primary_material_light)
        } else {
            Color.LTGRAY
        }
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    // fungsi untuk meng-highlight
    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed {index, button ->
            val color = if(set.contains(index+1)) {
                ContextCompat.getColor(this, androidx.appcompat.R.color.primary_material_light)
            }  else {
                Color.LTGRAY
            }
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    // fungsi ketika cell ditekan
    override fun onCellTouched(row:Int, col:Int) {
        viewModel.sudokuGame.updateSelectedCell(row,col)
    }
}