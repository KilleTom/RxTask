package com.ypz.killetom.librxtask.base

import com.ypz.killetom.librxtask.scope.ITaskScope

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.base
 * @ClassName: ITask
 * @Description: Task抽象接口
 * @Author: KilleTom
 * @CreateDate: 2020/4/26 15:00
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
interface ITask<RESULT> {

    //启动
    fun start()

    //取消
    fun cancel()

    fun bindScope(scope: ITaskScope?): ITask<RESULT>?

}