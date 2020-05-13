package com.ypz.killetom.rxjava3.lib_rxtask.scheduler

import com.ypz.killetom.rxjava3.lib_rxtask.base.RxTaskScheduler

object RxTaskSchedulerManager {

    private var rxTaskScheduler: RxTaskScheduler = RxDefaultScheduler()

    fun setLocalScheduler(rxTaskScheduler: RxTaskScheduler) {
        this.rxTaskScheduler = rxTaskScheduler
    }

    fun getLocalScheduler():RxTaskScheduler{
        return rxTaskScheduler
    }
}