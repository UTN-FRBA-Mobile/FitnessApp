package ar.utn.frba.mobile.fitnessapp.ui.classes

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import ar.utn.frba.mobile.fitnessapp.R
import ar.utn.frba.mobile.fitnessapp.model.backend.BackendService
import ar.utn.frba.mobile.fitnessapp.model.backend.BookingBody
import ar.utn.frba.mobile.fitnessapp.model.backend.call

class AcceptDialogFragment : DialogFragment() {
    private val args: AcceptDialogFragmentArgs by navArgs()
    private val backend: BackendService = BackendService.create()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.dialog_start_game)
                .setPositiveButton(
                    R.string.accept_gym
                ) { dialog, id ->
                    // START THE GAME!
                    backend
                    println("UserID: 1, gymID: " + args.gymClass.gymId + "classId: " + args.gymClass.id)
                    backend.reserve(BookingBody("1"), args.gymClass.gymId, args.gymClass.id).call(
                        onResponse = { _, response ->
                            println(response)
                            Toast.makeText(
                                (dialog as AlertDialog).context,
                                R.string.class_successfully_booked,
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        },
                        onFailure = { _, response ->
                            Toast.makeText(
                                (dialog as AlertDialog).context,
                                R.string.class_book_error,
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()

                        })
                }
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}