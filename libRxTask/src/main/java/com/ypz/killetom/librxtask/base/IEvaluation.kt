package com.ypz.killetom.librxtask.base

/**
 * @ProjectName: RxTask
 * @Package: com.ypz.killetom.librxtask.base
 * @ClassName: IEvaluation
 * @Description: 负责运算结果的抽象接口
 * @Author: KilleTom
 * @CreateDate: 2020/4/27 12:14
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/5/18 12:00
 * @UpdateRemark: 创建
 * @Version: 1.0
 */
interface IEvaluation<RESULT> {

    fun evaluationResult(): RESULT
}