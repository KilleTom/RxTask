package com.ypz.killetom.librxtask.task

import com.ypz.killetom.librxtask.base.ISuperTask
import com.ypz.killetom.librxtask.scope.ITaskScope
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.task
 * @ClassName: RxTimerTask
 * @Description: 关注定时器的task
 * @Author: KilleTom
 * @CreateDate: 2020/5/8 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
open class RxTimerTask
private constructor(private val timerAction: (RxTimerTask) -> Unit) : ISuperTask<Long>() {

    private var timerDisposable: Disposable? = null

    private var workDelayTime: Long = workDelayDefaultTime

    private var workIntervalTime: Long = workIntervalDefaultTime

    private var workTimeUnit = workDefaultUnit

    private var workScheduler = getDefaultWorkScheduler()

    protected val ticker = createTicker()

    private var errorAction: ((Throwable) -> Unit)? = null

    override fun bindScope(scope: ITaskScope?): RxTimerTask {
        super.bindScope(scope)
        return this
    }


    override fun start() {

        super.start()

        ticker.startTime = System.currentTimeMillis()

        timerDisposable = Flowable.interval(workDelayTime, workIntervalTime, workTimeUnit,workScheduler)
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

    //取消并且不重置
    fun justCancelUnreset() {

        super.cancel()

        timerDisposable?.dispose()
        timerDisposable = null
    }

    //创建一个定时器运行的记录器便于记录简易的信息
    protected open fun createTicker(): TimerTicker {
        return TimerTicker()
    }

    open fun getTimeTicker(): TimerTicker {
        return ticker
    }

    //定时器开始运行前的延迟时间
    fun setDelayTime(time: Long): RxTimerTask {

        if (!running()) {
            workDelayTime = time
        }

        return this
    }

    //定时器每个时间事件运行的时间间隔设置
    fun setIntervalTime(time: Long): RxTimerTask {

        if (!running()) {
            workIntervalTime = time
        }

        return this
    }

    //设置时间单位
    fun setTimeUnit(unit: TimeUnit): RxTimerTask {

        if (!running()) {
            workTimeUnit = unit
        }

        return this
    }

    //设置工作线程
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