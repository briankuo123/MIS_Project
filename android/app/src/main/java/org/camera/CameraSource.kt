/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ImageReader
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import androidx.navigation.Navigation.findNavController
import kotlinx.coroutines.*
import org.R
import org.VisualizationUtils
import org.YuvToRgbConverter
import org.data.Person
import org.data.AudioCountdown
import org.ml.MoveNetMultiPose
import org.ml.PoseClassifier
import org.ml.PoseDetector
import org.ml.TrackerType
import org.動作判斷式.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class CameraSource(
    private val surfaceView: SurfaceView,
    private val listener: CameraSourceListener? = null,
    //context
    private val textView: TextView,
    //pointer
    private val textView2: TextView,
    //fps
    private val textView3: TextView,
    //countdown timer
    private val textView4: TextView,
    private val menu: Array<Int>,
    //private val menu_time: Array<Int>

    private val menu_id: Int,

    private val videoView: VideoView,

    private val movement_show: TextView,

    private val correction_Counter: TextView,

    private val final_score: TextView,

    private val context: Context?
) {
    companion object {
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
        private const val TAG = "Camera Source"
    }

    private val lock = Any()
    private var detector: PoseDetector? = null
    private var classifier: PoseClassifier? = null
    private var isTrackerEnabled = false
    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)
    private lateinit var imageBitmap: Bitmap
    private var mp_List: MutableList<MediaPlayer> = ArrayList()
    private var audioCountDownList: MutableList<AudioCountdown> = ArrayList()
    private var score = arrayOf(0)
    //菜單一 促進血液循環
    private var movement_blood_circulation = arrayOf("抱膝向胸","高抬腿","後踢臀","左右弓箭步")
    //菜單二 全身伸展
    private var movement_stretch = arrayOf("頸部側向伸展","體側伸展","抬頭延展手上舉","抬頭延展後上舉")
    //菜單三 核心訓練
    private var movement_core = arrayOf("棒式","仰臥起坐","仰臥交替抬腿","鳥狗式")

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = surfaceView.context
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Readers used as buffers for camera still shots */
    private var imageReader: ImageReader? = null

    /** The [CameraDevice] that will be opened in this fragment */
    private var camera: CameraDevice? = null

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private var session: CameraCaptureSession? = null

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null
    private var cameraId: String = ""
    private var timeCounter = 0
    private var preTimeCounter = -1
    private var timer_go = 0
    private var time_array = arrayOf(40,30,30,30)
    private var mp_list_created = 0
    private var starter = true
    //動作執行倒數40秒
    val timer_40 = object: CountDownTimer(40000, 1000) {
        override fun onTick(p0: Long) {
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView4.setText("${p0/1000}")
                if(textView4.text.toString().toInt() % 10 == 0 && textView4.text.toString().toInt() != 40 && textView.text.toString() != "--") {

                    val counter = correction_Counter.text.toString().toInt()
                    val orign_score = final_score.text.toString().toInt()

                    if(counter <= 50) {
                        final_score.setText((orign_score + 100).toString())
                    }
                    else if(counter in 51..200) {
                        final_score.setText((orign_score + 75).toString())
                    }
                    else if(counter in 201..400) {
                        final_score.setText((orign_score + 50).toString())
                    }
                    else if(counter in 601..600) {
                        final_score.setText((orign_score + 25).toString())
                    }
                    else {
                        final_score.setText((orign_score + 1).toString())
                    }

                    correction_Counter.setText("0")
                }
            }
            for(i in audioCountDownList) {
                if(i.countDown > 0) {
                    i.countDown = i.countDown-1
                }
            }
        }

        override fun onFinish() {
            timeCounter++
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                if(timeCounter <= menu.size) {
                    textView2.setText("-1")
                    movement_show.setText("休息一下")
                    textView4.setText("0")
                }
            }
            for(i in audioCountDownList) {
                i.countDown = 0
            }
            release_audio()
            timer_go = 2
        }
    }

    val timer_30 = object: CountDownTimer(30000, 1000) {
        override fun onTick(p0: Long) {
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView4.setText("${p0/1000}")
                if(textView4.text.toString().toInt() % 10 == 0 && textView4.text.toString().toInt() != 30 && textView.text.toString() != "無偵測到人") {

                    val counter = correction_Counter.text.toString().toInt()
                    val orign_score = final_score.text.toString().toInt()

                    if(counter <= 50) {
                        final_score.setText((orign_score + 100).toString())
                    }
                    else if(counter in 51..200) {
                        final_score.setText((orign_score + 75).toString())
                    }
                    else if(counter in 201..400) {
                        final_score.setText((orign_score + 50).toString())
                    }
                    else if(counter in 601..600) {
                        final_score.setText((orign_score + 25).toString())
                    }
                    else {
                        final_score.setText((orign_score + 1).toString())
                    }

                    correction_Counter.setText("0")
                }
            }
            for(i in audioCountDownList) {
                if(i.countDown > 0) {
                    i.countDown = i.countDown-1
                }
            }
        }

        override fun onFinish() {
            timeCounter++
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                if(timeCounter <= menu.size) {
                    textView2.setText("-1")
                    movement_show.setText("休息一下")
                    textView4.setText("0")
                }
            }
            release_audio()
            timer_go = 2
        }
    }

    val timer_60 = object: CountDownTimer(60000, 1000) {
        override fun onTick(p0: Long) {
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView4.setText("${p0/1000}")
                if(textView4.text.toString().toInt() % 10 == 0 && textView4.text.toString().toInt() != 60 && textView.text.toString() != "--") {

                    val counter = correction_Counter.text.toString().toInt()
                    val orign_score = final_score.text.toString().toInt()

                    if(counter <= 50) {
                        final_score.setText((orign_score + 100).toString())
                    }
                    else if(counter in 51..200) {
                        final_score.setText((orign_score + 75).toString())
                    }
                    else if(counter in 201..400) {
                        final_score.setText((orign_score + 50).toString())
                    }
                    else if(counter in 601..600) {
                        final_score.setText((orign_score + 25).toString())
                    }
                    else {
                        final_score.setText((orign_score + 1).toString())
                    }

                    correction_Counter.setText("0")
                }
            }
            for(i in audioCountDownList) {
                if(i.countDown > 0) {
                    i.countDown = i.countDown-1
                }
            }
        }

        override fun onFinish() {
            timeCounter++
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                if(timeCounter <= menu.size) {
                    textView2.setText("-1")
                    movement_show.setText("休息一下")
                    textView4.setText("0")
                }
            }
            for(i in audioCountDownList) {
                i.countDown = 0
            }
            release_audio()
            timer_go = 2
        }
    }

    //休息倒數
    val timer_rest = object: CountDownTimer(10000, 1000) {
        override fun onTick(p0: Long) {
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                textView4.setText("${p0/1000}")
            }

        }

        override fun onFinish() {
            MainScope().launch {
                withContext(Dispatchers.Default) {

                }
                if(timeCounter <= menu.size-1) {
                    textView2.setText(menu[timeCounter].toString())
                    change_movement_text(menu_id)
                    textView4.setText("0")
                }
            }
            textView3.setText("0")
            timer_go = 0
        }
    }

    suspend fun initCamera() {
        camera = openCamera(cameraManager, cameraId)
        imageReader =
            ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                if (!::imageBitmap.isInitialized) {
                    imageBitmap =
                        Bitmap.createBitmap(
                            PREVIEW_WIDTH,
                            PREVIEW_HEIGHT,
                            Bitmap.Config.ARGB_8888
                        )
                }
                yuvConverter.yuvToRgb(image, imageBitmap)
                // Create rotated version for portrait display
                val rotateMatrix = Matrix()
                rotateMatrix.setScale(-1.0f, 1.0f);
                rotateMatrix.postRotate(90.0f)

                val rotatedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                    rotateMatrix, false
                )
                processImage(rotatedBitmap)
                image.close()
            }
        }, imageReaderHandler)

        imageReader?.surface?.let { surface ->
            session = createSession(listOf(surface))
            val cameraRequest = camera?.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )?.apply {
                addTarget(surface)
            }
            cameraRequest?.build()?.let {
                session?.setRepeatingRequest(it, null, null)
            }
        }
    }

    private suspend fun createSession(targets: List<Surface>): CameraCaptureSession =
        suspendCancellableCoroutine { cont ->
            camera?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(captureSession: CameraCaptureSession) =
                    cont.resume(captureSession)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    cont.resumeWithException(Exception("Session error"))
                }
            }, null)
        }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(manager: CameraManager, cameraId: String): CameraDevice =
        suspendCancellableCoroutine { cont ->
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) = cont.resume(camera)

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    if (cont.isActive) cont.resumeWithException(Exception("Camera error"))
                }
            }, imageReaderHandler)
        }

    fun prepareCamera() {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (cameraDirection != null &&
                cameraDirection == CameraCharacteristics.LENS_FACING_BACK
            ) {
                continue
            }
            this.cameraId = cameraId
        }
    }

    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    fun setClassifier(classifier: PoseClassifier?) {
        synchronized(lock) {
            if (this.classifier != null) {
                this.classifier?.close()
                this.classifier = null
            }
            this.classifier = classifier
        }
    }

    /**
     * Set Tracker for Movenet MuiltiPose model.
     */
    fun setTracker(trackerType: TrackerType) {
        isTrackerEnabled = trackerType != TrackerType.OFF
        (this.detector as? MoveNetMultiPose)?.setTracker(trackerType)
    }

    fun resume() {
        imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
        imageReaderHandler = Handler(imageReaderThread!!.looper)
        fpsTimer = Timer()
        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    fun close() {
        session?.close()
        session = null
        camera?.close()
        camera = null
        imageReader?.close()
        imageReader = null
        stopImageReaderThread()
        detector?.close()
        detector = null
        classifier?.close()
        classifier = null
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }

    // process image
    private fun processImage(bitmap: Bitmap) {
        val persons = mutableListOf<Person>()
        var classificationResult: List<Pair<String, Float>>? = null

        synchronized(lock) {
            detector?.estimatePoses(bitmap)?.let {
                persons.addAll(it)

                // if the model only returns one item, allow running the Pose classifier.
                if (persons.isNotEmpty()) {
                    classifier?.run {
                        classificationResult = classify(persons[0])
                    }
                }
            }
        }
        frameProcessedInOneSecondInterval++
        /*if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            listener?.onFPSListener(framesPerSecond)
        }

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            listener?.onDetectedInfo(persons[0].score, classificationResult)
        }*/


        if(starter) {
            doRest()
            starter = false
        }

        //語音倒數檢測
        if(textView4.text.toString().toInt() > 1) {
            audioCountDown()
        }

        //倒數計時
        if(timer_go == 0 && timeCounter <= menu.size-1) {
            doMenu()
        }
        if(timer_go == 2 && timeCounter <= menu.size-1) {
            doRest()
        }

        //動作判斷
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            textView3.setText((textView3.text.toString().toInt()+1).toString())
        }
        //數值與動作的對應
        if(textView4.text.toString().toInt() > 1) {
            when(textView2.text.toString().toInt()) {
                0 -> visualize(persons, bitmap, Knee_Holding().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter,40, mp_List, audio_playing_check(), audioCountDownList))
                1 -> visualize(persons, bitmap, High_Lift_Leg().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter,30, mp_List, audio_playing_check(), audioCountDownList))
                2 -> visualize(persons, bitmap, Butt_Kickers().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
                3 -> visualize(persons, bitmap, Dynamic_Lunge().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
                4 -> visualize(persons, bitmap, Neck_Stretch().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 60, mp_List, audio_playing_check(), audioCountDownList))
                5 -> visualize(persons, bitmap, Side_Stretch().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 60, mp_List, audio_playing_check(), audioCountDownList))
                6 -> visualize(persons, bitmap, LookUp_Stretch().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 60, mp_List, audio_playing_check(), audioCountDownList))
                7 -> visualize(persons, bitmap, ArmDown_Stretch().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 60, mp_List, audio_playing_check(), audioCountDownList))
                8 -> visualize(persons, bitmap, Plank().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
                9 -> visualize(persons, bitmap, Sit_Ups().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
                10 -> visualize(persons, bitmap, Leg_Raise().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
                11 -> visualize(persons, bitmap, Bird_Dog().check(textView, persons.filter { it.score > MIN_CONFIDENCE }, textView3, textView4, correction_Counter, 30, mp_List, audio_playing_check(), audioCountDownList))
            }
        }
        else {
            val answer: List<Pair<Int, Int>> = listOf(Pair(0, 0))
            visualize(persons, bitmap, answer)
        }

    }

    private fun visualize(persons: List<Person>, bitmap: Bitmap, answer: List<Pair<Int, Int>>) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            persons.filter { it.score > MIN_CONFIDENCE }, isTrackerEnabled, answer
        )

        val holder = surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
                Rect(left, top, right, bottom), null
            )
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun stopImageReaderThread() {
        imageReaderThread?.quitSafely()
        try {
            imageReaderThread?.join()
            imageReaderThread = null
            imageReaderHandler = null
        } catch (e: InterruptedException) {
            Log.d(TAG, e.message.toString())
        }
    }

    private fun doMenu() {
        timer_go = 1 // 1=正在執行doMenu
        if(timeCounter != preTimeCounter) {
            when(time_array[timeCounter]) {
                40 -> timer_40.start()
                30 -> timer_30.start()
                60 -> timer_60.start()
            }
            preTimeCounter++
        }
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            videoView.visibility = View.INVISIBLE
        }
    }

    private fun doRest() {
        timer_go = 3 // 3=正在執行Rest
        timer_rest.start()
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            videoView.visibility = View.VISIBLE
        }
        video_set(menu[timeCounter])
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            videoView.requestFocus()
            videoView.start()
        }

    }

    //影片路徑設定
    private fun video_set(pointer: Int) {

        var video = Uri.parse("android.resource://org.tensorflow.lite.examples.poseestimation/${R.raw.knee_holding_video}")

        when(pointer) {
            0 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.knee_holding_video}")
            1 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.high_lift_leg_video}")
            2 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.butt_kickers_video}")
            3 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.dynamic_lunge_video}")
            4 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.neck_stretch_video}")
            5 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side_stretch_video}")
            6 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lookup_stretch_video}")
            7 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.armdown_stretch_video}")
            8 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.plank_video}")
            9 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.sit_ups_video}")
            10 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.leg_raise_video}")
            11 -> video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.bird_dog_video}")
        }
        MainScope().launch {
            withContext(Dispatchers.Default) {

            }
            videoView.setVideoURI(video)
        }
    }
    //語音路徑設定
    fun setAudio(context: Context, menu_pointer: Int) {

        var Uri_array: MutableList<Uri> = ArrayList()

        when(menu_pointer) {
            0 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.knee_holding_body_straight}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.knee_holding_change_side}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.knee_holding_leg_higher}"))
            }
            1 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.leg_lift_leg_higher}"))
            }
            2 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.butt_kicker_kick_higher}"))
            }
            3 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.dynamic_lunge_backleg_straight}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.dynamic_lunge_frontleg_bend}"))
            }
            4 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.neck_stretch_change_side}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.neck_stretch_head_down}"))
            }
            5 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side_stretch_change_side}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side_stretch_bend_more}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side_stretch_body_straight}"))
            }
            6 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.up_extension_backleg_straight}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.up_extension_frontleg_bend}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.up_extension_hand_up}"))
                Uri_array.add(12, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.up_extension_look_up}"))
            }
            7 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.back_extension_backleg_straight}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.back_extension_frontleg_bend}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.back_extension_hand_back}"))
                Uri_array.add(12, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.back_extension_look_up}"))
            }
            8 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.plank_bend_knee}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.plank_high_butt}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.plank_low_butt}"))
            }
            9 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.sit_ups_body_up}"))
            }
            10 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.leg_raise_leg_higher}"))
            }
            11 -> {
                Uri_array.add(0, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.lazy}"))
                Uri_array.add(1, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.five_second}"))
                Uri_array.add(2, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.front}"))
                Uri_array.add(3, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.move_finish}"))
                Uri_array.add(4, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_finish}"))
                Uri_array.add(5, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.rest_time}"))
                Uri_array.add(6, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.side}"))
                Uri_array.add(7, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_close}"))
                Uri_array.add(8, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.too_far}"))
                Uri_array.add(9, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.bird_dog_change_side}"))
                Uri_array.add(10, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.bird_dog_hand_lift_more}"))
                Uri_array.add(11, Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.bird_dog_leg_higher}"))
            }
        }


        var run_counter = 0
        for(i in Uri_array) {
            var mp = MediaPlayer()
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mp.setDataSource(context, i)
            mp.prepare()
            mp_List.add(run_counter, mp)
            run_counter++
            audioCountDownList.add(Uri_array.indexOf(i), AudioCountdown(Uri_array.indexOf(i)))
        }
    }
    //檢查語音撥放狀況
    private fun audio_playing_check(): Boolean {
        for(i in mp_List) {
            if(i == null){
                return true
            }
            if(i.isPlaying) {
                return true
            }
        }

        return false
    }
    //釋放資源
    private fun release_audio() {
        for(i in mp_List) {
            if(i.isPlaying && i != null) {
                i.stop()
            }
            i.release()
        }
        for(i in mp_List) {
            i == null
        }
        mp_List.clear()
    }
    //紀錄語音倒數
    private fun audioCountDown() {
        for(i in mp_List) {
            if(i.isPlaying) {
                audioCountDownList.add(mp_List.indexOf(i), AudioCountdown(mp_List.indexOf(i), 10))
            }
        }
    }

    private fun change_movement_text(menu_id: Int) {
        when(menu_id) {
            0 -> movement_show.setText(movement_blood_circulation[timeCounter])
            1 -> movement_show.setText(movement_stretch[timeCounter])
            2 -> movement_show.setText(movement_core[timeCounter])
        }
    }


    interface CameraSourceListener {
        //fun onFPSListener(fps: Int)

        //fun onDetectedInfo(personScore: Float?, poseLabels: List<Pair<String, Float>>?)
    }
}
