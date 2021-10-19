package com.glebkrep.yandexcup.arshooting.ui.game

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.glebkrep.yandexcup.arshooting.R
import com.glebkrep.yandexcup.arshooting.ar.PlaceNode
import com.glebkrep.yandexcup.arshooting.ar.PlacesArFragment
import com.glebkrep.yandexcup.arshooting.ar.model.Geometry
import com.glebkrep.yandexcup.arshooting.ar.model.GeometryLocation
import com.glebkrep.yandexcup.arshooting.ar.model.Player
import com.glebkrep.yandexcup.arshooting.ar.model.getPositionVector
import com.glebkrep.yandexcup.arshooting.ui.home.HomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.AnchorNode

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

    private var planceAnchorNode: AnchorNode? = null
    private var players: List<Player>? = null
    private var currentLocation: Location? = null

    private val viewModel by viewModels<GameVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isSupportedDevice()) {
            return
        }
        val sessionId = intent.getStringExtra("SESSION_ID") ?: return
        viewModel.setSessionId(sessionId)
        setContentView(R.layout.activity_game)

        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as PlacesArFragment

        sensorManager = getSystemService()!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setUpAr()
        setUpMaps()

        viewModel.otherAlivePlayers.observe(this) {
            addPlaces(it)
        }
        viewModel.location.observe(this){
            currentLocation = it
        }
        viewModel.amIAlive.observe(this){
             //todo: i'm dead, display according message
            Toast.makeText(this,"I'm dead...",Toast.LENGTH_LONG).show()
        }
        viewModel.isGameOverId.observe(this){
            startActivity(Intent(this,HomeActivity::class.java),Bundle().apply {
                putString("game_id",it)
            })
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
            planceAnchorNode = AnchorNode(anchor)
            planceAnchorNode?.setParent(arFragment.arSceneView.scene)
        }
    }

    private fun addPlaces(players: List<Player>) {
        val anchorNode = planceAnchorNode
        if (anchorNode == null) {
            Toast.makeText(this, "Нажмите на поверхность чтобы начать", Toast.LENGTH_SHORT).show()
            return
        }
        val currentLocation = currentLocation
        if (currentLocation == null) {
            Log.w(TAG, "Location has not been determined yet")
            return
        }

        for (anchorChild in anchorNode.children) {
            anchorNode.removeChild(anchorChild)
        }

        for (place in players) {
            // Add the place in AR
            val placeNode = PlaceNode(this, place)
            placeNode.setParent(anchorNode)
            placeNode.localPosition =
                place.getPositionVector(orientationAngles[0], currentLocation.latLng)
        }
    }


    private fun setUpMaps() {
        getCurrentLocation {
            players = getNearbyPlaces()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            onSuccess(location)
        }.addOnFailureListener {
            Log.e(TAG, "Could not get location")
        }
    }

    private fun getNearbyPlaces(): List<Player> {
        return listOf(
            Player(
                udid = "0",
                name = "Конец справа",
                location = Geometry(
                    GeometryLocation(
                        59.94065, 30.384895
                    )
                )
            ),
            Player(
                udid = "1",
                name = "Конец слева",
                location = Geometry(
                    GeometryLocation(
                        59.939938, 30.386433
                    )
                )
            ),
            Player(
                udid = "2",
                name = "Через дорогу",
                location = Geometry(
                    GeometryLocation(
                        59.940382, 30.385033
                    )
                )
            ),
            Player(
                udid = "3",
                name = "Сзади",
                location = Geometry(
                    GeometryLocation(
                        59.940681, 30.385757
                    )
                )
            )
        )
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

        // Update rotation matrix, which is needed to update orientation angles.
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

