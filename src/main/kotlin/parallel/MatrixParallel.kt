package org.matrix.parallel
import org.matrix.common.Matrix


class MatrixParallel : Matrix {

    constructor(rows: Int, columns: Int) : super(rows, columns)

    constructor(rows: Int, columns: Int, zerod: Boolean, max: Double) : super(rows, columns, zerod, max)

    fun scalarMultiply(scalar: Double): Matrix {
        val result: Matrix = MatrixParallel(getNumRows(), getNumColumns(), true, 0.0)
        for (i in 0 until getNumRows()) {
            for (j in 0 until getNumColumns()) {
                result.setElem(i, j, scalar * matrix[i][j])
            }
        }
        return result
    }
    fun createSubMatrix(excludeRow: Int, excludeCol: Int): Matrix {
        var r = -1
        val result: Matrix = MatrixParallel(getNumRows() - 1, getNumColumns() - 1, true, 0.0)
        for (i in 0 until getNumRows()) {
            if (i == excludeRow) continue
            r++
            var c = -1
            for (j in 0 until getNumColumns()) {
                if (j == excludeCol) continue
                result.setElem(r, ++c, matrix[i][j])
            }
        }
        return result
    }

    fun transpose(): Matrix {
        val result: Matrix = MatrixParallel(getNumRows(), getNumColumns(), true, 0.0)
        for (i in 0 until getNumRows()) {
            for (j in 0 until getNumColumns()) {
                result.setElem(i, j, matrix[j][i])
            }
        }
        return result
    }
}