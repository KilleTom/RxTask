package com.ypz.killetom.librxtask.base

import java.util.*

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.base
 * @ClassName: RxTaskLogManager
 * @Description: 全局信息输出管理
 * @Author: KilleTom
 * @CreateDate: 2020/5/13 16:00
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
class RxTaskLogManager private constructor() {

    private var logAction: RxLogAction = object : RxLogAction {

    }

    fun logD(iSuperTask: ISuperTask<*>, message: String) {
        logAction.d(iSuperTask, message)
    }

    fun set(rxLogAction: RxLogAction) {
        this.logAction = rxLogAction
    }

    companion object {
        val instants by lazy { RxTaskLogManager() }
    }
}

interface RxLogAction {

    fun d(objects: ISuperTask<*>, message: String) {

        System.out.println("${objects::class.java.simpleName}->Message:$message")
    }
}