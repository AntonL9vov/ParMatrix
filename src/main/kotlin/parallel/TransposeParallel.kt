package org.matrix.parallel

import org.matrix.common.IfaceTranspose
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TransposeParallel : IfaceTranspose, Runnable {
    var cA = 0.0
    var m = 0
    var n = 0

    constructor() : super()
    constructor(cA: Double, i: Int, j: Int) : super() {
        this.cA = cA
        m = j
        n = i
    }

    override fun run() {
        endResult!!.setElem(m, n, cA)
    }

    override fun transpose(A: Matrix?, threadPool: ExecutorService?): Matrix? {
        endResult = MatrixParallel(A!!.getNumRows(), A.getNumColumns())
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        }else{
            threadPool
        }
        for (i in 0 until A.getNumRows()) {
            for (j in 0 until A.getNumColumns()) {
                val s = TransposeParallel(A.getElem(i,j), i, j)
                threadPool1!!.execute(s)
            }
        }
        return endResult
    }

    companion object {
        var endResult: Matrix? = null
    }
}

fun main(){
    val a = MatrixParallel(5,5, false, 150.0)
    val c = TransposeParallel().transpose(a, null)
    println(c)
    println()
    println(a)
}