package com.example.reto.fragments


import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.example.reto.R
import com.example.reto.databinding.FragmentJuego2Binding
import com.example.reto.databinding.ItemDraggableNumberBinding
import kotlin.random.Random

class Juego2Fragment : Fragment() {

    private lateinit var binding: FragmentJuego2Binding
    private val correctOrder = listOf("1", "2", "3", "4", "5")
    private val draggableNumbers = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_juego2, container, false)

        // This is important for data binding to observe LiveData
        binding.lifecycleOwner = viewLifecycleOwner

        // Set fragment as a variable in the binding
        binding.fragment = this

        // Generate unique random numbers for draggable shapes
        generateRandomNumbers()

        // Create draggable shapes dynamically
        for (number in draggableNumbers) {
            createDraggableShape(number.toString())
        }

        binding.llTop.setOnDragListener(dragListener)
        binding.llBottom.setOnDragListener(dragListener)



        return binding.root
    }

    private fun generateRandomNumbers() {
        val uniqueNumbers = mutableSetOf<Int>()
        while (uniqueNumbers.size < 5) {
            uniqueNumbers.add(Random.nextInt(1, 101))
        }
        draggableNumbers.addAll(uniqueNumbers.shuffled())
    }

    private fun createDraggableShape(number: String) {
        val textViewBinding = DataBindingUtil.inflate<ItemDraggableNumberBinding>(
            LayoutInflater.from(requireContext()),
            R.layout.item_draggable_number,
            binding.llTop,
            false
        )
        textViewBinding.number = number

        textViewBinding.root.setOnLongClickListener {
            val clipData = ClipData.newPlainText("number", number)
            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(clipData, dragShadowBuilder, it, 0)
            it.visibility = View.INVISIBLE
            true
        }

        binding.llTop.addView(textViewBinding.root)
    }


    private val dragListener = View.OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                Toast.makeText(context, dragData, Toast.LENGTH_SHORT).show()

                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout
                destination.addView(v)
                v.visibility = View.VISIBLE
                checkCorrectOrder()
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }

    // Function to check the correct order
    fun checkCorrectOrder() {
        val containerBottom = binding.llBottom
        val childCount = containerBottom.childCount
        val orderList = mutableListOf<String>()

        for (i in 0 until childCount) {
            val shape = containerBottom.getChildAt(i) as View
            // Add the shape's identifier to the list (modify as per your identifier)
            orderList.add(shape.tag.toString())
        }

        // Check if the order matches the correct order
        if (orderList == correctOrder) {
            // Display a message or perform any action
            Toast.makeText(context, "Correct Order!", Toast.LENGTH_SHORT).show()
        }
    }
}
