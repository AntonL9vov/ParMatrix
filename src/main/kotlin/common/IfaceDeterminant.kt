package org.matrix.common

interface IfaceDeterminant {
	//You can't return a null so I think we need an exception here
	@Throws(Exception::class)
	fun determinant(A: Matrix?): Double
}