package com.techatonic.sudokututorial.frontend.game

import androidx.lifecycle.MutableLiveData
import com.techatonic.sudokututorial.backend.CreateSudoku
import com.techatonic.sudokututorial.backend.Settings
import com.techatonic.sudokututorial.backend.classic.ClassicSudokuType

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private val board:Board
    private var sudoku:ClassicSudokuType

    init {
        sudoku = when(Settings.selectedSudokuType){
            ClassicSudokuType.SudokuType.Classic -> CreateSudoku.createClassicSudoku()
            ClassicSudokuType.SudokuType.Killer -> CreateSudoku.createKillerSudoku()
            else -> CreateSudoku.createClassicSudoku()
        }

        val cells = mutableListOf<Cell>()

        for (row in 0..8){
            for(col in 0..8){
                if(sudoku.grid[row][col] == 0){
                    cells.add(Cell(row, col, sudoku.grid[row][col], false))
                } else{
                    cells.add(Cell(row, col, sudoku.grid[row][col], true))
                }
            }
        }

        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)

    }

    fun handleInput(number: Int){
        if(selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if(cell.isStartingCell) return

        if(isTakingNotes){
            if(cell.notes.contains(number)){
                cell.notes.remove(number)
            } else{
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row:Int, col:Int){
        val cell = board.getCell(row, col)
        if(cell.isStartingCell){
            return
        }
        selectedRow = row
        selectedCol = col
        selectedCellLiveData.postValue(Pair(row, col))

        if(isTakingNotes){
            highlightedKeysLiveData.postValue(cell.notes)
        }
    }

    fun changeNoteTakingState(){
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if(isTakingNotes) board.getCell(selectedRow, selectedCol).notes else setOf()
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete(){
        val cell = board.getCell(selectedRow, selectedCol)
        if(isTakingNotes){
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else{
            cell.value = 0
        }
        cellsLiveData.postValue(board.cells)
    }

    fun clearSelectedCell() {
        selectedRow = -1
        selectedCol = -1
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
    }

}