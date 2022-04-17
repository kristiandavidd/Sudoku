package com.example.sudoku.view.custom

import android.content.Context
import android.graphics.*
import android.graphics.Color.parseColor
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudoku.game.Cell
import kotlinx.coroutines.CoroutineStart

// class yang mengatur tampilan sudoku
class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    //inisialisasi var
    private var sqrtSize = 3
    private var size = 9

    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    private var selectedRow = 0
    private var selectedCol = 0

    private var listener: SudokuBoardView.OnTouchListener? = null

    private var cells: List<Cell>? = null

    //inisialisasi val
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = parseColor("#bbbbbb")
    }

    private val conflictingPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = parseColor("#dddddd")
    }

    private val textPaint = Paint(). apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private val startingCellTextPaint = Paint(). apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        typeface = Typeface.DEFAULT
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = parseColor("#aaaaaa")
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK

    }

    // mengatur ukuran dari cell
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(sizePixels,sizePixels)
    }

    // menuliskan hasil perubahan pada layar
    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)

        fillCells(canvas)
        drawLine(canvas)
        drawText(canvas)
    }

    // memperbarui ukuran pada teks
    private fun updateMeasurements(width: Int) {
        cellSizePixels = (width/size.toFloat())
        noteSizePixels = cellSizePixels /sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellPaint.textSize = cellSizePixels / 1.5F
    }

    // mengatur cell-cell yang dipilih
    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val r = it.row
            val c = it.col

            if(it.isStartingCell) {
                fillCell(canvas, r,c, startingCellPaint)
            } else if(r == selectedRow && c == selectedCol) {
                fillCell(canvas, r,c, selectedCellPaint)
            } else if (r == selectedRow || c == selectedCol) {
                fillCell(canvas, r, c, conflictingPaint)
            } else if (r/sqrtSize == selectedRow/sqrtSize && c/sqrtSize == selectedCol/sqrtSize) {
                fillCell(canvas,r,c,conflictingPaint)
            }
        }
    }

    // mengatur apa yang dilakukan terhadap cell yang dipilih
    private fun fillCell(canvas: Canvas,r: Int, c: Int, paint:Paint) {
        canvas.drawRect(c * cellSizePixels, r * cellSizePixels, (c+1) * cellSizePixels, (r+1) * cellSizePixels, paint)
    }

    // menggambar garis pembatas antar cell, jika kelipatan 3 maka garis lebih tebal
    private fun drawLine(canvas:Canvas) {
        canvas.drawRect(0F,0F, width.toFloat(), height.toFloat(), thickLinePaint)

        for (i in 1 until size) {
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(i * cellSizePixels, 0F, i* cellSizePixels, height.toFloat(), paintToUse)
            canvas.drawLine(0F,i * cellSizePixels, width.toFloat(),i * cellSizePixels, paintToUse)

        }
    }

    // fungsi menampilkan teks ke layar
    private fun drawText(canvas:Canvas) {
        cells?.forEach { cell ->
            val value = cell.value

            val textBounds = Rect()

            if(value == 0) {
                cell.notes.forEach {note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString = note.toString()
                    noteTextPaint.getTextBounds(valueString,0,valueString.length,textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth/2f,
                        (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textWidth/2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString,
                    (col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                    (row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2,
                    paintToUse
                )
            }
        }
    }

    // fungsi yang mengatur ketika ditekan akan melakukan aksi
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x,event.y)
                true
            }
            else -> false
        }
    }

    // fungsi yang mengambil tombol apa yang ditekan
    private fun handleTouchEvent(x:Float,y:Float) {
        val possibleSelectedRow = (y/cellSizePixels).toInt()
        val possibleSelectedCol = (x/cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow,possibleSelectedCol)
    }

    // fungsi yang mengatur UI ketika ditekan
    fun updateSelectedCellUI(row:Int, col:Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    // penyimpan tombol yang ditekan
    fun registerListener(listener:SudokuBoardView.OnTouchListener) {
        this.listener = listener
    }

    //memperbarui cell
    fun updateCells(cells:List<Cell>) {
        this.cells = cells
        invalidate()
    }

    //pendengar tombol yang ditekan
    interface OnTouchListener {
        fun onCellTouched(row:Int,Col:Int)
    }
}