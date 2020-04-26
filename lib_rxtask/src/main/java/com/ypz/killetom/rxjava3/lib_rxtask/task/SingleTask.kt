package com.ypz.killetom.rxjava3.lib_rxtask.task

import com.ypz.killetom.rxjava3.lib_rxtask.base.RxTaskCancelException
import com.ypz.killetom.rxjava3.lib_rxtask.base.RxTaskEvaluationException
import com.ypz.killetom.rxjava3.lib_rxtask.base.RxTaskRunningException
import com.ypz.killetom.rxjava3.lib_rxtask.base.SuperTask
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SingleTask<Result> : SuperTask<Result>() {

    private val creater: Maybe<Result>
    var disposable: Disposable? = null

    private var evaluationRunnable: ((task: SingleTask<Result>) -> Result)? = null

    init {
        creater = Maybe.create<Result> { emitter ->

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

    override fun evaluationAction(): Result {

        val runnable = evaluationRunnable
            ?: throw RxTaskEvaluationException("not evaluation expression")

        if (!running())
            throw RxTaskRunningException("Task unRunning")

        return runnable.invoke(this)
    }

    override fun start() {
        super.start()

        disposable = creater
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { resultAction(it) },
                { errorAction(it) }
            )

    }

    override fun cancel() {

        TASK_CURRENT_STATUS == CANCEL_STATUS

        disposable?.dispose()

        disposable = null

    }

    override fun running(): Boolean {

        val dis = disposable ?: return false

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            return dis.isDisposed != false
        }

        return false
    }
}