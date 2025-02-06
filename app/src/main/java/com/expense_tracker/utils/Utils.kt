package com.expense_tracker.utils

import com.expense_tracker.R
import com.expense_tracker.data.model.ExpenseEntity
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils{

    fun formatDateToHumanReadableForm(dateInMilliSeconds:Long):String{
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMilliSeconds)
    }

    fun formatDateForChart(dateinMilliSeconds: Long): String{
        val dateFormatter = SimpleDateFormat("dd-MMM", Locale.getDefault())
        return dateFormatter.format(dateinMilliSeconds)
    }

    fun formatCurrency(
        amount:Double,
        locale: Locale = Locale.CANADA
    ): String{
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        return currencyFormatter.format(amount)
    }

    fun formatDayMonthYear(dateInMilliseconds:Long):String{
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMilliseconds)
    }

    fun formatStringDateToMonthDayYear(date:String):String{
        val milliseconds = getMillisecondsFromDate(date)
        return formatDayMonthYear(milliseconds)
    }

    fun getMillisecondsFromDate(date:String):Long{
        return getMilliFromDate(date)
    }

    fun getMilliFromDate(dateFormat: String?):Long{
        var date = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try{
            date = formatter.parse(dateFormat)
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return date.time
    }

    fun getItemIcon(item: ExpenseEntity):Int{
        return if(item.title == "Paypal"){
            R.drawable.ic_paypal
        }else if(item.title == "Netflix"){
            R.drawable.ic_netflix
        }else if(item.title == "Starbucks"){
            R.drawable.ic_starbucks
        }else {
            R.drawable.ic_upwork
        }
    }
}