package com.ypz.killetom.rxjava3.lib_rxtask.base

import io.reactivex.rxjava3.core.Scheduler

interface RxTaskScheduler {

     fun getObserveScheduler(): Scheduler

     fun getSubscribeScheduler(): Scheduler
}