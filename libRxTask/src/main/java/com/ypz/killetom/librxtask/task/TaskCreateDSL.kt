package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.RxTaskScheduler
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager

/**
 * 利用SingleEvaluation接口拓展构建task
 * */
fun <RESULT> RxSingleEvaluationTask.SingleEvaluation<RESULT>.getTask(
    rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
): RxSingleEvaluationTask<RESULT> {
    val task = RxSingleEvaluationTask.createTask({ task ->
        return@createTask this.evluation(task)
    }, rxTaskScheduler)

    return task
}

/**
 * 利用(RxSingleEvaluationTask<*>) -> RESULT这种方式构建singletask
 * */
fun <RESULT> ((RxSingleEvaluationTask<*>) -> RESULT).createSingleTask(
    rxTaskScheduler: RxTaskScheduler = RxTaskSchedulerManager.getLocalScheduler()
): RxSingleEvaluationTask<RESULT> {


    val task = RxSingleEvaluationTask.createTask(this, rxTaskScheduler)

    return task
}