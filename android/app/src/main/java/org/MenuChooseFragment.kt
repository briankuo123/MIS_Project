package org

import androidx.navigation.Navigation.findNavController
//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*

import org.camera.CameraSource
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [MenuChooseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MenuChooseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
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
        return inflater.inflate(R.layout.fragment_menu_choose, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button: ImageButton
        val button2: ImageButton
        val button3: ImageButton
        val button4: Button
        val button5: Button
        val button6: Button
        val button7: Button

        val userid = GlobalVariable.getId().toString()
        var exerciseCount :Int
        //先計算今天運動次數，大於10次則不能點選
        val okHttpClient = OkHttpClient()
        val formbody = FormBody.Builder()
            .add("userid",userid)
            .build()
        val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/counttimes").post(formbody).build()
        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from Exercise page" , Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val msg = response.body!!.string()
                if(msg == "count_times fail"){
                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    GlobalVariable.setTimes(msg.toInt())
                    val ExeTimes = GlobalVariable.getTimes()

                    MainScope().launch {
                        withContext(Dispatchers.Default) {

                        }
                        Toast.makeText(context, "今天運動次數:" + ExeTimes.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        button = requireView().findViewById(R.id.imageButton9)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_menuFragment)
        }
        button2 = requireView().findViewById(R.id.imageButton11)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_homePageFragment)
        }

        //開始運動
        button3 = requireView().findViewById(R.id.imageButton6)
        button3.setOnClickListener { view ->
            val ExeTimes = GlobalVariable.getTimes()
            if ( ExeTimes >= 10 ) {
                Toast.makeText(context, "今天已經超過10次囉!\n適當休息也是很重要的!", Toast.LENGTH_LONG).show()
            }
            else{
                val controller = findNavController(view)
                controller.navigate(R.id.action_menuChooseFragment_to_exersiceFragment)
            }
        }

        button4 = requireView().findViewById(R.id.button12)
        button4.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_MovementKneeHoldingFragment)
        }
        button5 = requireView().findViewById(R.id.button13)
        button5.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_MovementHighLiftLegFragment)
        }
        button6 = requireView().findViewById(R.id.button14)
        button6.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_MovementButtKickerFragment)
        }
        button7 = requireView().findViewById(R.id.button15)
        button7.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_menuChooseFragment_to_MovementDynamicLungeFragment)
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuChooseFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MenuChooseFragment {
            val fragment = MenuChooseFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}