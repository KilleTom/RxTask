package com.ypz.killetom.rxjava3.lib_rxtask.base

interface ITask<Result> {

    fun start()

    fun cancel()

    fun evaluationResult():Result



}