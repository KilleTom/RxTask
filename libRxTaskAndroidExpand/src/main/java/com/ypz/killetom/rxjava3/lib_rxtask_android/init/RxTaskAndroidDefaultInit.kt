package com.ypz.killetom.rxjava3.lib_rxtask_android.init

import com.ypz.killetom.librxtask.base.RxTaskLogManager
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import com.ypz.killetom.rxjava3.lib_rxtask_android.log_action.RxAndroidDefaultLogAction
import com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler.RxAndroidDefaultScheduler

class RxTaskAndroidDefaultInit private constructor() {

    fun defaultInit() {
        RxTaskSchedulerManager.setLocalScheduler(RxAndroidDefaultScheduler())
        RxTaskLogManager.instants.set(RxAndroidDefaultLogAction.instant)
    }

    companion object {
        val instant by lazy { RxTaskAndroidDefaultInit() }
    }
}