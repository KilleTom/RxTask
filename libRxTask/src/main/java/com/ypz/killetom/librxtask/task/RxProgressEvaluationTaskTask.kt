package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.ISuperEvaluationTask
import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.exception.RxTaskCancelException
import com.ypz.killetom.librxtask.exception.RxTaskRunningException
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

class RxProgressEvaluationTaskTask<PROGRESS, RESULT>
private constructor(
    private val createRunnable: (RxProgressEvaluationTaskTask<PROGRESS, RESULT>) -> RESULT,
    private val rxTaskScheduler: RxTaskScheduler) :
    ISuperEvaluationTask<RESULT>() {

    private val resultTask: Maybe<RESULT>
    private var resultDisposable: Disposable? = null

    private val progressTask: PublishSubject<PROGRESS> = PublishSubject.create<PROGRESS>()
    private var progressDisposable: Disposable? = null

    private var progressAction: ((PROGRESS) -> Unit)? = null

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

    override fun start() {
        super.start()

        resultDisposable = resultTask
            .subscribeOn(rxTaskScheduler.getSubscribeScheduler())
            .observeOn(rxTaskScheduler.getObserveScheduler())
            .subscribe(
                { resultAction(it) },
                { errorAction(it) }
            )

        progressDisposable = progressTask
            .subscribeOn(rxTaskScheduler.getSubscribeScheduler())
            .observeOn(rxTaskScheduler.getObserveScheduler())
            .subscribe(
                {
                    progressAction?.invoke(it)
                },
                {
                    //nothing to do by error
                }
            )

    }

    override fun evaluationAction(): RESULT {

        if (!running())
            throw RxTaskRunningException("Task unRunning")

        val result = createRunnable.invoke(this)

        if (!running())
            throw RxTaskRunningException("Task unRunning")

        return result
    }

    override fun running(): Boolean {

        val dis = resultDisposable ?: return false

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            return !dis.isDisposed
        }

        return false
    }

    override fun cancel() {

        super.cancel()

        finalResetAction()
    }

    override fun finalResetAction() {

        resultDisposable?.dispose()
        resultDisposable = null

        progressDisposable?.dispose()
        progressDisposable = null

    }

    fun progressAction(action: (PROGRESS) -> Unit): RxProgressEvaluationTaskTask<PROGRESS, RESULT> {

        progressAction = action

        return this
    }

    fun publishProgressAction(progress: PROGRESS) {

        if (running())
            progressTask.onNext(progress)

    }

    companion object {

        fun <PROGRESS, RESULT> createTask(
            taskRunnable: (RxProgressEvaluationTaskTask<PROGRESS, RESULT>) -> RESULT
        )
                : RxProgressEvaluationTaskTask<PROGRESS, RESULT> {
            return RxProgressEvaluationTaskTask(taskRunnable, RxTaskSchedulerManager.getLocalScheduler())
        }

        fun <PROGRESS, RESULT> createTask(
            taskRunnable: (RxProgressEvaluationTaskTask<PROGRESS, RESULT>) -> RESULT,
            rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
        )
                : RxProgressEvaluationTaskTask<PROGRESS, RESULT> {
            return RxProgressEvaluationTaskTask(taskRunnable,rxTaskScheduler)
        }
    }
}