package com.ypz.killetom.rxjava3.rxtask

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ypz.killetom.rxjava3.lib_rxtask.task.RxProgressEvaluationTaskTask
import com.ypz.killetom.rxjava3.lib_rxtask.task.RxSingleEvaluationTaskTask
import com.ypz.killetom.rxjava3.lib_rxtask.task.RxTimerTask
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(6000, TimeUnit.MILLISECONDS)
            .callTimeout(6000, TimeUnit.MILLISECONDS)
            .readTimeout(18000, TimeUnit.MILLISECONDS)
            .writeTimeout(60000, TimeUnit.MILLISECONDS)
            .build()

        val singleTask = RxSingleEvaluationTaskTask.createTask<JsonObject> {

            val result = okHttpClient.newCall(createRequest(createNewUrl("top")))
                .execute()

            val body = result.body ?: throw RuntimeException("body null")

            return@createTask Gson().fromJson(body.string(), JsonObject::class.java)
        }

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

                    task.publishProgressAction(jsonObject)
                }

                return@createTask true
            }

        val manager = application
            .getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val timerTask = RxTimerTask.createTask {task->

            if (task.getTimeTick().countTimes >=10){
                task.cancel()
            }

            if (Build.VERSION.SDK_INT >= 23) {

                val network = manager.activeNetwork

                if (network == null){
                    Log.d("KilleTom","network null connect false")
                    return@createTask
                }

                val connectInfo = manager
                    .getNetworkCapabilities(network)

                if (connectInfo == null){
                    Log.d("KilleTom","connectInfo null connect false")
                    return@createTask
                }


                val isInterNet =
                    connectInfo.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                Log.d("KilleTom", "$isInterNet")

            }

            return@createTask

        }.setDelayTime(0L)
            .setIntervalTime(1000)
            .setTaskScheduler(Schedulers.computation())


        single.setOnClickListener {

            singleTask.successAction {
                Log.i("KilleTom", "$it")
            }.failAction {
                it.printStackTrace()
            }.start()

        }

        progress.setOnClickListener {
            progressTask.progressAction {
                Log.i("KilleTom", "收到进度,message:$it")
            }.successAction {
                Log.i("KilleTom", "Done")
            }.failAction {
                Log.i("KilleTom", "error message:${it.message ?: "unknown"}")
            }.start()
        }

        timer.setOnClickListener {
            timerTask.start()
        }
    }

    private fun createNewUrl(type: String): HttpUrl {
        val url = HttpUrl
            .Builder()
            .scheme("https")
            .host("v.juhe.cn")
            .addEncodedPathSegment("toutiao")
            .addEncodedPathSegment("index")
            .addQueryParameter("type", type)
            .addQueryParameter("key", "13728f03ef29af183184d6d30dc6ae43")
            .build()

        return url
    }

    private fun createRequest(url: HttpUrl): Request {
        val request = Request
            .Builder()
            .url(url)
            .build()

        return request
    }
}
