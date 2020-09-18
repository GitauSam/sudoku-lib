package phosphor.sudoku.lib.solver

import phosphor.sudoku.lib.constants.*
import phosphor.sudoku.lib.constants.GRID_SIZE_SQUARE_ROOT
import phosphor.sudoku.lib.constants.MAX_DIGIT_INDEX
import phosphor.sudoku.lib.constants.MAX_DIGIT_VALUE
import phosphor.sudoku.lib.constants.MIN_DIGIT_INDEX
import phosphor.sudoku.lib.constants.MIN_DIGIT_VALUE

internal object Solver {

    lateinit var grid: Array<IntArray>

    fun solvable(grid: Array<IntArray>): Boolean {
        this.grid = grid.copy()

        return solve()
    }

    private fun solve(): Boolean {
        for (i in 0 until GRID_SIZE) {
            for (j in 0 until GRID_SIZE) {
                if (grid[i][j] == 0) {
                    val availableDigits = getAvailableDigits(i, j)
                    for (k in availableDigits) {
                        grid[i][j] = k
                        if (solve()) {
                            return true
                        }
                        grid[i][j] = 0
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

    private fun getAvailableDigits(row: Int, column: Int): Iterable<Int> {
        val digitsRange = MIN_DIGIT_VALUE..MAX_DIGIT_VALUE
        var availableDigits = mutableSetOf<Int>()
        availableDigits.addAll(digitsRange)

        truncateDigitsAlreadyUsedInRow(availableDigits, row)
        if (availableDigits.size > 1) {
            truncateDigitsAlreadyUsedInColumn(availableDigits, column)
        }

        if (availableDigits.size > 1) {
            truncateDigitsAlreadyUsedInBox(availableDigits, row, column)
        }

        return availableDigits.asIterable()
    }

    private fun truncateDigitsAlreadyUsedInRow(availableDigits: MutableSet<Int>, row: Int) {
        for (i in MIN_DIGIT_INDEX..MAX_DIGIT_INDEX) {
            if (grid[row][i] != 0) {
                availableDigits.remove(grid[row][i])
            }
        }
    }

    private fun truncateDigitsAlreadyUsedInColumn(availableDigits: MutableSet<Int>, column: Int) {
        for (i in MIN_DIGIT_INDEX..MAX_DIGIT_INDEX) {
            if (grid[i][column] != 0) {
                availableDigits.remove(grid[i][column])
            }
        }
    }

    private fun truncateDigitsAlreadyUsedInBox(availableDigits: MutableSet<Int>, row: Int, column: Int) {
        val rowStart = findBoxStart(row)
        val rowEnd = findBoxEnd(rowStart)
        val columnStart = findBoxStart(column)
        val columnEnd = findBoxEnd(columnStart)

        for (i in rowStart until rowEnd) {
            for (j in columnStart until columnEnd) {
                if (grid[i][j] != 0) {
                    availableDigits.remove(grid[i][j])
                }
            }
        }
    }

    private fun findBoxStart(index: Int) = index - index % GRID_SIZE_SQUARE_ROOT

    private fun findBoxEnd(index: Int) = index + BOX_SIZE - 1
}