package org.matrix.common

import java.util.concurrent.ExecutorService

interface IfaceAdd {
    fun add(A: Matrix?, B: Matrix?, threadPool: ExecutorService?): Matrix?
}