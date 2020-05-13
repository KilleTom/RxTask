package com.ypz.killetom.librxtask.scheduler

import com.ypz.killetom.librxtask.base.RxTaskScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class RxDefaultScheduler : RxTaskScheduler {

    override fun getObserveScheduler(): Scheduler {
        return Schedulers.computation()
    }

    override fun getSubscribeScheduler(): Scheduler {
        return Schedulers.newThread()
    }

}