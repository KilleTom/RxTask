package com.ypz.killetom.librxtask.base

import com.ypz.killetom.librxtask.exception.RxTaskCancelException

abstract class ISuperEvaluationTask<RESULT> : IEvaluation<RESULT>, ISuperTask<RESULT>() {

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
    abstract fun evaluationAction(): RESULT

    protected var resultAction: ((RESULT) -> Unit)? = null

    open fun successAction(resultAction: (RESULT) -> Unit): ISuperEvaluationTask<RESULT> {

        this.resultAction = resultAction

        return this
    }

    protected var failAction: ((Throwable) -> Unit)? = null

    open fun failAction(failAction: (Throwable) -> Unit): ISuperEvaluationTask<RESULT> {

        this.failAction = failAction

        return this
    }


    // 计算生成Result
    override fun evaluationResult(): RESULT {
        return evaluationAction()
    }

    //内部错误回调
    protected fun errorAction(t: Throwable) {

        if (t is RxTaskCancelException) {

            TASK_CURRENT_STATUS = CANCEL_STATUS

        } else {

            if (TASK_CURRENT_STATUS == RUNNING_STATUS){
                failAction?.invoke(t)
            }

            TASK_CURRENT_STATUS = ERROR_STATUS

        }

        finalResetAction()
    }

    //结果回调
    protected fun resultAction(result: RESULT) {

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {

            resultAction?.invoke(result)

            TASK_CURRENT_STATUS = DONE_STATUS

        }

        finalResetAction()
    }


}