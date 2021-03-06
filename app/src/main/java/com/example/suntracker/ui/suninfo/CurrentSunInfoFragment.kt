package com.example.suntracker.ui.suninfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.suntracker.R
import com.example.suntracker.data.provider.DEFAULT_LAT
import com.example.suntracker.ui.ScopedFragment
import kotlinx.android.synthetic.main.current_sun_info_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*

class CurrentSunInfoFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CurrentSunInfoViewModelFactory by instance()

    private lateinit var viewModel: CurrentSunInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_sun_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel =ViewModelProvider(this, viewModelFactory)
            .get(CurrentSunInfoViewModel::class.java)

        bindUI()

        updateToolBar()
    }

    private fun bindUI() = launch {

        val currentSunInfo = viewModel.sunInfo.await()

        val sunLocation = viewModel.sunLocation.await()

        sunLocation.observe(viewLifecycleOwner, Observer { location ->
            if(location == null) return@Observer
            updateLocation(location = location.lat.toString())
        })

        currentSunInfo.observe(viewLifecycleOwner, Observer {
            if(it == null) return@Observer

            progress_circular.visibility = View.GONE

            sun_rise_TV.text = requireContext().getString(R.string.sunRise, it.sunrise)
            sun_set_TV.text = requireContext().getString(R.string.sunSet, it.sunset)

        })
    }

    private fun updateLocation(location: String){
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "lat: ${location.subSequence(0,4)}"
    }

    private fun updateToolBar(){
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Today's track"
    }

}
