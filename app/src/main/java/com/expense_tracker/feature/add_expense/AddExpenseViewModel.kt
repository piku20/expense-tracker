package com.expense_tracker.feature.add_expense

import androidx.lifecycle.viewModelScope
import com.expense_tracker.base.AddExpenseNavigationEvent
import com.expense_tracker.base.BaseViewModel
import com.expense_tracker.base.NavigationEvent
import com.expense_tracker.base.UiEvent
import com.expense_tracker.data.dao.ExpenseDao
import com.expense_tracker.data.model.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(val dao:ExpenseDao): BaseViewModel(){

    suspend fun addExpense(expenseEntity: ExpenseEntity):Boolean{
        return try{
            dao.insertExpense(expenseEntity)
            true
        }catch(ex: Throwable){
            false
        }
    }

    override fun onEvent(event: UiEvent) {
        when(event){
            is AddExpenseUiEvent.OnAddExpenseClicked -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        val result = addExpense(event.expenseEntity)
                        if(result) {
                            _navigationEvent.emit(NavigationEvent.NavigateBack)
                        }
                    }
                }
            }

            is AddExpenseUiEvent.OnBackPressed -> {
                viewModelScope.launch{
                    _navigationEvent.emit(NavigationEvent.NavigateBack)
                }
            }

            is AddExpenseUiEvent.OnMenuClicked -> {
                viewModelScope.launch{
                    _navigationEvent.emit(AddExpenseNavigationEvent.MenuOpenedClicked)
                }
            }

        }
    }
}

sealed class AddExpenseUiEvent : UiEvent(){
    data class OnAddExpenseClicked(val expenseEntity: ExpenseEntity) : AddExpenseUiEvent()
    object OnBackPressed : AddExpenseUiEvent()
    object OnMenuClicked: AddExpenseUiEvent()
}
