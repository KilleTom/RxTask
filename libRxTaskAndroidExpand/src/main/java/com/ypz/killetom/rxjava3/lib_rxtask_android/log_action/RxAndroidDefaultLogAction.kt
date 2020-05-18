package com.ypz.killetom.rxjava3.lib_rxtask_android.log_action

import android.util.Log
import com.ypz.killetom.librxtask.base.ISuperTask
import com.ypz.killetom.librxtask.base.RxLogAction
import com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler.BuildConfig

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.rxjava3.lib_rxtask_android.log_action
 * @ClassName: RxAndroidDefaultLogAction
 * @Description: 针对android进行debug信息输出
 * @Author: KilleTom
 * @CreateDate: 2020/5/13 16:04
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 15:04
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
class RxAndroidDefaultLogAction private constructor(): RxLogAction {

    override fun d(objects: ISuperTask<*>, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(objects::class.java.simpleName, message)
        }
    }

    companion object{
        val instant by lazy { RxAndroidDefaultLogAction() }
    }
}