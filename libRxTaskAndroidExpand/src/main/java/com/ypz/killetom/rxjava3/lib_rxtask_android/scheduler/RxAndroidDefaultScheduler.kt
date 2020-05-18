package com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler

import com.ypz.killetom.librxtask.base.RxTaskScheduler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler
 * @ClassName: RxAndroidDefaultScheduler
 * @Description: 针对android 默认实现的线程工作类
 * @Author: KilleTom
 * @CreateDate: 2020/5/13 16:04
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 15:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
class RxAndroidDefaultScheduler : RxTaskScheduler {

    override fun getObserveScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun getSubscribeScheduler(): Scheduler {
        return Schedulers.newThread()
    }

}