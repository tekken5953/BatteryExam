package com.example.batteryexam

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

class MainActivity : AppCompatActivity() {
    companion object {
        var batteryPct = -1f
    }

    private lateinit var batteryImg: ImageView
    lateinit var valueText: TextView
    lateinit var isChargingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        batteryImg = findViewById(R.id.batteryImg)
        valueText = findViewById(R.id.batteryValue)
        isChargingText = findViewById(R.id.batteryIsCharging)

        getBatteryState()
    }

    // 배터리 잔량 및 충전여부를 암시적 인텐트로 브로드캐스팅
    private fun getBatteryState() {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver = PowerConnectionReceiver(batteryImg, isChargingText)
        val batteryStatus: Intent? = registerReceiver(receiver, ifilter)
        batteryStatus?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            batteryPct = level * 100 / scale.toFloat()
            val i: Int = batteryPct.toInt()
            if (i > -1) {
                Log.d("Battery", "Get Battery Status : $batteryPct")
                val s = "$i%"
                valueText.text = s
            }
        }
    }

    // 충전케이블 연결 여부를 불러 와 잔량을 표시하는 뷰를 업데이트
    class PowerConnectionReceiver(
        private val imgView: ImageView,
        private val isChargingText: TextView,
    ) : BroadcastReceiver() {
        @SuppressLint("UnsafeProtectedBroadcastReceiver")
        override fun onReceive(context: Context, intent: Intent) {
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

            if (isCharging) {
                isChargingText.visibility = View.VISIBLE
                Log.i("Battery", "Charging")
            } else {
                isChargingText.visibility = View.GONE
                Log.i("Battery", "DisCharging")
            }

            if (batteryPct != -1f) {
                if (isCharging) {
                    setDrawable(context, R.drawable.battery_charging)
                } else if (batteryPct == 100f) {
                    setDrawable(context, R.drawable.battery_full)
                } else if (batteryPct < 100f && batteryPct > 84.5) {
                    setDrawable(context, R.drawable.battery_6)
                } else if (batteryPct < 84.5 && batteryPct > 67.5) {
                    setDrawable(context, R.drawable.battery_5)
                } else if (batteryPct < 67.5 && batteryPct > 50.5) {
                    setDrawable(context, R.drawable.battery_4)
                } else if (batteryPct < 50.5 && batteryPct > 33.5) {
                    setDrawable(context, R.drawable.battery_3)
                } else if (batteryPct < 33.5 && batteryPct > 16.5) {
                    setDrawable(context, R.drawable.battery_2)
                } else if (batteryPct < 16.5 && batteryPct > 5f) {
                    setDrawable(context, R.drawable.battery_1)
                } else if (batteryPct < 5f && batteryPct > 0f) {
                    setDrawable(context, R.drawable.battery_caution)
                } else {
                    setDrawable(context, R.drawable.battery_empty)
                }
            }
        }

        private fun setDrawable(context: Context, drawable: Int) {
            imgView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    drawable,
                    null
                )
            )
        }
    }
}