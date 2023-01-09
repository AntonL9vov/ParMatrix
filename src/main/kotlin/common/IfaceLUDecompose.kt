package org.matrix.common

import java.util.concurrent.ExecutorService

interface IfaceLUDecompose {
    fun LUDecompose(A: Matrix?, threadPool: ExecutorService?): Array<Matrix?>?
}