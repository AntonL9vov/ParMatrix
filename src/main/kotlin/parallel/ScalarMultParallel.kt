package org.matrix.parallel

import org.matrix.common.IfaceScalarMult
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScalarMultParallel : IfaceScalarMult, Runnable {
    var cA = 0.0
    var num = 0.0
    var m = 0
    var n = 0

    constructor() : super()
    constructor(cA: Double, num: Double, i: Int, j: Int) : super() {
        this.cA = cA
        this.num = num
        m = i
        n = j
    }

    override fun run() {
        endResult!!.setElem(m, n, cA*num)
    }

    override fun multiply(A: Matrix?, num: Double, threadPool: ExecutorService?): Matrix? {
        val length = A!!.matrix.size
        endResult = MatrixParallel(length, length)
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        }else{
            threadPool
        }
        for (i in 0 until length) {
            for (j in 0 until length) {
                val s = ScalarMultParallel(A.getElem(i,j), num, i, j)
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
    val a = MatrixParallel(3,3, false, 150.0)
    val c = ScalarMultParallel().multiply(a, 2.0, null)
    println(a)
    println()
    println(c)
}