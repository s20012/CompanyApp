package com.example.listtest

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import com.bumptech.glide.Glide
import com.example.listtest.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listItem.onItemClickListener = ListItemClick()

        apiList()

    }

    @SuppressLint("CheckResult")
    private fun apiList(){
        val handler = HandlerCompat.createAsync(mainLooper)
        val executeService = Executors.newSingleThreadExecutor()
        val apiUrl = "https://job.yahooapis.jp/v1/furusato/jobinfo/?appid="
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
                    val name = index.getString("title")
                    val logoSp = index.getString("imgUrlSp")
                    dataList.add((Data().apply {
                        icon?.let { it1 ->
                            Glide.with(this@MainActivity)
                                .load(logoSp)
                                .into(it1)
                        }
                        title = name
                    }))
                    s += 1
                }
                val adapter = CustomAdapter(this, dataList.distinct() as ArrayList<Data>)
                binding.listItem.adapter = adapter


            }

        }
    }

    private inner class  ListItemClick: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val intent = Intent(this@MainActivity, ListActivity::class.java)
            intent.putExtra("id", position)
            startActivity(intent)
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
}