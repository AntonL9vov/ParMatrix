package org.matrix.common

import java.util.concurrent.ExecutorService

interface IfaceTranspose {
    fun transpose(A: Matrix?, threadPool: ExecutorService?): Matrix?
}