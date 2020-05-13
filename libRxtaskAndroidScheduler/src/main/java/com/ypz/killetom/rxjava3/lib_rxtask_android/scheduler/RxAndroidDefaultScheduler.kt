package com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler

import com.ypz.killetom.rxjava3.lib_rxtask.base.RxTaskScheduler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class RxAndroidDefaultScheduler : RxTaskScheduler {

    override fun getObserveScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun getSubscribeScheduler(): Scheduler {
        return Schedulers.newThread()
    }

}