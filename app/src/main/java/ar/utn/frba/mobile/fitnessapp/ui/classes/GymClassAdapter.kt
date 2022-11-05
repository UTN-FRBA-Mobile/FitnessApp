package ar.utn.frba.mobile.fitnessapp.ui.classes

import android.content.Context
import android.content.res.Resources.Theme
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.model.GymClass

class GymClassAdapter(private val currContext: Context, private val arrayList: ArrayList<GymClass>)
    : ArrayAdapter<GymClass>(currContext, R.layout.class_info_item, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.class_info_item, null)

        val cardInfo: ConstraintLayout = view.findViewById(R.id.cardInfo)

        val gymClassType: TextView = view.findViewById(R.id.classType)
        val gymClassProfessor: TextView = view.findViewById(R.id.professor)
        val gymClassStartDate: TextView = view.findViewById(R.id.startDate)
        val gymClassEndDate: TextView = view.findViewById(R.id.endDate)
        val gymClassPeopleStatus: TextView = view.findViewById(R.id.peopleStatus)

        val gymClass = arrayList[position]

        gymClassType.text = gymClass.type
        gymClassProfessor.text = gymClass.professor
        gymClassStartDate.text = gymClass.startDate
        gymClassEndDate.text = gymClass.endDate
        val peopleStatus = "${gymClass.people} / ${gymClass.maxCapacity}"
        gymClassPeopleStatus.text = peopleStatus

        if (gymClass.people >= gymClass.maxCapacity) {
            val notAvailableColor = currContext.resources.getColor(R.color.purple_200)
            cardInfo.setBackgroundColor(notAvailableColor)
        }

        return view
    }
}