package com.ypz.killetom.librxtask.scope

import com.ypz.killetom.librxtask.scope.ITaskScopeCallAction

/**
 * create by 易庞宙(KilleTom) on 2022/10/12 13:27
 * email : 1986545332@qq.com
 * description：
 */
interface ITaskScope {
    fun scopeOnDestroy()
    fun subScope(callAction: ITaskScopeCallAction?)
}