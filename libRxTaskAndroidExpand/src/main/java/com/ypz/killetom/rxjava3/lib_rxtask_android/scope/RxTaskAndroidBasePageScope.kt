package com.ypz.killetom.rxjava3.lib_rxtask_android.scope

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.ypz.killetom.librxtask.scope.ITaskScope
import com.ypz.killetom.librxtask.scope.ITaskScopeCallAction

/**
 * create by 易庞宙(KilleTom) on 2022/10/18 17:10
 * email : 1986545332@qq.com
 * description：
 **/
class RxTaskAndroidBasePageScope : ITaskScope, LifecycleEventObserver {

    var canelByStopStatus = false

    var cancelByPauseStatus = false

    var cancelByDestroyStatus = true

    private var observers: MutableList<ITaskScopeCallAction> = mutableListOf()

    override fun scopeOnDestroy() {
        observers.forEach { it.doOnScopeDestroyAction() }
    }

    override fun subScope(callAction: ITaskScopeCallAction?) {
        callAction?.let {
            if (!observers.contains(it))
                observers.add(it)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {

        when (event) {

            Lifecycle.Event.ON_PAUSE -> {

                if (cancelByPauseStatus) {
                    scopeOnDestroy()
                    return
                }
            }

            Lifecycle.Event.ON_STOP -> {

                if (canelByStopStatus) {
                    scopeOnDestroy()
                    return
                }
            }

            Lifecycle.Event.ON_DESTROY -> {

                if (cancelByDestroyStatus) {
                    scopeOnDestroy()
                    return
                }
            }

            else -> {
            }
        }
    }
}


