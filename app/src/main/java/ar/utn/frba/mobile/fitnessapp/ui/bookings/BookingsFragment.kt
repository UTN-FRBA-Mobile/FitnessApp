package ar.utn.frba.mobile.fitnessapp.ui.bookings

//import android.R
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentBookingsBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymClass
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.text.SimpleDateFormat
import java.util.*


class BookingsFragment : Fragment() {

    companion object {
        fun newInstance() = BookingsFragment()
    }

    private var _binding : FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingsViewModel

    private var selectedDate : Calendar? = null
    private var selectedGym : Gym? = null

    private var userClasses = listOf(
        GymClass(0,"CrossFit","2022-12-03 12:30:00","2022-12-03 13:30:00","Arnold" ,10,30),
        GymClass(1,"Spin"    ,"2022-12-04 15:00:00","2022-12-03 16:30:00","Jenny"  ,15,35),
        GymClass(2,"Yoga"    ,"2022-12-05 19:15:00","2022-12-03 20:15:00","Darrell",20,40),
    )

    private fun getDrawableText(text: String, color: Int, size: Int): Drawable {
        val bitmap = Bitmap.createBitmap(105, 48, Bitmap.Config.ARGB_8888) //TODO(fran): find out a way to retrieve the width and height of the cell

        val canvas = Canvas(bitmap)
        val scale = resources.displayMetrics.density

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.color = resources.getColor(color,null)//ContextCompat.getColor(this@getDrawableText, color)
            this.textSize = (size * scale).toInt().toFloat()
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val left_x = bounds.width().toFloat()/text.length
        val x = (bitmap.width - bounds.width()) / 2
        val y = (bitmap.height + bounds.height()) / 2
        canvas.drawText(text, left_x/*x.toFloat()*/, y.toFloat(), paint)

        return BitmapDrawable(resources, bitmap)
    }

    private fun getCalendarEventDrawable(string: String): Drawable {
        val background : Drawable =resources.getDrawable(R.drawable.calendar_event_bk, null)
        val text: Drawable = getDrawableText(string,R.color.white,10)
        val layers = arrayOf(background, text)
        return LayerDrawable(layers)
    }

    private fun stringToCalendar(string: String): Calendar{
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        cal.time = dateFormat.parse(string)
        return cal
    }

    private fun stringdateRemoveHMS(string: String): String{ //TODO(fran): horrible name and horrible function
        val res = string.substringBefore(' ')
        return res
    }
    private fun calendarToString(cal: Calendar): String{
        val res = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        return res
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun shuffleString(string: String): String {
        val shuffled = string.split(' ').toMutableList()
        shuffled.shuffle()
        var res = ""
        for (word in shuffled) {
            res += word + " "
        }
        return res
    }

    private fun uiClearSelectedGymClass(){
        binding.bookBtnReview.isEnabled = false //TODO(fran): better looking disabled buttons
        binding.bookBtnUnbook.isEnabled = false
        binding.gymInfoCard.visibility = View.GONE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookingsViewModel::class.java)

        var events = mutableListOf<EventDay>()

        userClasses.forEach {
            events.add(EventDay(stringToCalendar(it.startDate), getCalendarEventDrawable(it.type)))
        }
        binding.calendarView.setEvents(events)

        val min_date = Calendar.getInstance()
        min_date.add(Calendar.MONTH, -1)

        val max_date = Calendar.getInstance()
        max_date.add(Calendar.MONTH, 3)

        binding.calendarView.setMinimumDate(min_date)
        binding.calendarView.setMaximumDate(max_date)
        binding.calendarView.clearSelectedDays()

        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar: Calendar = eventDay.calendar

                val booking = userClasses.find { stringdateRemoveHMS(it.startDate) == calendarToString(clickedDayCalendar) }
                //TODO?(fran): instead of having to check in this very poor man's way we could subclass EventDay and add the information of the class inside, though that may cause
                //outdated state issues

                if(booking!=null){
                    val startDate = stringToCalendar(booking.startDate)
                    val endDate = stringToCalendar(booking.endDate)

                    binding.bookClassTitle.text = booking.type //TODO(fran): add gym name
                    binding.bookClassTime.text = //eg 'Sat 12 Aug 12:30 - 13:30'
                        SimpleDateFormat("EE").format(startDate.time) + " " + startDate.get(Calendar.DAY_OF_MONTH).toString() + " " + SimpleDateFormat("MMM").format(startDate.time) + " " +
                        startDate.get(Calendar.HOUR_OF_DAY).toString() +":"+ startDate.get(Calendar.MINUTE).toString() + " - " +
                        endDate.get(Calendar.HOUR_OF_DAY).toString() +":"+ endDate.get(Calendar.MINUTE).toString()
                    binding.bookClassDesc.text = shuffleString("Heavy weightlifting Arnold Schwarzenegger style before becoming the Terminator") //TODO: retrieve real gym class description
                    binding.bookBtnReview.isEnabled = true
                    binding.bookBtnUnbook.isEnabled = true
                    binding.gymInfoCard.visibility = View.VISIBLE

                }
                else{
                    uiClearSelectedGymClass()
                }
            }
        })

        uiClearSelectedGymClass()

        //binding.bookBtnReview.setOnClickListener {  } //NOTE(fran): this need not exist, the info on the class description is enough

        binding.bookBtnUnbook.setOnClickListener {
            Log.d("",calendarToString(binding.calendarView.firstSelectedDate))
            //INFO: At this point 'binding.calendarView.firstSelectedDate' always has a valid date that corresponds to the currently selected gym class
            /*TODO(fran): unbook class*/
        }

    }

}