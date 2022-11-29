package org

//import androidx.navigation.NavController.navigate
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController


/**
 * A simple [Fragment] subclass.
 * Use the [MovementButtKickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MovementButtKickerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var videoView: VideoView
    var video = Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.butt_kickers_video}")

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
        return inflater.inflate(R.layout.fragment_movement_buttkicker, container, false)
    }

    private fun setupVideoView() {
        videoView = requireView().findViewById(R.id.videoview2)
        videoView.setVideoURI(Uri.parse("android.resource://"+ context?.getPackageName() +"/${R.raw.butt_kickers_video}"))
        videoView.start()

        // hide medie controller
        videoView.setMediaController(null)
    }


    private val playButtonClickHandler = View.OnClickListener { _ ->
        videoView.start()
    }

    private val pauseButtonClickHandler = View.OnClickListener { _ ->
        videoView.pause()
    }

    private val stopButtonClickHandler = View.OnClickListener { _ ->
        videoView.seekTo(0)
        videoView.pause()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupVideoView()


        // hide medie controller


        val button: ImageButton
        val button2: ImageButton
        val button4: Button
        button = requireView().findViewById(R.id.imageButton13)
        button.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_MovementButtKickerFragment_to_menuChooseFragment)
        }
        button2 = requireView().findViewById(R.id.imageButton15)
        button2.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_MovementButtKickerFragment_to_homePageFragment)
        }
        button4 = requireView().findViewById(R.id.button26)
        button4.setOnClickListener { view ->
            val controller = findNavController(view)
            controller.navigate(R.id.action_MovementButtKickerFragment_to_menuChooseFragment)
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
        fun newInstance(param1: String?, param2: String?): MovementButtKickerFragment {
            val fragment = MovementButtKickerFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}
