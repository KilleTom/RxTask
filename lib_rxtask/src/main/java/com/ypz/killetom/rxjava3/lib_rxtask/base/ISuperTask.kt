package com.ypz.killetom.rxjava3.lib_rxtask.base

import android.util.Log
import com.ypz.killetom.rxjava3.lib_rxtask.BuildConfig
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskCancelException
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskRunningException

abstract class ISuperTask<RESULT> :  ITask<RESULT>{

    protected var TASK_CURRENT_STATUS = NORMAL_STATUS

    override fun start() {

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            logD("TASK already start")
            return
        }

        TASK_CURRENT_STATUS = RUNNING_STATUS

    }

    override fun cancel() {

        TASK_CURRENT_STATUS = CANCEL_STATUS

    }

    protected open fun finalResetAction(){

    }

    abstract fun running(): Boolean

    //use this can be judge task can be running, it was throw error when task was stop
    fun judgeRunningError() {

        if (!running()) {

            TASK_CURRENT_STATUS = STOP_STATUS

            throw RxTaskRunningException(
                "task was stop"
            )
        }
    }

    @Throws
    fun throwCancelError() {
        throw RxTaskCancelException(
            "TASK Cancel"
        )
    }

    companion object {

        val NORMAL_STATUS = 0x00

        val RUNNING_STATUS = 0x100

        val CANCEL_STATUS = 0x200

        val ERROR_STATUS = 0x300

        val DONE_STATUS = 0x400

        val STOP_STATUS = 0x500
    }

    protected fun logD(message: String) {

        if (BuildConfig.DEBUG)
            Log.d(this::class.java.simpleName, message)

    }
}