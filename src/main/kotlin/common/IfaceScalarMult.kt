package org.matrix.common

import java.util.concurrent.ExecutorService

interface IfaceScalarMult {
    fun multiply(A: Matrix?, num: Double, threadPool: ExecutorService?): Matrix?
}