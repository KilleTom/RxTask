package com.ypz.killetom.rxjava3.lib_rxtask_android.log_action

import android.util.Log
import com.ypz.killetom.librxtask.base.ISuperTask
import com.ypz.killetom.librxtask.base.RxLogAction
import com.ypz.killetom.rxjava3.lib_rxtask_android.scheduler.BuildConfig

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