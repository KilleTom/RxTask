package com.ypz.killetom.librxtask.scheduler

import com.ypz.killetom.librxtask.base.RxTaskScheduler


object RxTaskSchedulerManager {

    private var rxTaskScheduler: RxTaskScheduler = RxDefaultScheduler()

    fun setLocalScheduler(rxTaskScheduler: RxTaskScheduler) {
        RxTaskSchedulerManager.rxTaskScheduler = rxTaskScheduler
    }

    fun getLocalScheduler():RxTaskScheduler{
        return rxTaskScheduler
    }
}