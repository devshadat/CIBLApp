package com.devshadat.ciblapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devshadat.ciblapp.R
import com.devshadat.ciblapp.databinding.FragmentMyHomeBinding

class MyHomeFragment : Fragment() {

    private var _binding: FragmentMyHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyHomeBinding.inflate(inflater, container, false);
        val view = binding.root;
        return view;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.cardViewPayBkash.setOnClickListener(View.OnClickListener {

            findNavController().navigate(R.id.action_myHomeFragment_to_bkashPaymentFragment)
        })

        _binding!!.cardViewPayNagad.setOnClickListener(View.OnClickListener {

            findNavController().navigate(R.id.action_myHomeFragment_to_nagadPaymentFragment)
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()
    }

}
