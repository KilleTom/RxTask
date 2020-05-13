package com.ypz.killetom.librxtask.base

import java.util.*

class RxTaskLogManager private constructor() {

    private var logAction: RxLogAction = object : RxLogAction {

    }

    fun logD(iSuperTask: ISuperTask<*>, message: String) {
        logAction.d(iSuperTask, message)
    }

    fun set(rxLogAction: RxLogAction) {
        this.logAction = rxLogAction
    }

    companion object {
        val instants by lazy { RxTaskLogManager() }
    }
}

interface RxLogAction {

    fun d(objects: ISuperTask<*>, message: String) {

        System.out.println("${objects::class.java.simpleName}->Message:$message")
    }
}