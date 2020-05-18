package com.ypz.killetom.rxjava3.lib_rxtask_android.init

import com.ypz.killetom.librxtask.base.RxTaskLogManager
import com.ypz.killetom.librxtask.scheduler.RxTaskSchedulerManager
import com.ypz.killetom.rxjava3.lib_rxtask_android.log_action.RxAndroidDefaultLogAction
import com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler.RxAndroidDefaultScheduler

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.rxjava3.lib_rxtask_android.init
 * @ClassName: RxTaskAndroidDefaultInit
 * @Description: 针对android平台进行初始化
 * @Author: KilleTom
 * @CreateDate: 2020/5/13 16:04
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 15:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
class RxTaskAndroidDefaultInit private constructor() {

    fun defaultInit() {
        RxTaskSchedulerManager.setLocalScheduler(RxAndroidDefaultScheduler())
        RxTaskLogManager.instants.set(RxAndroidDefaultLogAction.instant)
    }

    companion object {
        val instant by lazy { RxTaskAndroidDefaultInit() }
    }
}