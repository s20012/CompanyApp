package com.example.listtest


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import com.example.listtest.databinding.ActivitySecondBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listItem.onItemClickListener = ListItemClick()

        apiList()

        binding.listItem.visibility = View.INVISIBLE
        binding.search.visibility = View.INVISIBLE

        val fadeAnim = AlphaAnimation(1.0f, 0.0f)
        fadeAnim.duration = 6000
        fadeAnim.fillAfter = true

        binding.animationView.animation = fadeAnim

        Handler(Looper.getMainLooper()).postDelayed({
            val inAnim = AlphaAnimation(0.0f, 1.0f)
            inAnim.duration = 1000
            inAnim.fillAfter = true
            binding.listItem.animation = inAnim
            binding.search.animation = inAnim
        }, 6000)

    }
    private fun apiList(){
        val handler = HandlerCompat.createAsync(mainLooper)
        val executeService = Executors.newSingleThreadExecutor()
        val apiUrl = "https://job.yahooapis.jp/v1/furusato/jobinfo/?appid="
        val apiId = "【あなたのAPIキー】"
        executeService.submit @WorkerThread {
            var result = ""
            var s = 0
            val url = URL("$apiUrl$apiId&results=100&start=100")
            val con = url.openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 100000
                    it.readTimeout = 100000
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
                val data = mutableListOf<String>()
                val rootJSON = JSONObject(result)
                val results = rootJSON.getJSONArray("results")

                repeat(100) {
                    val index = results.getJSONObject(s)
                    val name = index.getString("title")
                    data.add(name)

                    s += 1
                }

                binding.listItem.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_expandable_list_item_1,
                    data
                )

                binding.listItem.isTextFilterEnabled = true


                binding.search.setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(p0: String?): Boolean {
                            if (p0.isNullOrBlank()) {
                                binding.listItem.clearTextFilter()
                            } else {
                                binding.listItem.setFilterText(p0)
                            }
                            return false
                        }

                        override fun onQueryTextSubmit(p0: String?): Boolean {
                            return false
                        }
                    }
                )


            }

        }
    }

    private inner class  ListItemClick: AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val intent = Intent(this@SecondActivity, ListActivity::class.java)
            intent.putExtra("id", position)
            startActivity(intent)

            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
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