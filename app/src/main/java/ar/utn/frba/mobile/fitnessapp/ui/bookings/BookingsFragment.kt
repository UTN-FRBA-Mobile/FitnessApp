package ar.utn.frba.mobile.fitnessapp.ui.bookings

//import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.databinding.FragmentBookingsBinding
import ar.utn.frba.mobile.fitnessapp.model.Gym
import ar.utn.frba.mobile.fitnessapp.model.GymClass
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.model.backend.BookingBody
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookingsFragment : Fragment() {

    companion object {
        fun newInstance() = BookingsFragment()
    }

    private var _binding : FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BookingsViewModel

    //private var selectedDate : Calendar? = null
    //private var selectedGym : Gym? = null

    private var _userClasses = listOf(
        GymClass(id=0,gymId=1,type="CrossFit",startDate="2022-12-03T12:30:00",endDate="2022-12-03T13:30:00",professor="Arnold",people=10,maxCapacity=30),
        GymClass(id=1,gymId=0,type="Spin",startDate="2022-12-04T15:00:00",endDate="2022-12-03T16:30:00",professor="Jenny",people=15,maxCapacity=35),
        GymClass(id=2,gymId=3,type="Yoga",startDate="2022-12-05T19:15:00",endDate="2022-12-03T20:15:00",professor="Darrell",people=20,maxCapacity=40),
    )

    private var userClasses = mutableListOf<GymClass>()

    private val backend = BackendService.create()

    private val userId : Int = 1  //TODO(fran): will we handle multiple userIds?

    private var gyms: MutableList<Gym>? = null


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
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        cal.time = dateFormat.parse(string)
        return cal
    }

    private fun stringdateRemoveHMS(string: String): String{ //TODO(fran): horrible name and horrible function
        val res = string.substringBefore('T')
        return res
    }
    private fun calendarToString(cal: Calendar): String{
        val res = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        return res
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

    private fun uiShowSelectedGymClassInteractions(show : Boolean){
        binding.bookBtnReview.isEnabled = show //TODO(fran): better looking disabled buttons
        binding.bookBtnUnbook.isEnabled = show
        binding.gymInfoCard.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setCalendarEvents(classes : List<GymClass>){
        var events = mutableListOf<EventDay>()

        classes.forEach {
            events.add(EventDay(stringToCalendar(it.startDate), getCalendarEventDrawable(it.type)))
        }
        //binding.calendarView.clearSelectedDays()
        binding.calendarView.setEvents(events)
        userClasses = classes.toMutableList()
    }

    private fun userClassFromCalendar(cal:Calendar): GymClass?{
        val res = userClasses.find { stringdateRemoveHMS(it.startDate) == calendarToString(cal) }
        return res
    }

    private fun unbookCalendarEvent(){
        val booking = userClassFromCalendar(binding.calendarView.firstSelectedDate)!!

        uiShowSelectedGymClassInteractions(false)

        backend.unbook(BookingBody("$userId"),booking.gymId,booking.id).enqueue(object : Callback<Unit> { //TODO(fran): will we handle multiple userIds?
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                retrieveAndSetCalendarEvents()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(activity, "Couldn't unbook the class!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun retrieveAndSetCalendarEvents(){
        backend.userClasses(userId).enqueue(object : Callback<List<GymClass>> {
            override fun onResponse(call: Call<List<GymClass>>, response: Response<List<GymClass>>) {

                val newUserClasses : List<GymClass> = response.body()!!

                val TEST : Boolean = true //TODO(fran): remove when the backend userClasses contain valid startDate & endDate
                if(TEST)
                    setCalendarEvents(_userClasses)
                else
                    setCalendarEvents(newUserClasses)
            }

            override fun onFailure(call: Call<List<GymClass>>, t: Throwable) {
                Toast.makeText(activity, "No Classes found!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BookingsViewModel::class.java)

        backend.gyms().enqueue(object : Callback<List<Gym>> {
            override fun onResponse(call: Call<List<Gym>>, response: Response<List<Gym>>) { gyms = response.body()!!.toMutableList() }
            override fun onFailure(call: Call<List<Gym>>, t: Throwable) { Toast.makeText(activity, "No Gyms found!", Toast.LENGTH_SHORT).show() }
        })

        retrieveAndSetCalendarEvents() //TODO(fran): does this need to be performed here and in onStart?

        val min_date = Calendar.getInstance()
        min_date.add(Calendar.MONTH, -1)

        val max_date = Calendar.getInstance()
        max_date.add(Calendar.MONTH, 3)

        binding.calendarView.setMinimumDate(min_date)
        binding.calendarView.setMaximumDate(max_date)
        binding.calendarView.clearSelectedDays()

        uiShowSelectedGymClassInteractions(false)

        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar: Calendar = eventDay.calendar

                val booking = userClassFromCalendar(clickedDayCalendar)
                //TODO?(fran): instead of having to check in this very poor man's way we could subclass EventDay and add the information of the class inside,
                // though that may cause outdated state issues

                if(booking!=null){
                    val startDate = stringToCalendar(booking.startDate)
                    val endDate = stringToCalendar(booking.endDate)

                    val gymName = gyms?.find { it.id==booking.gymId }?.name
                    binding.bookClassTitle.text = booking.type + if(gymName!=null) " - " + gymName else ""
                    binding.bookClassTime.text = //eg 'Sat 12 Aug 12:30 - 13:30'
                        SimpleDateFormat("EE").format(startDate.time) + " " + startDate.get(Calendar.DAY_OF_MONTH).toString() + " " + SimpleDateFormat("MMM").format(startDate.time) + " " +
                        startDate.get(Calendar.HOUR_OF_DAY).toString() +":"+ startDate.get(Calendar.MINUTE).toString() + " - " +
                        endDate.get(Calendar.HOUR_OF_DAY).toString() +":"+ endDate.get(Calendar.MINUTE).toString()
                    binding.bookClassDesc.text = shuffleString("Heavy weightlifting Arnold Schwarzenegger style before becoming the Terminator") //TODO: retrieve real gym class description
                    uiShowSelectedGymClassInteractions(true)
                }
                else{
                    uiShowSelectedGymClassInteractions(false)
                }
            }
        })

        //binding.bookBtnReview.setOnClickListener {  } //NOTE(fran): this need not exist, the info on the class description is enough

        binding.bookBtnUnbook.setOnClickListener {
            //Log.d("",calendarToString(binding.calendarView.firstSelectedDate))
            //INFO: At this point 'binding.calendarView.firstSelectedDate' always has a valid date that corresponds to the currently selected gym class
            AlertDialog.Builder(activity)
                .setTitle("Confirm Unbooking")
                .setMessage("Are you sure you want to unbook from this class?")
                .setPositiveButton("Yes") { dialog, id -> unbookCalendarEvent(); dialog.cancel() }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
                .create()
                .show()
        }

    }

    override fun onStart() {
        super.onStart()

        retrieveAndSetCalendarEvents()
    }

}