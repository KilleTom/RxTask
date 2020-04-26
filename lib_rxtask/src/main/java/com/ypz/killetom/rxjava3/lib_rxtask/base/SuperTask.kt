package com.ypz.killetom.rxjava3.lib_rxtask.base

import android.util.Log
import com.ypz.killetom.rxjava3.lib_rxtask.BuildConfig

abstract class SuperTask<Result> : ITask<Result> {

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

    @Throws
    abstract fun evaluationAction(): Result

    protected var resultAction: ((Result) -> Unit)? = null

    open fun successAction(resultAction: (Result) -> Unit): SuperTask<Result> {

        this.resultAction = resultAction

        return this
    }

    protected var failAction: ((Throwable) -> Unit)? = null

    open fun failAction(failAction: (Throwable) -> Unit): SuperTask<Result> {

        this.failAction = failAction

        return this
    }


    // 计算生成Result
    override fun evaluationResult(): Result {
        return evaluationAction()
    }

    //内部错误回调
    protected fun errorAction(t: Throwable) {

        if (t is RxTaskCancelException) {

            TASK_CURRENT_STATUS = CANCEL_STATUS

        } else {

            TASK_CURRENT_STATUS = ERROR_STATUS

        }

        failAction?.invoke(t)
    }

    //结果回调
    protected fun resultAction(result: Result) {
        resultAction?.invoke(result)
    }

    abstract fun running(): Boolean

    fun throwCancelError() {
        throw RxTaskCancelException("TASK Cancel")
    }

    companion object {

        val NORMAL_STATUS = 0x00

        val RUNNING_STATUS = 0x100

        val CANCEL_STATUS = 0x200

        val ERROR_STATUS = 0x300

    }

    protected fun logD(message: String) {
        if (BuildConfig.DEBUG)
            Log.d(this::class.java.simpleName, message)
    }
}