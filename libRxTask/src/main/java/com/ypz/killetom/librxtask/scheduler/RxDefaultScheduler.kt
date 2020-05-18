package com.ypz.killetom.librxtask.scheduler

import com.ypz.killetom.librxtask.base.RxTaskScheduler
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.scheduler
 * @ClassName: RxDefaultScheduler
 * @Description: 默认实现java平台的线程工作类
 * @Author: KilleTom
 * @CreateDate: 2020/5/8 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
class RxDefaultScheduler : RxTaskScheduler {

    override fun getObserveScheduler(): Scheduler {
        return Schedulers.computation()
    }

    override fun getSubscribeScheduler(): Scheduler {
        return Schedulers.newThread()
    }

}