package ar.utn.frba.mobile.fitnessapp.ui.details

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentClassesBinding
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentDetailsBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.ui.classes.ClassesFragmentArgs
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Base64

class DetailsFragment : Fragment() {
    private val args: DetailsFragmentArgs by navArgs()
    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //private lateinit var viewModel: DetailsViewModel

    private var carouselHeight = R.dimen.details_img_height

    private val backend = BackendService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val detailsViewModel = ViewModelProvider(this)[DetailsViewModel::class.java]
        //return inflater.inflate(R.layout.fragment_details, container, false)

        val gym: Gym = args.gym

        binding.detGymImages.setImageResource(R.drawable.bg_yoga)
        binding.detGymLogo.setImageResource(R.drawable.bg_beachx)

        backend.image(gym.id).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val b64img : String = response.body()!!
                val decodedImg : ByteArray = android.util.Base64.decode(b64img, android.util.Base64.DEFAULT)
                val img : Bitmap = BitmapFactory.decodeByteArray(decodedImg,0,decodedImg.size)
                binding.detGymLogo.setImageBitmap(img)
            }
            override fun onFailure(call: Call<String>, t: Throwable) { Toast.makeText(activity, "No Gym Logo found!", Toast.LENGTH_SHORT).show() }
        })

        binding.detGymName.text = gym.name
        binding.detGymContactnfo.text = gym.contactInfo
        binding.detGymDesc.text = gym.description

        binding.detBookBtn.setOnClickListener {
            val action = DetailsFragmentDirections.actionDetailsFragmentToClassesFragment(gym)
            findNavController().navigate(action)
        }

        binding.detScrollview.viewTreeObserver.addOnScrollChangedListener {
            if (_binding != null) {
                val scrollY = Math.min(Math.max(binding.detScrollview.scrollY, 0), carouselHeight)
                binding.detGymImages.translationY = (scrollY / 2.0).toFloat()
                binding.detImagesCarousel.translationY = (scrollY / 2.0).toFloat()
                //val alpha = scrollY / mImageViewHeight as Float
            }
        }

        val carousel = binding.detImagesCarousel
        carousel.registerLifecycle(lifecycle)
        val img_list = listOf(
            CarouselItem(imageDrawable = R.drawable.bg_gym),
            CarouselItem(imageDrawable = R.drawable.bg_yoga),
            CarouselItem(imageDrawable = R.drawable.bg_beach),
        )
        carousel.setData(img_list)
    }

/*    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}