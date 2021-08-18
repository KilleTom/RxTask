# RxTask

a easy Asynchronous Library by RxJava3.X 

[get lib version by use this web link： https://jitpack.io/#KilleTom/rxtask](https://jitpack.io/#KilleTom/rxtask)

be careful `v0.0.1-beta` defalut scheduler was android 

but start from `v1.0.3-beta` version you can use other scheduler

and now `v1.1.0` was first release version.

## TobeUse

### Step 1.in you project build.gradle(Project:XXXX) file, make repositories maven `https://jitpack.io`

look like this

```gradle

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

```

### dependencies lib

now lib  version was： `v1.1.0`

`libRxtask` a base lib, use this can run on webService or other

`libRxtaskAndroidScheduler` a expand android scheduler lib 


not run on android just like this dependdencies

```gradle

dependencies {
  //rxTask baselib
  implementation 'com.github.KilleTom:rxtask:libRxtask:version'
}

```

run on android must write this:

```gradle

dependencies {
  //rxTask baselib
  implementation 'com.github.KilleTom:rxtask:libRxtask:version'
  //android expand scheduler lib
  implementation 'com.github.KilleTom:rxtask:libRxtaskAndroidScheduler:version'
}

```
if you don't want find lib version just write like this


```gradle

dependencies {
   //rxTask base lib 
    implementation 'com.github.KilleTom.rxtask:libRxtask:v1.1.0'
    
   
    //run on android must use : android expand scheduler lib 
    implementation 'com.github.KilleTom.rxtask:libRxtaskAndroidScheduler:v1.1.0'
}

```

### use lib way：

#### init gloabl defalut scheduler
```kotlin

// init RxTask gloabl defalut scheduler
//for example like android use this lib init gloable defalut scheduler
RxTaskSchedulerManager.setLocalScheduler(RxAndroidDefaultScheduler())

```
sometime you don't want asynchronous run on default schedule you can make this asynchronous run on other schedule

#### asynchronous operation to be easy use

- like this android example use a init gloable defalut scheduler
```kotlin
//简单的异步运算
 val singleTask = RxSingleEvaluationTaskTask.createTask {

            //get news
            val response = okHttpClient.newCall(createRequest(createNewUrl("top")))
                .execute()

            val body = response.body ?: throw RuntimeException("body null")

            val result = Gson().fromJson(body.string(), JsonObject::class.java)

            return@createTask result
        }.successAction {
            //do your sucess logic
            Log.i("KilleTom", "$it")
        }.failAction {
            //do your fail logic
            it.printStackTrace()
      }.start()
```
- like this  android example not use a init gloable defalut scheduler
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
        //like this use a other scheduler 
        RxAndroidDefaultScheduler()
        ).successAction {
            //成功回调
            Log.i("KilleTom", "$it")
        }.failAction {
            //失败回调
            it.printStackTrace()
      }.start()
```

#### asynchronous operation can be has progress call
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

