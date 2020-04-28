package com.ypz.killetom.rxjava3.lib_rxtask.task

import com.ypz.killetom.rxjava3.lib_rxtask.base.ISuperTask
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

open class RxTimerTask
private constructor(private val timerAction: (RxTimerTask) -> Unit) : ISuperTask<Long>() {

    private var timerDisposable: Disposable? = null

    private var workDelayTime: Long = workDelayDefaultTime

    private var workIntervalTime: Long = workIntervalDefaultTime

    private var workTimeUnit = workDefaultUnit

    private var workScheduler = getDefaultWorkScheduler()

    protected val ticker = createTicker()

    private var errorAction: ((Throwable) -> Unit)? = null

    override fun start() {

        super.start()

        ticker.startTime = System.currentTimeMillis()

        timerDisposable = Flowable.interval(workDelayTime, workIntervalTime, workTimeUnit)
            .observeOn(workScheduler)
            .subscribe(
                { times ->
                    if (running()) {

                        ticker.countTimes = times
                        ticker.currentTime = System.currentTimeMillis()

                        timerAction.invoke(this)
                    }
                },

                { errorAction?.invoke(it) })

    }

    override fun cancel() {

        justCancelUnreset()

        finalResetAction()
    }

    override fun finalResetAction() {

        errorAction = null

        ticker.currentTime = -1
        ticker.startTime = -1
        ticker.countTimes = -1

        workScheduler = Schedulers.newThread()
        workTimeUnit = workDefaultUnit
        workDelayTime = workDelayDefaultTime
    }

    override fun running(): Boolean {

        val disposable = timerDisposable
            ?: return false

        if (!disposable.isDisposed)
            return TASK_CURRENT_STATUS == RUNNING_STATUS

        return false
    }

    fun justCancelUnreset() {

        super.cancel()

        timerDisposable?.dispose()
        timerDisposable = null
    }

    open fun createTicker(): TimerTicker {
        return TimerTicker()
    }

    open fun getTimeTicker(): TimerTicker {
        return ticker
    }

    fun setDelayTime(time: Long): RxTimerTask {

        if (!running()) {
            workDelayTime = time
        }

        return this
    }

    fun setIntervalTime(time: Long): RxTimerTask {

        if (!running()) {
            workIntervalTime = time
        }

        return this
    }

    fun setTimeUnit(unit: TimeUnit): RxTimerTask {

        if (!running()) {
            workTimeUnit = unit
        }

        return this
    }

    fun setTaskScheduler(scheduler: Scheduler): RxTimerTask {

        if (!running()) {
            workScheduler = scheduler
        }

        return this
    }

    open class TimerTicker {

        var startTime: Long = -1

        var currentTime: Long = -1

        var countTimes: Long = -1

        override fun toString(): String {
            return "TimerTick(startTime=$startTime, currentTime=$currentTime, currentTimes=$countTimes)"
        }

    }

    companion object {

        val workDefaultUnit = TimeUnit.MILLISECONDS
        const val workDelayDefaultTime: Long = 100
        const val workIntervalDefaultTime: Long = 500

        private fun getDefaultWorkScheduler(): Scheduler {
            return Schedulers.newThread()
        }

        fun createTask(timerAction: (RxTimerTask) -> Unit): RxTimerTask {
            return RxTimerTask(timerAction)
        }

    }
}