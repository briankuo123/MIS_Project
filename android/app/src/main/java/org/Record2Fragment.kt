package org

//import androidx.navigation.NavController.navigate
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [Record2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Record2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var score: TextView
    private lateinit var menuname: TextView
    private lateinit var menucal: TextView
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
        return inflater.inflate(R.layout.fragment_record2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val button: ImageButton

        //顯示分數-從分數暫存中取得放入textView7裡面
        val final_score: TextView = requireView().findViewById(R.id.textView7)
        final_score.text = GlobalVariable.getScore().toString()

        scorerankImage = requireView().findViewById(R.id.imageView_scorerank)
        scorerankText = requireView().findViewById(R.id.textView_scorerank)
        when(final_score.text.toString().toInt()){
            in 0..500 ->{
                scorerankImage.setImageResource(R.drawable.medal4)
                scorerankText.text = "初學者"
            }
            in 501..1000 ->{
                scorerankImage.setImageResource(R.drawable.medal3)
                scorerankText.text = "入門"
            }
            in 1001..1500 ->{
                scorerankImage.setImageResource(R.drawable.medal2)
                scorerankText.text = "大師"
            }
            in 1501..1600 ->{
                scorerankImage.setImageResource(R.drawable.medal1)
                scorerankText.text = "神"
            }
        }

        //顯示該次加了多少經驗值
        val gradeplus: TextView = requireView().findViewById(R.id.textView8)
        gradeplus.text = "  +"+GlobalVariable.getScore().toString()

        //顯示該次運動完等級
        val new_Grade: TextView = requireView().findViewById(R.id.textView6)
        new_Grade.text = "LV."+((GlobalVariable.getExp() +GlobalVariable.getScore())/2000+1).toString()

        val origin_Grade = GlobalVariable.getExp()/2000+1
        val final_Grade = (GlobalVariable.getExp() +GlobalVariable.getScore())/2000+1
        //顯示升等提示
        if(final_Grade == origin_Grade+1){
            var expbar: ProgressBar = requireView().findViewById(R.id.progressBar)
            expbar.secondaryProgress = 2000
            expbar.setProgress( GlobalVariable.getExp()%2000 )
            Toast.makeText(context, "恭喜提升一等" , Toast.LENGTH_LONG).show()
        }
        //如果沒有升級則顯示目前經驗條
        else if(final_Grade == origin_Grade){
            var expbar: ProgressBar = requireView().findViewById(R.id.progressBar)
            expbar.secondaryProgress = GlobalVariable.getExp()%2000 + GlobalVariable.getScore()
            expbar.setProgress( GlobalVariable.getExp()%2000 )
        }

        val loadingDialog =  loading_dialog(activity)
        loadingDialog.startLoadingDialog()

        //將該次運動紀錄到資料庫中
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
                        comment.text = "動作方面不夠熟練，藉由多加練習一定可以獲得更多分數，若想更熟悉動作也可以觀看動作教學影片"
                    }
                    in 501..1000 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "很好，動作基本都能完成，若想獲得更高的分數需要將動作做得更完美"
                    }
                    in 1001..1500 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "很棒!動作做得很標準，對該菜單已掌握至八到九成，若想獲得最高分可以更加嚴格完成每個動作的關節角度"
                    }
                    in 1501..1600 ->{
                        val comment: TextView = requireView().findViewById(R.id.textView3)
                        comment.text = "您已完全掌握了該菜單的所有細節。"
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
        //將分數暫存歸0
        GlobalVariable.setScore(0)

        button = requireView().findViewById(R.id.imageButton8)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_Record2Fragment_to_menuChoose2Fragment)
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
        fun newInstance(param1: String?, param2: String?): Record2Fragment {
            val fragment = Record2Fragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}