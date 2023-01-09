package org.matrix.common

import org.matrix.parallel.MatrixParallel
import java.io.*
import java.util.*
import kotlin.random.Random


//This is just an abstact class
//See concrete class like MatrixSeq
abstract class Matrix {
    @Volatile
    lateinit var matrix: Array<DoubleArray>
    protected var mOpAdd: IfaceAdd? = null
    protected var mOpMult: IfaceMult? = null
    protected var mOpDeterminant: IfaceDeterminant? = null
    protected var mOpInverse: IfaceInverse? = null
    protected var mOpLinearSolver: IfaceLinearSolver? = null
    protected var mOpLUDecompose: IfaceLUDecompose? = null

    //
    //Constructor
    //
    constructor(rows: Int, columns: Int) {
        matrix = Array(rows) { DoubleArray(columns) }
    }

    constructor(rows: Int, columns: Int, zerod: Boolean, max: Double) {
        matrix = Array(rows) { DoubleArray(columns) }
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                if (zerod) {
                    matrix[row][column] = 0.0
                } else {
                    matrix[row][column] = Random.nextDouble(0.0, max)
                }
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0 until getNumRows()) {
            for (j in 0 until getNumColumns()) {
                sb.append(matrix[i][j].toString() + " ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (other !is Matrix) return false
        val otherMatrix = other

        for (i in matrix.indices) {
            for (j in matrix[0].indices) {
                val diff = Math.abs(matrix[i][j]) - otherMatrix.getElem(i, j)
                if (diff > 0.00001) {
                    println(
                        i.toString() + "," + j + " don't match. "
                                + matrix[i][j] + " " + otherMatrix.getElem(i, j)
                    )
                    return false
                }
            }
        }
        return true
    }

    //
    //Operations
    //
    //Adds this matrix with B and returns the results
    //Returns If matrix B dimensions don't match, then returns null
    fun add(B: Matrix?): Matrix? {
        return mOpAdd!!.add(this, B, null)
    }

    //Multiplies this matrix with B
    //Returns null if number of colums in B don't match number of rows in this matrix
    //Otherwise, returns multiplied matrix
    fun multiply(B: Matrix?): Matrix? {
        return mOpMult!!.multiply(this, B, null)
    }

    //Finds the inverse of this matrix
    //Returns null if matrix is not square or invertible
    //Otherwise, returns inverse
    fun inverse(): Matrix? {
        return mOpInverse!!.inverse(this)
    }

    //Finds the determinant of this matrix
    //Returns throws exception if matrix is not square
    //Otherwise, returns the determinant
    @Throws(Exception::class)
    fun determinant(): Double {
        if (!isSquare()) {
            println("The not square matrix is: \n$this")
            throw Exception("Need square matrix.") //todo define matrix exception
        } else {
            return mOpDeterminant!!.determinant(this)
        }
    }

    fun addToElem(i: Int, j: Int, value: Double) {
        matrix[i][j] = matrix[i][j] + value
    }

    //Decomposes the matrix into Lower-Triangular and Upper Triangular
    //Returns null if no decomposition if found
    //Otherwise, returns a pair of matrix objects L and U
    fun LUDecompose(): Array<Matrix?>? {
        return mOpLUDecompose!!.LUDecompose(this, null)
    }

    //
    //Setters
    //
    fun setOpAdd(op: IfaceAdd?) {
        mOpAdd = op
    }

    fun setOpMult(op: IfaceMult?) {
        mOpMult = op
    }

    fun setOpDeterminant(op: IfaceDeterminant?) {
        mOpDeterminant = op
    }

    fun setOpLU(op: IfaceLUDecompose?) {
        mOpLUDecompose = op
    }

    fun setRow(inputRow: DoubleArray, rowID: Int): Boolean {
        if (inputRow.size == matrix[0].size && rowID < matrix.size) {
            matrix[rowID] = inputRow
            return true
        } else {
            return false
        }
    }

    fun setElem(i: Int, j: Int, value: Double): Boolean {
        if (i < getNumRows() && j < getNumColumns()) {
            matrix[i][j] = value
            return true
        } else {
            return false
        }
    }

    fun copy (u: Matrix) : Matrix {
        val result: Matrix = MatrixParallel(getNumRows(), getNumColumns(), true, 0.0)
        for (i in 0 until result.getNumRows()){
            for (j in 0 until result.getNumColumns()){
                result.setElem(i, j, u.getElem(i, j))
            }
        }
        return result
    }

    fun setSubMatrix(i: Int, j: Int, sub: Matrix): Boolean {
        //Todo check if rows and columns don't go over
        var subrow = i
        for (k in 0 until sub.getNumRows()) {
            var subcol = j
            for (m in 0 until sub.getNumColumns()) {
                if (subcol < getNumColumns() && subrow < getNumRows()) {
                    //System.out.println("Successfully set subcol: " + subcol + " and subrow " + subrow + " for matrix\n" + sub.toString());
                    matrix[subrow][subcol] = sub.getElem(k, m)
                    subcol++
                } else {
                    //System.out.println("COULDNT set row: " + subrow + " or subcol: " + subcol + " for matrix \n" + sub.toString());
                }
            }
            subrow++
        }
        return true
    }

    fun setAllElem(elems: String) {
        val lines = elems.split("\\r?\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var rows = 0
        var columns = 0
        var st: StringTokenizer? = null
        st = StringTokenizer(lines[0], " ")
        rows = st.nextToken().toInt()
        columns = st.nextToken().toInt()
        matrix = Array(rows) { DoubleArray(columns) }
        for (i in 1 until lines.size) {
            st = StringTokenizer(lines[i], " ")
            var j = 0
            while (st.hasMoreTokens() && j < columns) {
                matrix[i - 1][j] = st.nextToken().trim { it <= ' ' }.toDouble()
                j++
            }
        }
    }

    //
    //Getters
    //
    fun getNumRows(): Int {
        return matrix.size
    }

    fun getNumColumns(): Int {
        return matrix[0].size
    }

    fun getRow(row: Int): DoubleArray {
        return if (row < getNumRows()) matrix.get(row) else DoubleArray(0) // need to fix this
    }

    fun getColumn(col: Int): DoubleArray {
        val result = DoubleArray(getNumColumns())
        for (x in 0 until getNumRows()) {
            result[x] = matrix[x][col]
        }
        return result
    }

    fun getElem(i: Int, j: Int): Double {
        return matrix[i][j]
    }

    fun isSquare(): Boolean {
        return (getNumRows() == getNumColumns())
    }

    fun writeMatrixToFile(name: String) {
        var inrows: String? = null
        var incols: String? = null
        try {
            inrows = Integer.toString(getNumRows())
            incols = Integer.toString(getNumColumns())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val rows = getNumRows()
        val cols = getNumColumns()
        try {
            val fout = File(inrows + "_" + incols + "_" + name)
            val fos = FileOutputStream(fout)
            val bw = BufferedWriter(OutputStreamWriter(fos))
            bw.write("$rows $cols")
            bw.newLine()
            var sb: StringBuilder? = null
            val generator = Random(System.currentTimeMillis())
            for (j in 0 until rows) {
                sb = StringBuilder()
                for (i in 0 until cols) {
                    sb.append(matrix[j][i].toString() + " ")
                }
                bw.write(sb.toString().trim { it <= ' ' })
                bw.newLine()
            }
            bw.close()
        } catch (e: Exception) {
        }
    }

    companion object {
        fun flipOdd(i: Int): Int {
            return if ((i % 2) == 0) 1 else -1
        }
    }
}