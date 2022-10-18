package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.ISuperEvaluationTask
import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.exception.RxTaskCancelException
import com.ypz.killetom.librxtask.exception.RxTaskRunningException
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import com.ypz.killetom.librxtask.scope.ITaskScope
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.task
 * @ClassName: RxProgressEvaluationTask
 * @Description: 带进度的task
 * @Author: KilleTom
 * @CreateDate: 2020/5/8 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
class RxProgressEvaluationTask<PROGRESS, RESULT>
private constructor(
    private val createRunnable: (RxProgressEvaluationTask<PROGRESS, RESULT>) -> RESULT,
    private val rxTaskScheduler: RxTaskScheduler) :
    ISuperEvaluationTask<RESULT>() {

    private val resultTask: Maybe<RESULT>
    private var resultDisposable: Disposable? = null

    private val progressTask: PublishSubject<PROGRESS> = PublishSubject.create<PROGRESS>()
    private var progressDisposable: Disposable? = null

    private var progressAction: ((PROGRESS) -> Unit)? = null

    override fun bindScope(scope: ITaskScope?): RxProgressEvaluationTask<PROGRESS, RESULT> {
        super.bindScope(scope)
        return this
    }

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

    //计算结果
    override fun evaluationAction(): RESULT {

        if (!running())
            throw RxTaskRunningException("Task unRunning")

        val result = createRunnable.invoke(this)

        if (!running())
            throw RxTaskRunningException("Task unRunning")

        return result
    }

    //判断是否运行
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

    //最终操作后的重置
    override fun finalResetAction() {

        resultDisposable?.dispose()
        resultDisposable = null

        progressDisposable?.dispose()
        progressDisposable = null

    }

    //进度推送回调
    fun progressAction(action: (PROGRESS) -> Unit): RxProgressEvaluationTask<PROGRESS, RESULT> {

        progressAction = action

        return this
    }

    //内部通知需要进行进度推送
    fun publishProgressAction(progress: PROGRESS) {

        if (running())
            progressTask.onNext(progress)

    }

    companion object {

        fun <PROGRESS, RESULT> createTask(
            taskRunnable: (RxProgressEvaluationTask<PROGRESS, RESULT>) -> RESULT
        )
                : RxProgressEvaluationTask<PROGRESS, RESULT> {
            return RxProgressEvaluationTask(taskRunnable, RxTaskSchedulerManager.getLocalScheduler())
        }

        fun <PROGRESS, RESULT> createTask(
            taskRunnable: (RxProgressEvaluationTask<PROGRESS, RESULT>) -> RESULT,
            rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
        )
                : RxProgressEvaluationTask<PROGRESS, RESULT> {
            return RxProgressEvaluationTask(taskRunnable,rxTaskScheduler)
        }
    }
}