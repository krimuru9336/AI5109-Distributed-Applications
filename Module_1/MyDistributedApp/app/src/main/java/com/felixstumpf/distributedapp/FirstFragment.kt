package com.felixstumpf.distributedapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.felixstumpf.distributedapp.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val namesDataSource = NamesDataSource(requireContext())
        namesDataSource.open()


        binding.buttonInsert.setOnClickListener {
            namesDataSource.insertName(binding.editTextName.text.toString())
            Toast.makeText(requireContext(), "Inserted ${binding.editTextName.text.toString()}", Toast.LENGTH_SHORT).show()

            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonRetrieve.setOnClickListener{
            var allNames : String = ""
            namesDataSource.getAllNames().forEach {
                allNames += it.name + ", "
            }
            allNames = allNames.substring(0, allNames.length - 2)
            Toast.makeText(requireContext(), allNames, Toast.LENGTH_SHORT).show()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}