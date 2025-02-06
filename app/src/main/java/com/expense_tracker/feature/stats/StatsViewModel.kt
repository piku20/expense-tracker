package com.expense_tracker.feature.stats

import com.expense_tracker.base.BaseViewModel
import com.expense_tracker.base.UiEvent
import com.expense_tracker.data.dao.ExpenseDao
import com.expense_tracker.data.model.ExpenseSummary
import com.expense_tracker.utils.Utils
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(val dao: ExpenseDao) : BaseViewModel(){

    val entries = dao.getAllExpensesByDate()
    val topEntries = dao.getTopExpenses()

    fun getEntriesForChart(entries:List<ExpenseSummary>): List<Entry>{
        val list = mutableListOf<Entry>()
        for(entry in entries){
            val formattedDate = Utils.getMillisecondsFromDate(entry.date)
            list.add(Entry(
                formattedDate.toFloat(),
                entry.totalAmount.toFloat()
            ))
        }

        return list
    }

    override fun onEvent(event: UiEvent) {}
}