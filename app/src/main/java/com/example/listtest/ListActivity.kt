package com.example.listtest

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import com.bumptech.glide.Glide
import com.example.listtest.databinding.ActivityListBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val id = intent.getIntExtra("id", 0)
        binding.listId.text = id.toString()

        apiList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @SuppressLint("CheckResult")
    private fun apiList(){
        val handler = HandlerCompat.createAsync(mainLooper)
        val executeService = Executors.newSingleThreadExecutor()
        val apiUrl = "https://job.yahooapis.jp/v1/furusato/company/?appid="
        val apiId = "dj00aiZpPTZ0Q0FSSFhnbThJRyZzPWNvbnN1bWVyc2VjcmV0Jng9MGM-"
        executeService.submit @WorkerThread {
            var result = ""
            val num = 100
            var s = 0
            val url = URL("$apiUrl$apiId&results=$num")
            val con = url.openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 10000
                    it.readTimeout = 10000
                    it.requestMethod = "GET"
                    it.connect()
                    val stream = it.inputStream
                    result = is2String(stream)

                    stream.close()
                } catch (e: SocketTimeoutException) {
                    Log.d("TAG", "通信タイムアウト", e)
                }
                it.disconnect()
            }


            handler.post @UiThread {
                val dataList = arrayListOf<Data>()
                val rootJSON = JSONObject(result)
                val results = rootJSON.getJSONArray("results")

                repeat(num) {
                    val index = results.getJSONObject(s)
                    val name = index.getString("name")
                    val logoSp = index.getString("logoImgUrlPc")
                    dataList.add((Data().apply {
                        icon?.let { it1 ->
                            Glide.with(this@ListActivity)
                                .load(logoSp)
                                .into(it1)
                        }
                        title = name
                    }))
                    s += 1
                }

            }

        }
    }



    private fun is2String(stream: InputStream): String {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            android.R.id.home-> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}