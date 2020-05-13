package com.ypz.killetom.librxtask.base

import io.reactivex.rxjava3.core.Scheduler

interface RxTaskScheduler {

     fun getObserveScheduler(): Scheduler

     fun getSubscribeScheduler(): Scheduler
}