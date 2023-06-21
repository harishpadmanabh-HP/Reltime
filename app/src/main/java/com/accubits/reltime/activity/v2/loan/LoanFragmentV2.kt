package com.accubits.reltime.activity.v2.loan

import android.content.Intent
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.accubits.reltime.activity.v2.myloan.MyLoanActivity
import com.accubits.reltime.databinding.FragmentLoanV2Binding
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.tabs.TabLayout

import android.widget.TextView

import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.accubits.reltime.R
import com.google.android.material.tabs.TabLayoutMediator


@AndroidEntryPoint
class LoanFragmentV2 : Fragment() {

    private var _binding: FragmentLoanV2Binding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanV2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        val pagerAdapter = LoanPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter
//        binding.tabLayout.setupWithViewPager(binding.viewPager)
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position ->
            if (position == 0)
                tab.text = "Borrow"
            else tab.text = "Lend"
        }.attach()

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab: TabLayout.Tab? = binding.tabLayout.getTabAt(i)
            val relativeLayout = LayoutInflater.from(requireActivity())
                .inflate(R.layout.layout_tab, binding.tabLayout, false) as RelativeLayout
            val tabTextView = relativeLayout.findViewById<View>(R.id.tab_title) as TextView
            tabTextView.text = tab!!.text
            if (i == 0) {
                relativeLayout.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.bg_tab_left_selected)
                tabTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                relativeLayout.findViewById<RelativeLayout>(R.id.tabIndicator).setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.linear_gradient_top
                    )
                )
            } else {
                relativeLayout.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.bg_tab_unselected)
                tabTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white80))
                relativeLayout.findViewById<RelativeLayout>(R.id.tabIndicator).setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.bg_selected_tab
                    )
                )
            }
            tab.customView = relativeLayout
//            (tab.customView as RelativeLayout).setPadding(0, 0, 0, 0)
//            tab.select()
        }

        addOnTabSelectedListener()
    }

    private fun addOnTabSelectedListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab?.customView?.findViewById<TextView>(R.id.tab_title)
                if (view is TextView) {
                    view.setTypeface(view.typeface, NORMAL)
                }
                tab?.customView?.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.bg_tab_unselected)
                tab?.customView?.findViewById<TextView>(R.id.tab_title)
                    ?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white80))
                tab?.customView?.findViewById<RelativeLayout>(R.id.tabIndicator)?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.bg_selected_tab
                    )
                )
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val view = tab?.customView?.findViewById<TextView>(R.id.tab_title)
                if (view is TextView) {
                    view.setTypeface(view.typeface, BOLD)
                }
                tab?.customView?.findViewById<TextView>(R.id.tab_title)
                    ?.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                tab?.customView?.findViewById<RelativeLayout>(R.id.tabIndicator)?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.linear_gradient_top
                    )
                )

                if (tab!!.position == 0)
                    tab.customView?.background =
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.bg_tab_left_selected
                        )
                else
                    tab.customView?.background =
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.bg_tab_right_selected
                        )
            }
        })
    }

    private fun setListeners() {
        binding.txtMyLoans.setOnClickListener {
            startActivity(Intent(requireActivity(), MyLoanActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}