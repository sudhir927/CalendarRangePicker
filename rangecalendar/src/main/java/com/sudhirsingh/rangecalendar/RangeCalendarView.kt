package com.sudhirsingh.rangecalendar

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

class RangeCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var viewPager: ViewPager2
    private lateinit var monthYearTextView: TextView
    private lateinit var selectedDateTextView: TextView
    private lateinit var previousButton: ImageView
    private lateinit var nextButton: ImageView

    private val disabledDates = mutableSetOf<Calendar>()
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    private val selectedDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private val months = mutableListOf<Calendar>()
    private lateinit var adapter: CalendarPagerAdapter

    init {
        orientation = VERTICAL
        setupView()
        initMonths()
        setupViewPager()
        setupNavigation()
    }

    private fun setupView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.calendar_layout, this, true)

        viewPager = view.findViewById(R.id.calendarViewPager)
        monthYearTextView = view.findViewById(R.id.monthYearTextView)
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView)
        previousButton = view.findViewById(R.id.previous)
        nextButton = view.findViewById(R.id.next)
    }

    fun setDisabledDates(dates: Set<Calendar>) {
        disabledDates.clear()
        disabledDates.addAll(dates)
        adapter.notifyDataSetChanged()
    }

    private fun initMonths() {
        // Add previous, current and next months
        val current = Calendar.getInstance()

        val prevMonth = current.clone() as Calendar
        prevMonth.add(Calendar.MONTH, -1)
        months.add(prevMonth)

        months.add(current)

        val nextMonth = current.clone() as Calendar
        nextMonth.add(Calendar.MONTH, 1)
        months.add(nextMonth)
    }

    private fun setupViewPager() {
        adapter = CalendarPagerAdapter(months, disabledDates, selectedStartDate, selectedEndDate) { selectedDate ->
            handleDateSelection(selectedDate)
        }

        viewPager.adapter = adapter
        viewPager.setCurrentItem(1, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateMonthYearText(position)

                // Load more months dynamically when reaching edges
                if (position == 0) {
                    loadPreviousMonth()
                } else if (position == months.size - 1) {
                    loadNextMonth()
                }
            }
        })

        updateMonthYearText(1)
    }

    private fun setupNavigation() {
        previousButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
        }

        nextButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
        }
    }

    private fun loadPreviousMonth() {
        val firstMonth = months.first().clone() as Calendar
        firstMonth.add(Calendar.MONTH, -1)
        months.add(0, firstMonth)
        adapter.notifyItemInserted(0)
        viewPager.setCurrentItem(1, false)
    }

    private fun loadNextMonth() {
        val lastMonth = months.last().clone() as Calendar
        lastMonth.add(Calendar.MONTH, 1)
        months.add(lastMonth)
        adapter.notifyItemInserted(months.size - 1)
    }

    private fun updateMonthYearText(position: Int) {
        val calendar = months[position]
        monthYearTextView.text = dateFormat.format(calendar.time)
    }

    private fun handleDateSelection(selectedDate: Calendar) {
        if (selectedStartDate == null) {
            // First date selection
            selectedStartDate = selectedDate
            selectedDateTextView.text = "${selectedDateFormat.format(selectedDate.time)} - Select end date"
        } else if (selectedEndDate == null) {
            // Second date selection - complete the range
            if (selectedDate.after(selectedStartDate)) {
                selectedEndDate = selectedDate
                updateSelectedDateText()
            } else {
                // If second date is before first date, swap them
                selectedEndDate = selectedStartDate
                selectedStartDate = selectedDate
                updateSelectedDateText()
            }
        } else {
            // Third selection - reset and start new range
            selectedStartDate = selectedDate
            selectedEndDate = null
            selectedDateTextView.text = "${selectedDateFormat.format(selectedDate.time)} - Select end date"
        }

        // Update the calendar to show range highlights
        adapter.updateSelection(selectedStartDate, selectedEndDate)
        adapter.notifyDataSetChanged()
    }

    private fun updateSelectedDateText() {
        selectedStartDate?.let { start ->
            selectedEndDate?.let { end ->
                selectedDateTextView.text = "${selectedDateFormat.format(start.time)} - ${selectedDateFormat.format(end.time)}"
            } ?: run {
                selectedDateTextView.text = selectedDateFormat.format(start.time)
            }
        }
    }

    fun getSelectedRange(): Pair<Calendar?, Calendar?> {
        return Pair(selectedStartDate, selectedEndDate)
    }

    fun clearSelection() {
        selectedStartDate = null
        selectedEndDate = null
        selectedDateTextView.text = "Select Date Range"
        adapter.updateSelection(null, null)
        adapter.notifyDataSetChanged()
    }
}
class CalendarPagerAdapter(
    private val months: List<Calendar>,
    private val disabledDates: Set<Calendar>,
    private var selectedStartDate: Calendar?,
    private var selectedEndDate: Calendar?,
    private val onDateSelected: (Calendar) -> Unit
) : RecyclerView.Adapter<CalendarPagerAdapter.CalendarViewHolder>() {
    private val today = Calendar.getInstance()

    private val selectedColor = "#FF018786".toColorInt() // Purple color for selection
    private val rangeColor = "#F3E5F5".toColorInt() // Light purple for range
    private val disabledColor = "#CCCCCC".toColorInt() // Gray for disabled dates

    fun updateSelection(startDate: Calendar?, endDate: Calendar?) {
        selectedStartDate = startDate
        selectedEndDate = endDate
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayViews = mutableListOf<TextView>()

        init {
            // Collect all day TextViews
            for (i in 1..42) {
                val resId = itemView.resources.getIdentifier("day$i", "id", itemView.context.packageName)
                val dayView = itemView.findViewById<TextView>(resId)
                dayViews.add(dayView)

                dayView.setOnClickListener {
                    val day = dayView.text.toString()
                    if (day.isNotEmpty() && day != "00") {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val monthCalendar = months[position].clone() as Calendar
                            monthCalendar.set(Calendar.DAY_OF_MONTH, day.toInt())

                            // Check if date is disabled
                            if (!isDateDisabled(monthCalendar)) {
                                onDateSelected(monthCalendar)
                            }
                        }
                    }
                }

            }
        }

        fun bind(calendar: Calendar) {
            // Clear all days first
            dayViews.forEach {
                it.text = ""
                it.isEnabled = true
                it.setBackgroundColor(Color.TRANSPARENT)
                it.setTextColor(Color.BLACK)
            }

            // Set up the calendar for the month
            val firstDayOfMonth = calendar.clone() as Calendar
            firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)

            val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK)

            // Adjust for Monday as first day of week
            var dayPosition = when (firstDayOfWeek) {
                Calendar.SUNDAY -> 6
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> 0
            }

            // Fill in the days of the current month
            for (day in 1..maxDaysInMonth) {
                if (dayPosition < dayViews.size) {
                    val dayCalendar = calendar.clone() as Calendar
                    dayCalendar.set(Calendar.DAY_OF_MONTH, day)

                    dayViews[dayPosition].text = day.toString()

                    // Apply styling based on selection state
                    applyDateStyling(dayViews[dayPosition], dayCalendar)

                    dayPosition++
                }
            }

            // Fill previous month's days if needed (show as disabled)
            val prevMonth = calendar.clone() as Calendar
            prevMonth.add(Calendar.MONTH, -1)
            val daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

            var prevMonthDay = daysInPrevMonth
            for (i in dayPosition - 1 downTo 0) {
                if (dayViews[i].text.isEmpty()) {
                    dayViews[i].text = prevMonthDay.toString()
                    dayViews[i].isEnabled = false
                    dayViews[i].setTextColor(disabledColor)
                    prevMonthDay--
                }
            }

            // Fill next month's days if needed (show as disabled)
            var nextMonthDay = 1
            for (i in dayPosition until dayViews.size) {
                if (dayViews[i].text.isEmpty()) {
                    dayViews[i].text = nextMonthDay.toString()
                    dayViews[i].isEnabled = false
                    dayViews[i].setTextColor(disabledColor)
                    nextMonthDay++
                }
            }
        }

        private fun applyDateStyling(dayView: TextView, dayCalendar: Calendar) {
            if (isDateDisabled(dayCalendar)) {
                dayView.isEnabled = false
                dayView.setTextColor(disabledColor)
                return
            }

            // Check if this date is selected as start or end
            val isStartDate = selectedStartDate != null && isSameDay(dayCalendar, selectedStartDate!!)
            val isEndDate = selectedEndDate != null && isSameDay(dayCalendar, selectedEndDate!!)

            // Check if this date is within the selected range
            val isInRange = selectedStartDate != null && selectedEndDate != null &&
                    dayCalendar.after(selectedStartDate) && dayCalendar.before(selectedEndDate)

            when {
                isStartDate || isEndDate -> {
                    // Selected start or end date
                    dayView.setBackgroundColor(selectedColor)
                    dayView.setTextColor(Color.WHITE)
                }
                isInRange -> {
                    // Date within the range
                    dayView.setBackgroundColor(rangeColor)
                    dayView.setTextColor(Color.BLACK)
                }
                else -> {
                    // Normal date
                    dayView.setBackgroundColor(Color.TRANSPARENT)
                    dayView.setTextColor(Color.BLACK)
                }
            }
        }

        private fun isDateDisabled(calendar: Calendar): Boolean {
            // Disable if the date is in disabledDates OR before today
            if (calendar.before(today)) return true
            return disabledDates.any { isSameDay(it, calendar) }
        }


        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_month_layout, parent, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(months[position])
    }

    override fun getItemCount(): Int = months.size
}