package org.matrix.parallel

import org.matrix.common.IfaceAdd
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddParallel : IfaceAdd, Runnable {
    var cA: Double = 0.0
    var cB: Double = 0.0
    var m = 0
    var n = 0

    constructor()
    constructor(cA: Double, cB: Double, i: Int, j: Int) : super() {
        this.cA = cA
        this.cB = cB
        m = i
        n = j
    }

    override fun run() {
        endResult!!.setElem(m, n, cA + cB)
    }

    override fun add(A: Matrix?, B: Matrix?, threadPool: ExecutorService?): Matrix? {
        if (A!!.matrix.size != B!!.matrix[0].size && A.matrix[0].size != B.matrix.size && A.matrix.size != A.matrix[0].size) return null
        val length = A.matrix.size
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        }else{
            threadPool
        }
        endResult = MatrixParallel(length, length)
        for (i in 0 until length) {
            for (j in 0 until length) {
                val s = AddParallel(A.getElem(i, j), B.getElem(i, j), i, j)
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
    val a = MatrixParallel(2,2, false, 150.0)
    val b = MatrixParallel(2,2, false, 150.0)
    val c = AddParallel().add(a, b, null)
    println(a)
    println()
    println(b)
    println()
    println(c)
}