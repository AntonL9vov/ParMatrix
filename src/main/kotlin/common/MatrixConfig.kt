package org.matrix.common

//Singleton class for matrix configuration
class MatrixConfig  //Private constructor makes this singleton
private constructor() {
    var maxThreads: Int
        get() = Companion.maxThreads
        //Max Threads
        set(n) {
            Companion.maxThreads = n
        }
    var maxHWThreads: Int
        get() = Companion.maxHWThreads
        //Max threads in HW
        set(n) {
            Companion.maxHWThreads = n
        }

    companion object {
        private var maxThreads = 0
        private var maxHWThreads = 0
        private var self: MatrixConfig? = null
        val matrixConfig: MatrixConfig?
            //Use this to get a handle to MatrixConfig object.
            get() {
                if (self == null) {
                    self = MatrixConfig()
                }
                return self
            }
    }
}