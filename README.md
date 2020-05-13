# RxTask

利用RxJava 3.X 封装的Task 简化异步操作

[可以通过该网站获取版本 https://jitpack.io/#KilleTom/rxtask](https://jitpack.io/#KilleTom/rxtask)

注意`v0.0.1-beta`还没有抽离Scheduler 建议从`v1.0.0-beta`开始使用

## 配置使用

### Step 1.先在 build.gradle(Project:XXXX) 的 repositories 添加:

```gradle

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

```

### 基础库依赖添加

当前库最新版本为  version:`v1.0.1-beta`

```gradle

dependencies {
  //rxTask 基础库
  implementation 'com.github.KilleTom:rxtask:lib_rxtask:version'
  //android 扩展线程库
  implementation 'com.github.KilleTom:rxtask:lib_rxtask_android_scheduler:version'
}

```
PS: 不想找版本就这样子写

```gradle

dependencies {
  //rxTask 基础库
  implementation 'com.github.KilleTom:rxtask:lib_rxtask:v1.0.1-beta'
  //android 扩展线程库
  implementation 'com.github.KilleTom:rxtask:lib_rxtask_android_scheduler:v1.0.1-beta'
}

```

### 使用方式：

#### 初始化全局线程
```kotlin

//初始化Task全局的异步线程以及回调线程
//例如这里利用Android 拓展线程库针对 android 设置全局线程的工作
RxTaskSchedulerManager.setLocalScheduler(RxAndroidDefaultScheduler())

```

#### 简单异步运算

- 直接使用全局线程去做简单的异步运算
```kotlin
//简单的异步运算
 val singleTask = RxSingleEvaluationTaskTask.createTask {

            //例如获取新闻
            val response = okHttpClient.newCall(createRequest(createNewUrl("top")))
                .execute()

            val body = response.body ?: throw RuntimeException("body null")

            val result = Gson().fromJson(body.string(), JsonObject::class.java)

            return@createTask result
        }.successAction {
            //成功回调
            Log.i("KilleTom", "$it")
        }.failAction {
            //失败回调
            it.printStackTrace()
      }.start()
```
- 指定线程去做简单的异步运算
```kotlin
//简单的异步运算
 val singleTask = RxSingleEvaluationTaskTask.createTask({

            //例如获取新闻
            val response = okHttpClient.newCall(createRequest(createNewUrl("top")))
                .execute()

            val body = response.body ?: throw RuntimeException("body null")

            val result = Gson().fromJson(body.string(), JsonObject::class.java)

            return@createTask result
        },
        //例如这里指定默认实现好的 Android 拓展线程库
        RxAndroidDefaultScheduler()).successAction {
            //成功回调
            Log.i("KilleTom", "$it")
        }.failAction {
            //失败回调
            it.printStackTrace()
      }.start()
```
#### 带进度的异步运算
- 使用全局线程的异步进度写法
```kotlin
 val progressTask = RxProgressEvaluationTaskTask
            .createTask<JsonObject, Boolean> { task ->

                val types = arrayListOf<String>("top", "shehui", "guonei")

                types.forEach { value ->

                    val result = okHttpClient
                        .newCall(createRequest(createNewUrl(value)))
                        .execute()

                    val body = result.body ?: throw RuntimeException("body null")

                    val jsonObject = Gson().fromJson(body.string(), JsonObject::class.java)

                    Log.d("KilleTom", "推送新闻类型$value")
                    //推送进度
                    task.publishProgressAction(jsonObject)
                }
                //返回结果
                return@createTask true
            }.progressAction {
                Log.i("KilleTom", "收到进度,message:$it")
            }.successAction {
                Log.i("KilleTom", "Done")
            }.failAction {
                Log.i("KilleTom", "error message:${it.message ?: "unknown"}")
            }.start()
```
- 运行指定线程写法
```kotlin
 val progressTask = RxProgressEvaluationTaskTask
            .createTask<JsonObject, Boolean> ({ task ->

                val types = arrayListOf<String>("top", "shehui", "guonei")

                types.forEach { value ->

                    val result = okHttpClient
                        .newCall(createRequest(createNewUrl(value)))
                        .execute()

                    val body = result.body ?: throw RuntimeException("body null")

                    val jsonObject = Gson().fromJson(body.string(), JsonObject::class.java)

                    Log.d("KilleTom", "推送新闻类型$value")
                    //推送进度
                    task.publishProgressAction(jsonObject)
                }
                //返回结果
                return@createTask true
            },
            //例如这里指定默认实现好的 Android 拓展线程库
            RxAndroidDefaultScheduler())
            .progressAction {
                Log.i("KilleTom", "收到进度,message:$it")
            }.successAction {
                Log.i("KilleTom", "Done")
            }.failAction {
                Log.i("KilleTom", "error message:${it.message ?: "unknown"}")
            }.start()
```
#### 定时器异步
```kotlin
RxTimerTask.createTask { task ->

            if (task.getTimeTicker().countTimes >= 10) {
                task.cancel()
            }
            // do your logic
            
            return@createTask

        }.setDelayTime(0L)
            .setIntervalTime(1000)
            .setTaskScheduler(Schedulers.computation())
            .start()
```

