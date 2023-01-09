package org.matrix.common

import java.util.concurrent.ExecutorService

interface IfaceMult {
    fun multiply(A: Matrix?, B: Matrix?, threadPool: ExecutorService?): Matrix?
}