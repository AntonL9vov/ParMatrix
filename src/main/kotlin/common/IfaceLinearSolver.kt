package org.matrix.common

interface IfaceLinearSolver {
    fun linearSolver(A: Matrix?, B: Matrix?): Matrix?
}