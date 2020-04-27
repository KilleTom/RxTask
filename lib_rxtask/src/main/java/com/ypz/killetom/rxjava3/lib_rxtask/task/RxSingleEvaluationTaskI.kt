package com.ypz.killetom.rxjava3.lib_rxtask.task

import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskCancelException
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskEvaluationException
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskRunningException
import com.ypz.killetom.rxjava3.lib_rxtask.base.ISuperEvaluation
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RxSingleEvaluationTaskI<RESULT> : ISuperEvaluation<RESULT>() {

    private val resultTask: Maybe<RESULT>
    var disposable: Disposable? = null

    private var evaluationRunnable: ((task: RxSingleEvaluationTaskI<RESULT>) -> RESULT)? = null

    init {
        resultTask = Maybe.create<RESULT> { emitter ->

            try {

                if (TASK_CURRENT_STATUS == CANCEL_STATUS) {
                    emitter.onError(
                        RxTaskCancelException(
                            "Task cancel"
                        )
                    )
                }

                val action = evaluationAction()

                if (TASK_CURRENT_STATUS == CANCEL_STATUS) {
                    emitter.onError(
                        RxTaskCancelException(
                            "Task cancel"
                        )
                    )
                }

                emitter.onSuccess(action)

            } catch (e: Exception) {

                emitter.onError(e)

            }

        }
    }

    override fun evaluationAction(): RESULT {

        val runnable = evaluationRunnable
            ?: throw RxTaskEvaluationException(
                "not evaluation expression"
            )

        if (!running())
            throw RxTaskRunningException(
                "Task unRunning"
            )

        return runnable.invoke(this)
    }

    override fun start() {
        super.start()

        disposable = resultTask
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultAction(it) },
                { errorAction(it) }
            )

    }

    override fun cancel() {

        TASK_CURRENT_STATUS == CANCEL_STATUS



    }

    override fun running(): Boolean {

        val dis = disposable ?: return false

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            return !dis.isDisposed
        }

        return false
    }

    override fun finalResetAction() {

        disposable?.dispose()
        disposable = null

    }
}