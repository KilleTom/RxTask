package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager

fun <RESULT> RxSingleEvaluationTask.SingleEvluation<RESULT>.getTask(
    rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
): RxSingleEvaluationTask<RESULT> {
    val task = RxSingleEvaluationTask.createTask({ task ->
        return@createTask this.evluation(task)
    }, rxTaskScheduler)

    return task
}

fun <RESULT> ((RxSingleEvaluationTask<*>) -> RESULT).createSingleTask(
    rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
): RxSingleEvaluationTask<RESULT> {


    val task = RxSingleEvaluationTask.createTask(this, rxTaskScheduler)

    return task
}