package com.example.listtest

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
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

        apiList()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.scroll.visibility = View.INVISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            binding.animationView.visibility = View.INVISIBLE
            binding.scroll.visibility = View.VISIBLE
        }, 3500)

    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun apiList(){
        val handler = HandlerCompat.createAsync(mainLooper)
        val executeService = Executors.newSingleThreadExecutor()
        val apiUrl = "https://job.yahooapis.jp/v1/furusato/jobinfo/?appid="
        val apiId = "【あなたのAPIキー】"

        //ここでHTTP通信を行いURLにリクエストをかけています
        executeService.submit @WorkerThread {
            var result = ""
            val url = URL("$apiUrl$apiId&results=100&start=100")
            val con = url.openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 1000
                    it.readTimeout = 1000
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
                val rootJSON = JSONObject(result)
                val results = rootJSON.getJSONArray("results")
                val id = intent.getIntExtra("id", 0)
                val index = results.getJSONObject(id)

                //【作業スペース】APIから情報取得
                val logoSp = index.getString("imgUrlPc")
                val note = index.getString("description")
                val cpName = index.getString("cpName")
                val postalCode = index.getString("postalCode")
                val postCode = postalCode.substring(0, 3) + "-" + postalCode.substring(3)
                val receptionTel = index.getString("receptionTel")
                val workLocationPrefecture = index.getString("workLocationPrefecture")
                val workLocationCity = index.getString("workLocationCity")
                val workLocationTown = index.getString("workLocationTown")
                val workLocationBlock = index.getString("workLocationBlock")
                val workLocationBuilding = index.getString("workLocationBuilding")
                val industryCodeContent = index.getString("industryCode")
                val industryCode =
                    industryCodeContent.substring(0, 1)
                val industry = industryCode
                    .replace(Regex("[A-T]")) {
                        when (it.value) {
                            "A" -> "農業、林業"
                            "B" -> "漁業"
                            "C" -> "鉱業、採石業、砂利採取業"
                            "D" -> "建設業"
                            "E" -> "製造業"
                            "F" -> "電気・ガス・熱供給・水道業"
                            "G" -> "情報通信業"
                            "H" -> "運輸業、郵便業"
                            "I" -> "卸売業、小売業"
                            "J" -> "金融業、保険業"
                            "K" -> "不動産業、物品賃貸業"
                            "L" -> "学術研究、専門・技術サービス業"
                            "M" -> "宿泊業、飲酒サービス業"
                            "N" -> "生活関連サービス業、娯楽業"
                            "O" -> "教育、学習支援業"
                            "P" -> "医療、福祉"
                            "Q" -> "複合サービス事業"
                            "R" -> "サービス業（他に分類されないもの）"
                            "S" -> "公務（他に分類されるものを除く）"
                            "T" -> "分類不能の産業"
                            else -> it.value
                        }
                    }
                val workingDayNote = index.getString("workingDayNote")
                val workingTimeStart = index.getString("workingTimeStart")
                val workingTimeEnd = index.getString("workingTimeEnd")
                val workingTimeNote = index.getString("workingTimeNote")
                val breakTimeStart = index.getString("breakTimeStart")
                val breakTimeEnd = index.getString("breakTimeEnd")
                val breakTimeNote = index.getString("breakTimeNote")
                val salaryContentMax = index.getString("salaryMax")
                val salaryMax =
                    salaryContentMax.substring(0, 3) + "," + salaryContentMax.substring(3)
                val salaryContentMin = index.getString("salaryMin")
                val salaryMin =
                    salaryContentMin.substring(0, 3) + "," + salaryContentMin.substring(3)
                val employmentTypeCode = index.getString("employmentTypeCode")
                val employmentType = employmentTypeCode
                    .replace("100", "正社員")
                    .replace("110", "新卒採用")
                    .replace("120", "パート・アルバイト")
                    .replace("130", "派遣社員")
                    .replace("140", "インターン")
                    .replace("150", "ボランティア")
                    .replace("160", "契約社員")
                    .replace("170", "業務委託")
                    .replace("180", "プロボノ")
                val holidayCode = index.getString("holidayCode")
                val holidayNote = index.getString("holidayNote")
                val holiday = holidayCode
                    .replace(Regex("[1-9]")){
                        when (it.value) {
                            "1" -> "月曜日"
                            "2" -> "火曜日"
                            "3" -> "水曜日"
                            "4" -> "木曜日"
                            "5" -> "金曜日"
                            "6" -> "土曜日"
                            "7" -> "日曜日"
                            "8" -> "祝日"
                            "9" -> holidayNote
                            else -> it.value
                        }
                    }
                val insuranceNote = index.getString("insuranceNote")
                val trialPeriodContent = index.getString("trialPeriod")
                val trialPeriodNote = index.getString("trialPeriodNote")
                val trialPeriod = trialPeriodContent
                    .replace(Regex("[0-1]")){
                        (
                                when (it.value) {
                                    "0" -> "無"
                                    "1" -> trialPeriodNote
                                    else -> it.value
                                }
                                )
                    }
                val description = index.getString("description")

                //画像表示

                Glide.with(this)
                    .load(logoSp)
                    .into(binding.companyImg)


                //【作業スペース】レイアウトIDとAPI取得結果の紐付け
                binding.companyName.text = cpName.replace("null","")
                binding.postCode.text = ("〒${postCode.replace("null","")}")
                binding.telNumber.text = receptionTel.replace("null","")
                binding.addressName.text = ("${workLocationPrefecture.replace("null","")} ${workLocationCity.replace("null","")} ${workLocationTown.replace("null","")} ${workLocationBlock.replace("null","")} ${workLocationBuilding.replace("null","")}")
                binding.businessContentContent.text = description.replace("null","")
                binding.industryContent.text = industry.replace("null","")
                binding.workingTimeContent.text = ("""
                    ${workingDayNote.replace("null","")}〜勤務時間〜
                    ${workingTimeStart.replace("null","")}〜${workingTimeEnd.replace("null","")}
                    ${workingTimeNote.replace("null","")}
                    ${breakTimeStart.replace("null","")}
                    ${breakTimeEnd.replace("null","")}
                    ${breakTimeNote.replace("null","")}""")
                binding.workingPlaceContent.text = ("${workLocationPrefecture.replace("null","")} ${workLocationCity.replace("null","")} ${workLocationTown.replace("null","")} ${workLocationBlock.replace("null","")} ${workLocationBuilding.replace("null","")}")
                binding.salaryContent.text = ("""給与上限${salaryMax.replace("null","")}円給与下限${salaryMin.replace("null","")}円""".trimIndent())
                binding.employmentStatusContent.text = employmentType.replace("null","")
                binding.holidayContent.text = holiday.replace("null","")
                binding.insuranceContent.text = insuranceNote.replace("null","")
                binding.testPeriodContent.text = trialPeriod.replace("null","")

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
        when(item.itemId) {
            android.R.id.home-> {
                finish()
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }

        }
        return super.onOptionsItemSelected(item)
    }

}