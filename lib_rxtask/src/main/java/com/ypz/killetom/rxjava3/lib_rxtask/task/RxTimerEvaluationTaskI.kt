package com.ypz.killetom.rxjava3.lib_rxtask.task

import com.ypz.killetom.rxjava3.lib_rxtask.base.ISuperEvaluation
import com.ypz.killetom.rxjava3.lib_rxtask.base.ISuperTask
import io.reactivex.rxjava3.disposables.Disposable

class RxTimerEvaluationTaskI : ISuperTask<kotlin.Long>() {

    private var timerDisposable: Disposable? = null




    override fun finalResetAction() {

    }

    override fun running(): Boolean {

        val disposable = timerDisposable
            ?: return false

        if (!disposable.isDisposed)
            return TASK_CURRENT_STATUS == RUNNING_STATUS

        return false
    }
}