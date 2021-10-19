package com.glebkrep.yandexcup.arshooting.ui.game

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.getSystemService
import com.glebkrep.yandexcup.arshooting.R
import com.glebkrep.yandexcup.arshooting.ar.PlaceNode
import com.glebkrep.yandexcup.arshooting.ar.PlacesArFragment
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.ar.model.getPositionVector
import com.glebkrep.yandexcup.arshooting.ui.home.HomeActivity
import com.glebkrep.yandexcup.arshooting.utils.Debug
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node

class GameActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "MainActivity"

    private lateinit var arFragment: PlacesArFragment

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Sensor
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var placeAnchorNode: AnchorNode? = null
    private var currentLocation: Location? = null

    private val viewModel by viewModels<GameVM>()

    private var txtAlivePlayers: TextView? = null
    private var constraintImDead: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService()!!
        if (!isSupportedDevice()) {
            return
        }
        Debug.log(intent.extras?.keySet())
        val sessionId = intent.extras?.getString("SESSION_ID") ?: return

        viewModel.setSessionId(sessionId, this)
        setContentView(R.layout.activity_game)
        txtAlivePlayers = findViewById(R.id.txtAlivePlayers)
        constraintImDead = findViewById(R.id.constraint_im_dead)
        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as PlacesArFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setUpAr()

        viewModel.otherAlivePlayers.observe(this) { it ->
            Debug.log("alive players: ${it.map { it.name }}")
            val playersString =
                it.map { it.name + " [lng:${it.location.location.lng};lat:${it.location.location.lat}]\n" }
                    .joinToString { it }
            Debug.log("playersString:\n${playersString}")
            txtAlivePlayers
            txtAlivePlayers?.text = "Живые Соперники:\n${playersString}"
            addPlaces(it)
        }
        viewModel.location.observe(this) {
            currentLocation = it
        }
        viewModel.amIAlive.observe(this) {
            if (it) return@observe
            constraintImDead?.visibility = View.VISIBLE
            Toast.makeText(this, "I'm dead...", Toast.LENGTH_LONG).show()
        }
        viewModel.isGameOverId.observe(this) {
            if (it == "") return@observe
            Debug.log("game over")
            startActivity(Intent(this, HomeActivity::class.java).apply {
                this.putExtra("game_id", it)
            })
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun setUpAr() {
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            // Create anchor
            val anchor = hitResult.createAnchor()
            placeAnchorNode = AnchorNode(anchor)
            placeAnchorNode?.setParent(arFragment.arSceneView.scene)
        }
    }

    private fun addPlaces(players: List<Player>) {
        val anchorNode = placeAnchorNode
        placeAnchorNode
        if (anchorNode == null) {
            Toast.makeText(this, "Нажмите на поверхность чтобы начать", Toast.LENGTH_SHORT).show()
            return
        }
        val currentLocation = currentLocation
        if (currentLocation == null) {
            Log.w(TAG, "Location has not been determined yet")
            return
        }
        val newAnchorNodesList = mutableListOf<Node>()
        newAnchorNodesList.addAll(anchorNode.children ?: listOf())

        for (anchorChild in newAnchorNodesList) {
            anchorChild.isEnabled = false
            anchorChild.setParent(null)
            anchorNode.removeChild(anchorChild)
        }

        for (place in players) {
            // Add the place in AR
            val placeNode = PlaceNode(this, place)
            placeNode.setParent(anchorNode)
            placeNode.localPosition =
                place.getPositionVector(orientationAngles[0], currentLocation.latLng)
            placeNode.setOnTapListener(object : Node.OnTapListener {
                override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
                    viewModel.shoot(place.udid)
                    Toast.makeText(this@GameActivity, "BANG!", Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    private fun isSupportedDevice(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < 3.0) {
            Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            finish()
            return false
        }
        return true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }
}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)

