package org.matrix.parallel

import org.matrix.common.IfaceLUDecompose
import org.matrix.common.Matrix
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.swing.text.html.HTML.Tag.U


class LUDecomposeParallel1D : IfaceLUDecompose {
    override fun LUDecompose(A: Matrix?, threadPool: ExecutorService?): Array<Matrix?>? {
        if(A!!.getNumRows() != A.getNumColumns()){
            return null
        }
        var threadPool1: ExecutorService? = null
        threadPool1 = if(threadPool === null){
            Executors.newCachedThreadPool()
        }else{
            threadPool
        }
        val n = A.getNumRows()
        val l = MatrixParallel(n, n)
        val u = MatrixParallel(n, n)

        for (i in 0 until n){
            for (j in 0 until n){
                u.setElem(i, j, A.getElem(i, j))
            }
        }

        for (i in 0 until n) {
            val s = Runnable {
                for (j in i until n) {
                    l.setElem(j, i, u.getElem(j,i) / u.getElem(i, i))
                }
            }
            threadPool1!!.execute(s)
        }

        for (k in 1 until n) {
            val s = Runnable {
                for (i in k - 1 until n) {
                    for (j in i until n) {
                        l.setElem(j, i, u.getElem(j, i) / u.getElem(i, i))
                    }
                }
                for (i in k until n) {
                    for (j in k - 1 until n) {
                        u.setElem(i, j, u.getElem(i, j) - l.getElem(i, k-1) * u.getElem(k-1, j))
                    }
                }
            }
            threadPool1!!.execute(s)
        }

        return arrayOf(u, l)
    }

}

fun main(){
    val x = MatrixParallel(3,3, false, 150.0)
    val y = LUDecomposeParallel1D()

    println(x)

    val a = y.LUDecompose(x, null)

    println(a!![0])
    println(a[1])
}