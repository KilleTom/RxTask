package com.ypz.killetom.librxtask.exception

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.exception
 * @ClassName: RxTaskEvaluationException
 * @Description: task 运算结果异常
 * @Author: KilleTom
 * @CreateDate: 2020/4/26 17:30
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
class RxTaskEvaluationException : RxTaskSuperException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(cause: Throwable?) : super(cause) {}
}