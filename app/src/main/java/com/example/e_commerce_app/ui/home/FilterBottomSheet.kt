package com.example.e_commerce_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.e_commerce_app.R
import com.example.e_commerce_app.util.PriceFormat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.sheet_filters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val priceSlider = view.findViewById<RangeSlider>(R.id.priceSlider)
        val ratingSlider = view.findViewById<Slider>(R.id.ratingSlider)
        val priceLabel = view.findViewById<TextView>(R.id.priceRangeLabel)
        val ratingLabel = view.findViewById<TextView>(R.id.ratingLabel)
        val applyBtn = view.findViewById<Button>(R.id.filtersApply)
        val resetBtn = view.findViewById<Button>(R.id.filtersReset)

        val current = viewModel.filters.value
        val initialMax = if (current.maxPrice == Double.MAX_VALUE) 100_000f else current.maxPrice.toFloat()
        val initialMin = current.minPrice.toFloat().coerceAtMost(initialMax)
        priceSlider.setValues(initialMin, initialMax)
        ratingSlider.value = current.minRating.toFloat()

        renderPrice(priceLabel, initialMin, initialMax)
        renderRating(ratingLabel, ratingSlider.value)

        priceSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            renderPrice(priceLabel, values[0], values[1])
        }
        ratingSlider.addOnChangeListener { _, value, _ ->
            renderRating(ratingLabel, value)
        }

        applyBtn.setOnClickListener {
            val values = priceSlider.values
            val maxValue = values[1].toDouble()
            val maxOut = if (maxValue >= 100_000.0) Double.MAX_VALUE else maxValue
            viewModel.applyFilters(
                HomeViewModel.Filters(
                    minPrice = values[0].toDouble(),
                    maxPrice = maxOut,
                    minRating = ratingSlider.value.toDouble()
                )
            )
            dismiss()
        }

        resetBtn.setOnClickListener {
            viewModel.applyFilters(HomeViewModel.Filters())
            dismiss()
        }
    }

    private fun renderPrice(label: TextView, min: Float, max: Float) {
        val maxText = if (max >= 100_000f) "₹100,000+"
        else PriceFormat.format(max.toDouble())
        label.text = "${PriceFormat.format(min.toDouble())} — $maxText"
    }

    private fun renderRating(label: TextView, value: Float) {
        label.text = if (value <= 0f) getString(R.string.filter_any)
        else "%.1f ★ & up".format(value)
    }

    companion object {
        fun newInstance(): FilterBottomSheet = FilterBottomSheet()
    }
}