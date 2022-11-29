package org

//import androidx.navigation.NavController.navigate
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.camera.CameraSource
import org.data.Device
import org.ml.*

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [ExersiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExersiceFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var surfaceView: SurfaceView
    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var spinner: Spinner
    private lateinit var videoView: VideoView
    private lateinit var movement_show: TextView
    private lateinit var score: TextView
    private lateinit var correction_Counter: TextView
    private var cameraOn: Boolean = false


    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == MoveNet MultiPose model
     * 3 == PoseNet model
     **/
    private var modelPos = 0

    /** Default device is CPU */
    private var device = Device.CPU

    //private lateinit var tvScore: TextView
    //private lateinit var tvFPS: TextView
    //private lateinit var spnDevice: Spinner
    //private lateinit var spnModel: Spinner
    //private lateinit var spnTracker: Spinner
    //private lateinit var vTrackerOption: View
    //private lateinit var tvClassificationValue1: TextView
    //private lateinit var tvClassificationValue2: TextView
    //private lateinit var tvClassificationValue3: TextView
    //private lateinit var swClassification: SwitchCompat
    //private lateinit var vClassificationOption: View
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private var movementId = 0
    //arrayOf()傳值到CameraSource，該四個值代表四個動作，會於CameraSource呼叫對應的動作判斷式
    private var menu = arrayOf(0,1,2,3)
    private var menu_id = 0
    private var menu_pointer = 0
    private val main_view = getView()


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                getActivity()?.let {
                    ExersiceFragment.ErrorDialog.newInstance(getString(R.string.tfe_pe_request_permission))
                        .show(it.supportFragmentManager, ExersiceFragment.FRAGMENT_DIALOG)
                }
            }
        }

    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var changeTrackerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeTracker(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var changeMovementListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            movementId = position
            //textView2.setText(position.toString())
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    fun getMovementId():Int {
        return movementId
    }


    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exersice, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // keep screen on while app is running
        val window = getActivity()?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //tvScore = findViewById(R.id.tvScore)
        //tvFPS = findViewById(R.id.tvFps)
        //spnModel = findViewById(R.id.spnModel)
        //spnDevice = findViewById(R.id.spnDevice)
        //spnTracker = findViewById(R.id.spnTracker)
        //vTrackerOption = findViewById(R.id.vTrackerOption)
        surfaceView = requireView().findViewById(R.id.surfaceView2)
        textView = requireView().findViewById(R.id.textView26)
        textView2 = requireView().findViewById(R.id.textView20)
        textView3 = requireView().findViewById(R.id.textView21)
        textView4 = requireView().findViewById(R.id.textView25)
        movement_show = requireView().findViewById(R.id.textView42)
        score = requireView().findViewById(R.id.textView44)
        correction_Counter = requireView().findViewById(R.id.textView24)
        spinner = requireView().findViewById(R.id.spinner2)
        videoView = requireView().findViewById(R.id.videoView2)
        textView2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(textView2.text.toString().toInt() == -1 && menu_pointer < menu.size-1) {
                    menu_pointer++
                    //main_core?.let { cameraSource?.setAudio(it, menu[menu_pointer])}
                    context?.let { cameraSource?.setAudio(it, menu[menu_pointer]) }
                }
                else if(textView2.text.toString().toInt() == -1) {
                    var temp_score = GlobalVariable.setScore(score.text.toString().toInt())
                    Navigation.findNavController(view!!).navigate(R.id.RecordFragment)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        //tvClassificationValue1 = findViewById(R.id.tvClassificationValue1)
        //tvClassificationValue2 = findViewById(R.id.tvClassificationValue2)
        //tvClassificationValue3 = findViewById(R.id.tvClassificationValue3)
        //swClassification = findViewById(R.id.swPoseClassification)
        //vClassificationOption = findViewById(R.id.vClassificationOption)
        initSpinner()
        //spnModel.setSelection(modelPos)
        //swClassification.setOnCheckedChangeListener(setClassificationListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
        val button: ImageButton
        val button2: ImageButton

        val button3:Button

        button = requireView().findViewById(R.id.imageButton7)
        button.setOnClickListener { view ->
            val controller = Navigation.findNavController(view)
            controller.navigate(R.id.action_exersiceFragment_to_menuChooseFragment)
        }
        button2 = requireView().findViewById(R.id.imageButton8)
        button2.setOnClickListener { view ->
            val controller = Navigation.findNavController(view)
            controller.navigate(R.id.action_exersiceFragment_to_homePageFragment)
        }

        /*button3 = requireView().findViewById(R.id.Endbutton)
        button3.setOnClickListener { view ->
            val controller = Navigation.findNavController(view)
            controller.navigate(R.id.action_exersiceFragment_to_RecordFragment)
        }*/
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        cameraSource?.resume()
        super.onResume()
    }

    override fun onPause() {
        cameraSource?.close()
        cameraSource = null
        super.onPause()
    }

    // check if permission is granted or not.
    fun isCameraPermissionGranted(): Boolean {
        return true
    }

    fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(surfaceView, object : CameraSource.CameraSourceListener {
                        /*override fun onFPSListener(fps: Int) {
                             tvFPS.text = getString(R.string.tfe_pe_tv_fps, fps)
                         }

                         override fun onDetectedInfo(
                             personScore: Float?,
                             poseLabels: List<Pair<String, Float>>?
                         ) {
                             tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
                             poseLabels?.sortedByDescending { it.second }?.let {
                                 tvClassificationValue1.text = getString(
                                     R.string.tfe_pe_tv_classification_value,
                                     convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                 )
                                 tvClassificationValue2.text = getString(
                                     R.string.tfe_pe_tv_classification_value,
                                     convertPoseLabels(if (it.size >= 2) it[1] else null)
                                 )
                                 tvClassificationValue3.text = getString(
                                     R.string.tfe_pe_tv_classification_value,
                                     convertPoseLabels(if (it.size >= 3) it[2] else null)
                                 )
                             }
                         }*/

                    }, textView, textView2, textView3, textView4, menu, menu_id, videoView, movement_show, correction_Counter, score, context).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                //設定聲音撥放，pointer應該會設定成菜單編號
                context?.let { cameraSource?.setAudio(it, menu[menu_pointer])}
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) getActivity()?.let {
            PoseClassifier.create(
                it
            )
        } else null)
    }

    // Initialize spinners to let user select model/accelerator/tracker.
    private fun initSpinner() {
        getActivity()?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.movement,
                R.layout.spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
                spinner.onItemSelectedListener = changeMovementListener
            }
        }
    }

    // Change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // Change device (accelerator) type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    // Change tracker for Movenet MultiPose model
    private fun changeTracker(position: Int) {
        cameraSource?.setTracker(
            when (position) {
                1 -> TrackerType.BOUNDING_BOX
                2 -> TrackerType.KEYPOINTS
                else -> TrackerType.OFF
            }
        )
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                getActivity()?.let { MoveNet.create(it, device, ModelType.Lightning) }
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                getActivity()?.let { MoveNet.create(it, device, ModelType.Thunder) }
            }
            2 -> {
                // MoveNet (Lightning) MultiPose
                showPoseClassifier(false)
                showDetectionScore(false)
                // Movenet MultiPose Dynamic does not support GPUDelegate
                if (device == Device.GPU) {
                    showToast(getString(R.string.tfe_pe_gpu_error))
                }
                showTracker(true)
                getActivity()?.let {
                    MoveNetMultiPose.create(
                        it,
                        device,
                        Type.Dynamic
                    )
                }
            }
            3 -> {
                // PoseNet (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                getActivity()?.let { PoseNet.create(it, device) }
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }
    private fun showPoseClassifier(isVisible: Boolean) {
        //vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) {
            //swClassification.isChecked = false
        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        //tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        //tvClassificationValue1.visibility = visibility
        //tvClassificationValue2.visibility = visibility
        //tvClassificationValue3.visibility = visibility
    }

    // Show/hide the tracking options.
    private fun showTracker(isVisible: Boolean) {
        if (isVisible) {
            // Show tracker options and enable Bounding Box tracker.
            //vTrackerOption.visibility = View.VISIBLE
            //.setSelection(1)
        } else {
            // Set tracker type to off and hide tracker option.
            //vTrackerOption.visibility = View.GONE
            //spnTracker.setSelection(0)
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            getActivity()?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            } -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }




    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val FRAGMENT_DIALOG = "dialog"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExersiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): ExersiceFragment {
            val fragment = ExersiceFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}