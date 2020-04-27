package com.ypz.killetom.rxjava3.lib_rxtask.task

import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskCancelException
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskEvaluationException
import com.ypz.killetom.rxjava3.lib_rxtask.exception.RxTaskRunningException
import com.ypz.killetom.rxjava3.lib_rxtask.base.ISuperEvaluationTask
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class RxProgressEvaluationTaskTask<PROGRESS, RESULT> private constructor
    (createRunnable: (RxProgressEvaluationTaskTask<PROGRESS, RESULT>) -> RESULT) :
    ISuperEvaluationTask<RESULT>() {

    private var createRunnable: ((RxProgressEvaluationTaskTask<PROGRESS, RESULT>) -> RESULT)? =
        createRunnable

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
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultAction(it) },
                { errorAction(it) }
            )

        progressDisposable = progressTask
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
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

        val runnable = createRunnable
            ?: throw RxTaskEvaluationException(
                "not evaluation expression"
            )

        if (!running())
            throw RxTaskRunningException(
                "Task unRunning"
            )

        val result = runnable.invoke(this)

        //throw RunningException when result by evaluation
        if (!running())
            throw RxTaskRunningException(
                "Task unRunning"
            )

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
            return RxProgressEvaluationTaskTask(taskRunnable)
        }
    }
}