package com.devshadat.ciblapp.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devshadat.ciblapp.R
import com.devshadat.ciblapp.databinding.FragmentBkashPaymentBinding
import com.devshadat.ciblapp.databinding.FragmentNagadPaymentBinding
import com.devshadat.ciblapp.models.Receipt
import com.devshadat.ciblapp.utilities.AlertManager
import com.devshadat.ciblapp.utilities.ReceiptGenerator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class NagadPaymentFragment : Fragment(), AlertManager.OnPrintListener {

    private var _binding: FragmentNagadPaymentBinding? = null
    private val binding get() = _binding!!

    private var receipt: Receipt? = null
    private var amount = ""
    private var name = ""
    private var narration = ""
    private var number = ""
    private var error = ""
    private var currentLocation = ""

    private val VIEW_CODE = 101
    private val SHARE_CODE = 102

    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNagadPaymentBinding.inflate(inflater, container, false);
        val view = binding.root;
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        _binding!!.btnSubmit.setOnClickListener {
            amount = _binding!!.etNagadAmount.text.toString()
            name = _binding!!.etNagadName.text.toString()
            number = _binding!!.etNagadNumber.text.toString()
            narration = _binding!!.etNagadNarration.text.toString()

            if (isValidated()) {
                Toast.makeText(context, currentLocation, Toast.LENGTH_SHORT).show()
                val current = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                val time = current.format(formatter)

                receipt = Receipt(
                    "Nagad Fund Transfer", R.drawable.ic_nagad, name, number, amount, narration, currentLocation, time
                )
                val alertDialogManager = AlertManager(activity)
                alertDialogManager.showPrintDialog(
                    this@NagadPaymentFragment, receipt!!
                )
            } else {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
                    var addresses: List<Address>? = null
                    try {
                        addresses =
                            geocoder!!.getFromLocation(location.latitude, location.longitude, 1)
                        currentLocation =
                            "${addresses!![0].locality.toString()}, ${addresses[0].countryName}"

                        Log.e("location", "getLastLocation: ${addresses!![0].locality}")

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            askPermission()
        }
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
    }

    private fun isValidated(): Boolean {
        if (number.isBlank()) {
            error = "Please input number"
            return false
        } else if (number.length < 11) {
            error = "number should be 11 digit"
            return false
        } else if (name.isBlank()) {
            error = "Please input name"
            return false
        } else if (amount.isBlank()) {
            error = "Please input amount"
            return false
        } else if (narration.isBlank()) {
            error = "Please input narration"
            return false
        }

        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(
                            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

        }
    }

    override fun didPrint(boolean: Boolean) {
        if (boolean) {
            val pdfConverter = ReceiptGenerator()
            pdfConverter.createPdf(requireActivity(), receipt!!, requireActivity(), VIEW_CODE)
            Toast.makeText(context, "PDF Downloaded", Toast.LENGTH_SHORT).show()
        }
    }

    override fun didShare(boolean: Boolean) {
        if (boolean) {
            val pdfConverter = ReceiptGenerator()
            pdfConverter.createPdf(requireActivity(), receipt!!, requireActivity(), SHARE_CODE)
        }
    }
}
