package org.matrix.parallel

import org.matrix.common.IfaceMult
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MultParallel : IfaceMult, Runnable {
    lateinit var cA: DoubleArray
    lateinit var cB: DoubleArray
    var m = 0
    var n = 0
    var o = 0

    constructor() : super()
    constructor(cA: DoubleArray, cB: DoubleArray, k: Int, i: Int, j: Int) : super() {
        this.cA = cA
        this.cB = cB
        m = i
        n = j
        o = k
    }

    override fun run() {
        var thisValue = 0.0
        for (p in 0 until o) {
            thisValue = thisValue + cA[p] * cB[p]
        }
        endResult!!.setElem(m, n, thisValue)
    }

    override fun multiply(A: Matrix?, B: Matrix?, threadPool: ExecutorService?): Matrix? {
        if (A!!.matrix.size != B!!.matrix[0].size && A.matrix[0].size != B.matrix.size && A.matrix.size != A.matrix[0].size) return null
        val length = A.matrix.size
        endResult = MatrixParallel(length, length)
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        }else{
            threadPool
        }
        for (i in 0 until length) {
            for (j in 0 until length) {
                val s = MultParallel(A.getRow(i), B.getColumn(j), length, i, j)
                threadPool1!!.execute(s)
            }
        }
        return endResult
    }

    companion object {
        var threadPool = Executors.newFixedThreadPool(50)
        var endResult: Matrix? = null
    }
}

fun main(){
    val a = MatrixParallel(100,100, false, 150.0)
    val b = MatrixParallel(100,100, false, 150.0)
    val c = MultParallel().multiply(a, b, null)
}