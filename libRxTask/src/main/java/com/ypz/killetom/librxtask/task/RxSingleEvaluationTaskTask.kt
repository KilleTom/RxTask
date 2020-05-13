package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.ISuperEvaluationTask
import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.exception.RxTaskCancelException
import com.ypz.killetom.librxtask.exception.RxTaskRunningException
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable

class RxSingleEvaluationTaskTask<RESULT>
internal constructor(
    private val runnable: (RxSingleEvaluationTaskTask<RESULT>) -> RESULT,
    private val taskScheduler: RxTaskScheduler
) : ISuperEvaluationTask<RESULT>() {

    private val resultTask: Maybe<RESULT>
    private var disposable: Disposable? = null


    init {
        resultTask = Maybe.create<RESULT> { emitter ->

            try {

                if (TASK_CURRENT_STATUS == CANCEL_STATUS) {
                    emitter.onError(RxTaskCancelException("Task cancel"))
                }

                val action = evaluationAction()

                if (TASK_CURRENT_STATUS == CANCEL_STATUS) {
                    emitter.onError(RxTaskCancelException("Task cancel"))
                }

                emitter.onSuccess(action)

            } catch (e: Exception) {

                emitter.onError(e)
            }
        }
    }

    override fun evaluationAction(): RESULT {

        if (!running())
            throw RxTaskRunningException(
                "Task unRunning"
            )

        return runnable.invoke(this)
    }

    override fun start() {
        super.start()

        disposable = resultTask
            .subscribeOn(taskScheduler.getSubscribeScheduler())
            .observeOn(taskScheduler.getObserveScheduler())
            .subscribe(
                { resultAction(it) },
                { errorAction(it) }
            )
    }

    override fun cancel() {

        TASK_CURRENT_STATUS = CANCEL_STATUS

        finalResetAction()
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

    companion object {

        fun <RESULT> createTask(
            taskRunnable:
                (RxSingleEvaluationTaskTask<*>) -> RESULT)
                : RxSingleEvaluationTaskTask<RESULT> {
            return RxSingleEvaluationTaskTask(taskRunnable, RxTaskSchedulerManager.getLocalScheduler())
        }

        fun <RESULT> createTask(
            taskRunnable:
                (RxSingleEvaluationTaskTask<*>) -> RESULT,
            rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
        )
                : RxSingleEvaluationTaskTask<RESULT> {
            return RxSingleEvaluationTaskTask(taskRunnable,rxTaskScheduler)
        }
    }
}