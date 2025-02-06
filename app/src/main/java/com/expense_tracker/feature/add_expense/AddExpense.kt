@file:OptIn(ExperimentalMaterial3Api::class)

package com.expense_tracker.feature.add_expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expense_tracker.R
import com.expense_tracker.base.AddExpenseNavigationEvent
import com.expense_tracker.base.NavigationEvent
import com.expense_tracker.data.model.ExpenseEntity
import com.expense_tracker.ui.theme.InterFontFamily
import com.expense_tracker.ui.theme.LightGrey
import com.expense_tracker.ui.theme.Typography
import com.expense_tracker.utils.Utils
import com.expense_tracker.widget.ExpenseTextView

@Composable
fun AddExpense(
    navController: NavController,
    isIncome: Boolean,
    viewModel: AddExpenseViewModel = hiltViewModel()
){
    val menuExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect{ event ->
            when(event){
                NavigationEvent.NavigateBack -> navController.popBackStack()

                AddExpenseNavigationEvent.MenuOpenedClicked -> {
                    menuExpanded.value = true
                }

                else -> {}
            }

        }
    }

    Surface(modifier = Modifier.fillMaxSize()){
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (nameRow, card, topBar) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.ic_topbar),
                contentDescription = null,
                modifier = Modifier.constrainAs(topBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 64.dp,
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .constrainAs(nameRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            viewModel.onEvent(AddExpenseUiEvent.OnBackPressed)
                        }

                )
                ExpenseTextView(
                    text = "Add ${if(isIncome) "Income" else "Expense"}",
                    style = Typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ){
                    Image(
                        painter = painterResource(id = R.drawable.dots_menu),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                viewModel.onEvent(AddExpenseUiEvent.OnMenuClicked)
                            }
                    )
                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = {
                            menuExpanded.value = false
                        }
                    ){
                        DropdownMenuItem(
                            text = { ExpenseTextView(text = "Profile") },
                            onClick = {
                                menuExpanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                ExpenseTextView(text = "Settings")
                            },
                            onClick = {
                                menuExpanded.value = false
                            },
                        )
                    }
                }
            }
            DateForm(
                modifier = Modifier.constrainAs(card){
                    top.linkTo(nameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onAddExpenseClick = {
                    viewModel.onEvent(AddExpenseUiEvent.OnAddExpenseClicked(it))
                },
                isIncome
            )
        }
    }


}

@Composable
fun DateForm(
    modifier:Modifier,
    onAddExpenseClick: (model: ExpenseEntity) -> Unit,
    isIncome:Boolean,
){

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableLongStateOf(0L) }
    var dateDialogVisibility by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf(if(isIncome) "Income" else "Expense") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleComponent(title = "name")
        ExpenseDropDown(
            if(isIncome) listOf(
                "Paypal",
                "Salary",
                "Freelance",
                "Investments",
                "Bonus",
                "Rental Income",
                "Other Income"
            ) else listOf(
                "Grocery",
                "Netflix",
                "Rent",
                "Paypal",
                "Starbucks",
                "Shopping",
                "Transport",
                "Utilities",
                "Dining Out",
                "Entertainment",
                "Healthcare",
                "Insurance",
                "Subscriptions",
                "Education",
                "Debt Payments",
                "Gifts & Donations",
                "Travel",
                "Other Expenses"
            ),
            onItemSelected = {name = it}
        )
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("amount")
        OutlinedTextField(
            value = amount,
            onValueChange = {newValue ->
                amount = newValue.filter{ it.isDigit() || it == '.'}
            },
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = VisualTransformation{ text ->
                val out = "$" + text.text
                val currencyOffsetTranslator = object : OffsetMapping{
                    override fun originalToTransformed(offset:Int):Int{
                        return offset + 1
                    }
                    override fun transformedToOriginal(offset:Int):Int{
                        return if(offset>0) offset-1 else 0
                    }
                }

                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = {
                ExpenseTextView(text = "Enter amount")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
            ),
        )
        Spacer(modifier = Modifier.size(24.dp))
        TitleComponent("date")
        OutlinedTextField(
            value = if(date == 0L) "" else Utils.formatDateToHumanReadableForm(date),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    dateDialogVisibility = true
                },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
            ),
            placeholder = {
                ExpenseTextView(text = "Select date")
            }
        )
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = {
                val model = ExpenseEntity(
                    id = null,
                    title = name,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    date = Utils.formatDateToHumanReadableForm(date),
                    type = type
                )
                onAddExpenseClick(model)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ){
            ExpenseTextView(
                text = "Add ${if(isIncome) "Income" else "Expense"}",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
    if(dateDialogVisibility){
        ExpenseDatePickerDialog(
            onDateSelected = {
                date = it
                dateDialogVisibility = false
            },
            onDismiss = {
                dateDialogVisibility = false
            }
        )
    }
}

@Composable
fun ExpenseDatePickerDialog(
    onDateSelected: (date:Long) -> Unit,
    onDismiss: () -> Unit = {}
){
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L

    DatePickerDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {onDateSelected(selectedDate)}
            ) {
                ExpenseTextView(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDateSelected(selectedDate) }
            ) {
                ExpenseTextView(text = "Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDropDown(
    listOfItems: List<String>,
    onItemSelected: (item:String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(listOfItems[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = it}
    ){
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(
                fontFamily = InterFontFamily,
                color = Color.Black
            ),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black,
                disabledTextColor = Color.Black,
                disabledPlaceholderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            )
        )
        ExposedDropdownMenu(
            expanded,
            onDismissRequest = {}
        ){
            listOfItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        ExpenseTextView(text = item)
                    },
                    onClick = {
                        selectedItem = item
                        onItemSelected(selectedItem)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TitleComponent(
    title: String
){
    ExpenseTextView(
        text = title.uppercase(),
        color = LightGrey,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAddExpense(){
    AddExpense(rememberNavController(), true)
}