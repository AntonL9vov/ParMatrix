package org.matrix.parallel

import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService

class DeterminantParallel : Callable<Double> {
    var matrix: Matrix? = null
    var column = 0

    @Throws(Exception::class)
    override fun call(): Double {
        return BeginDet(matrix, column)
    }

    constructor(): super()

    constructor(matrix: Matrix?,column: Int){
        this.matrix = matrix
        this.column = column
    }

    fun DetNxNParallel(matrix: Matrix, threadPool: ExecutorService?): Double {
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        } else {
            threadPool
        }

        val size = matrix.getRow(0).size
        if (size == 1) {
            return matrix.getElem(0,0)
        }

        val vals = ArrayList<Future<Double>>()
        for (i in 0 until size) {
            val d = DeterminantParallel(matrix, i)
            val fut = threadPool1!!.submit(d)
            vals.add(fut)
        }
        var det = 0.0
        for (j in vals.indices) {
            try {
                if (j % 2 == 0) {
                    det += vals[j].get()
                } else {
                    det -= vals[j].get()
                }
            } catch (exc: Exception) {
                System.err.println(exc)
            }
        }
        return det
    }

    fun BeginDet(matrix: Matrix?, column: Int): Double {
        val newMatrix = SubMatrix(column, matrix)
        return matrix!!.getElem(column, 0) * DetNxNParallel(newMatrix, null)
    }

    fun SubMatrix(column: Int, matrix: Matrix?): Matrix {
        var row = 0
        var col = 0
        val size = matrix!!.getRow(0).size - 1
        val sub = MatrixParallel(size, size)
        for (i in matrix.getRow(0).indices) {
            if (i != column) {
                for (j in 1 until matrix.getRow(0).size) {
                    sub.setElem(col, row, matrix.getElem(i, j))
                    row++
                }
                col++
            }
            row = 0
        }
        return sub
    }
}

fun main(args: Array<String>) {
    val x = MatrixParallel(4,4, false, 150.0)
    println(x)
    val startTime = System.currentTimeMillis()
    val det = DeterminantParallel().DetNxNParallel(x, null)
    val endTime = System.currentTimeMillis()
    val executeTimeMS = endTime - startTime
    println(executeTimeMS)
    println(det)
}