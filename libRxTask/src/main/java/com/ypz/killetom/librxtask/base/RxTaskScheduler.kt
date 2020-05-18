package com.ypz.killetom.librxtask.base

import io.reactivex.rxjava3.core.Scheduler

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.base
 * @ClassName: RxTaskScheduler
 * @Description: 全局信息输出管理
 * @Author: KilleTom
 * @CreateDate: 2020/5/8 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
interface RxTaskScheduler {

     /**
      * 观察者工作的线程
      * */
     fun getObserveScheduler(): Scheduler

     /**
      * 被观察者工作的线程
      * */
     fun getSubscribeScheduler(): Scheduler
}