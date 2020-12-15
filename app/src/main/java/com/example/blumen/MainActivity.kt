package com.example.blumen

import android.annotation.SuppressLint

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import helpers.MqttHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    var mqttHelper: MqttHelper? = null
    var estadoLuzQuarto1 = ""

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startMqtt()

        quarto1.setOnClickListener {
            if (estadoLuzQuarto1 == "OFF") {
                turnOn(MQTT_STATE_LIGTH_QUARTO)
                estadoLuzQuarto1 = "ON"

            } else {
                turnOff(MQTT_STATE_LIGTH_QUARTO)
                estadoLuzQuarto1 = "OFF"
            }

        }

    }

    private fun startMqtt() {

        mqttHelper = MqttHelper(getApplicationContext())
        mqttHelper!!.mqttAndroidClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connected")
                mqttHelper!!.publish(MQTT_STATE_LIGTH_QUARTO, "feedback")

            }

            override fun connectionLost(throwable: Throwable) {}

            @SuppressLint("UseCompatLoadingForDrawables")
            @Throws(Exception::class)
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", mqttMessage.toString())

                if (topic == MQTT_LIGTH_QUARTO) {

                    val estado = String(mqttMessage.payload)

                    if (estado == "ON") {

                        estadoLuzQuarto1 = "ON"

                        quarto1.setBackground(
                            getResources().getDrawable(
                                R.drawable.quarto1on,
                                getTheme()
                            )
                        )

                    } else {

                        estadoLuzQuarto1 = "OFF"
                        quarto1.setBackground(
                            getResources().getDrawable(
                                R.drawable.quarto1off,
                                getTheme()
                            )
                        )

                    }

                }
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {}
        })
    }

    fun turnOn(topic: String) {
        mqttHelper!!.publish(topic, "ON")

    }

    fun turnOff(topic: String) {

        mqttHelper!!.publish(topic, "OFF")
    }

    companion object {
        const val MQTT_LIGTH_QUARTO = "quarto/lampada1"
        const val MQTT_STATE_LIGTH_QUARTO = "quarto/estado1"
    }


}