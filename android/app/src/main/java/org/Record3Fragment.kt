package org

import androidx.navigation.Navigation.findNavController
//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [Record3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Record3Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var score: TextView
    private lateinit var menuname: TextView
    private lateinit var menucal:TextView
    private lateinit var scorerankImage: ImageView
    private lateinit var scorerankText: TextView

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
        return inflater.inflate(R.layout.fragment_record3, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button: ImageButton

        val final_score: TextView = requireView().findViewById(R.id.textView7)
        final_score.text = GlobalVariable.getScore().toString()

        scorerankImage = requireView().findViewById(R.id.imageView_scorerank)
        scorerankText = requireView().findViewById(R.id.textView_scorerank)
        when(final_score.text.toString().toInt()){
            in 0..500 ->{
                scorerankImage.setImageResource(R.drawable.medal4)
                scorerankText.text = "?????????"
            }
            in 501..1000 ->{
                scorerankImage.setImageResource(R.drawable.medal3)
                scorerankText.text = "??????"
            }
            in 1001..1500 ->{
                scorerankImage.setImageResource(R.drawable.medal2)
                scorerankText.text = "??????"
            }
            in 1501..1600 ->{
                scorerankImage.setImageResource(R.drawable.medal1)
                scorerankText.text = "???"
            }
        }

        //?????????????????????????????????
        val gradeplus: TextView = requireView().findViewById(R.id.textView8)
        gradeplus.text = "  +"+GlobalVariable.getScore().toString()

        //???????????????????????????
        val new_Grade: TextView = requireView().findViewById(R.id.textView6)
        new_Grade.text = "LV."+((GlobalVariable.getExp() +GlobalVariable.getScore())/2000+1).toString()

        val origin_Grade = GlobalVariable.getExp()/2000+1
        val final_Grade = (GlobalVariable.getExp() +GlobalVariable.getScore())/2000+1
        //??????????????????
        if(final_Grade == origin_Grade+1){
            var expbar: ProgressBar = requireView().findViewById(R.id.progressBar)
            expbar.secondaryProgress = 2000
            expbar.setProgress( GlobalVariable.getExp()%2000 )
            Toast.makeText(context, "??????????????????" , Toast.LENGTH_LONG).show()
        }
        //??????????????????????????????????????????
        else if(final_Grade == origin_Grade){
            var expbar: ProgressBar = requireView().findViewById(R.id.progressBar)
            expbar.secondaryProgress = GlobalVariable.getExp()%2000 + GlobalVariable.getScore()
            expbar.setProgress( GlobalVariable.getExp()%2000 )
        }

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        val okHttpClient = OkHttpClient()
        val userid = GlobalVariable.getId()
        val username = GlobalVariable.getName()
        score = requireView().findViewById(R.id.textView7)
        menuname = requireView().findViewById(R.id.textView_menuname)
        menucal = requireView().findViewById(R.id.textView_menuncal)

        val formbody = FormBody.Builder()
            .add("userid",userid.toString())
            .add("username",username)
            .add("score", score.text.toString())
            .add("menuname", menuname.text.toString())
            .add("menucal", menucal.text.toString())
            .build()

        val request = Request.Builder().url("https://misprojectserver.azurewebsites.net/record").post(formbody).build()
        okHttpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                MainScope().launch {
                    withContext(Dispatchers.Default) {

                    }
                    Toast.makeText(context, "error from login page" , Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                when(score.text.toString().toInt()){
                    in 0..500 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "?????????????????????????????????????????????????????????????????????????????????"
                    }
                    in 501..1000 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "?????????????????????????????????????????????????????????????????????????????????????????????"
                    }
                    in 1001..1500 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "??????!????????????????????????????????????????????????????????????????????????????????????????????????"
                    }
                    in 1501..1600 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "????????????????????????????????????????????????"
                    }
                }
                /*
                val msg = response.body!!.string()
                val returnval: TextView = requireView().findViewById(R.id.textView3)
                returnval.text = msg
                 */
                loadingDialog.dismissDialog()
            }
        })
        //?????????????????????????????????0
        GlobalVariable.setScore(0)

        button = requireView().findViewById(R.id.imageButton8)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_Record3Fragment_to_menuChoose3Fragment)
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
         * @return A new instance of fragment RecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Record3Fragment {
            val fragment = Record3Fragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}