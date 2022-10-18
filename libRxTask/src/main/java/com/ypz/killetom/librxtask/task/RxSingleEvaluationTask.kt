package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.ISuperEvaluationTask
import com.ypz.killetom.librxtask.base.ITask
import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.exception.RxTaskCancelException
import com.ypz.killetom.librxtask.exception.RxTaskRunningException
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import com.ypz.killetom.librxtask.scope.ITaskScope
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.task
 * @ClassName: RxSingleEvaluationTask
 * @Description: 关注结果运算的task
 * @Author: KilleTom
 * @CreateDate: 2020/5/8 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
class RxSingleEvaluationTask<RESULT>
internal constructor(
    private val runnable: (RxSingleEvaluationTask<RESULT>) -> RESULT,
    private val taskScheduler: RxTaskScheduler
) : ISuperEvaluationTask<RESULT>() {

    private val resultTask: Maybe<RESULT>
    private var disposable: Disposable? = null

    override fun bindScope(scope: ITaskScope?): RxSingleEvaluationTask<RESULT> {
         super.bindScope(scope)
        return this
    }

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

    //运算结果过程
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

    //是否处于启动过程
    override fun running(): Boolean {

        val dis = disposable ?: return false

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            return !dis.isDisposed
        }

        return false
    }

    //最终重置动作
    override fun finalResetAction() {

        disposable?.dispose()
        disposable = null
    }

    //运算抽象接口
    interface SingleEvaluation<RESULT> {

        fun evluation(task: RxSingleEvaluationTask<*>): RESULT

    }

    //添加一个builder 模式便于DSL方式 构建调用
    class Builder internal constructor() {

        private var taskScheduler = RxTaskSchedulerManager.getLocalScheduler()


        fun setTaskSchdeduler(init: Builder.() -> RxTaskScheduler) =
            apply { taskScheduler = init() }


        fun <RESULT> builder(evluation: (RxSingleEvaluationTask<*>) -> RESULT): RxSingleEvaluationTask<RESULT> {

            val task = createTask(evluation, taskScheduler)

            return task

        }
    }


    companion object {

        fun <RESULT> createTask(taskRunnable: (RxSingleEvaluationTask<*>) -> RESULT): RxSingleEvaluationTask<RESULT> {
            return RxSingleEvaluationTask(taskRunnable, RxTaskSchedulerManager.getLocalScheduler())
        }

        fun <RESULT> createTask(
            taskRunnable: (RxSingleEvaluationTask<*>) -> RESULT,
            rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
        ): RxSingleEvaluationTask<RESULT> {
            return RxSingleEvaluationTask(taskRunnable, rxTaskScheduler)
        }

        fun singleBuilder(): Builder {
            return Builder()
        }
    }
}