package ar.utn.frba.mobile.fitnessapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentDetailsBinding
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //private lateinit var viewModel: DetailsViewModel

    private var carouselHeight = R.dimen.details_img_height

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val detailsViewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //return inflater.inflate(R.layout.fragment_details, container, false)

        binding.detGymImages.setImageResource(R.drawable.bg_yoga)
        binding.detGymLogo.setImageResource(R.drawable.bg_beachx)
        binding.detGymName.text = "[AGymName]"
        binding.detGymContactnfo.text = "[AContact Info]"
        binding.detGymDesc.text = "ALorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        binding.detBookBtn.setOnClickListener { /*TODO(fran): Book gym date*/ }
        binding.detScrollview.viewTreeObserver.addOnScrollChangedListener{
            val scrollY = Math.min(Math.max(binding.detScrollview.scrollY, 0), carouselHeight)
            binding.detGymImages.translationY = (scrollY / 2.0).toFloat()
            binding.detImagesCarousel.translationY = (scrollY / 2.0).toFloat()
            //val alpha = scrollY / mImageViewHeight as Float
        }
        val carousel = binding.detImagesCarousel
        carousel.registerLifecycle(lifecycle)
        val img_list = listOf(
            CarouselItem(imageDrawable = R.drawable.bg_gym),
            CarouselItem(imageDrawable = R.drawable.bg_yoga),
            CarouselItem(imageDrawable = R.drawable.bg_beach),
        )
        carousel.setData(img_list)

        return root
    }

/*    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}