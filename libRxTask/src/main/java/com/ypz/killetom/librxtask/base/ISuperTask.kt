package com.ypz.killetom.librxtask.base

import com.ypz.killetom.librxtask.exception.RxTaskCancelException
import com.ypz.killetom.librxtask.exception.RxTaskRunningException
import com.ypz.killetom.librxtask.scope.ITaskScope
import com.ypz.killetom.librxtask.scope.ITaskScopeCallAction

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.base
 * @ClassName: ISuperTask
 * @Description: Task 基类
 * @Author: KilleTom
 * @CreateDate: 2020/5/18 12:00
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
abstract class ISuperTask<RESULT> : ITask<RESULT> {

    protected var TASK_CURRENT_STATUS = NORMAL_STATUS

    protected var taskScope: ITaskScope? = null

    protected var iTaskScopeCallAction: ITaskScopeCallAction = object : ITaskScopeCallAction {
        override fun doOnScopeDestroyAction() {
            cancel()
        }
    }

    override fun start() {

        if (TASK_CURRENT_STATUS == RUNNING_STATUS) {
            logD("TASK already start")
            return
        }

        TASK_CURRENT_STATUS = RUNNING_STATUS

    }

    override fun cancel() {

        TASK_CURRENT_STATUS = CANCEL_STATUS

    }

    protected open fun finalResetAction() {

    }

    abstract fun running(): Boolean

    //use this can be judge task can be running, it was throw error when task was stop
    fun judgeRunningError() {

        if (!running()) {

            TASK_CURRENT_STATUS = STOP_STATUS

            throw RxTaskRunningException("task was stop")
        }
    }

    @Throws
    fun throwCancelError() {
        throw RxTaskCancelException("TASK Cancel")
    }

    override fun bindScope(scope: ITaskScope?): ITask<RESULT>? {

        scope?.subScope(iTaskScopeCallAction)

        return this
    }

    companion object {

        val NORMAL_STATUS = 0x00

        val RUNNING_STATUS = 0x100

        val CANCEL_STATUS = 0x200

        val ERROR_STATUS = 0x300

        val DONE_STATUS = 0x400

        val STOP_STATUS = 0x500
    }

    protected fun logD(message: String) {

        RxTaskLogManager.instants.logD(this, message)
    }
}