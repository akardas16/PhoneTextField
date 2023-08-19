package com.example.phonetextfield

import android.content.res.AssetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(sheetSearchText:String,onChangedItem:(CountryModel) -> Unit,onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        BottomSheetContent(sheetSearchText,onChangedItem, onDismiss)
    }
}

@Composable
fun BottomSheetContent(sheetSearchText:String, onChangedItem:(CountryModel) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchText by remember {
        mutableStateOf("")
    }


    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        SearchBarSample(text = searchText, sheetSearchText ,onClearedText = { searchText = "" },
            onValueChange = {searchText = it})
    }
    LazyColumn {
        items(countryModelList(context.assets).filter { it.name.contains(searchText,
            ignoreCase = true) }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onChangedItem(item)
                        scope.launch {
                            delay(180)
                            onDismiss()
                        }

                    }
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {

                Text(
                    text = countryFlag(item.iso), maxLines = 1,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text(
                    text = item.name,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.code,
                    modifier = Modifier.padding(end = 8.dp)
                )

            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTextField(
    textValue: TextFieldValue, onTextValueChanged:(textValue: TextFieldValue) -> Unit, modifier: Modifier = Modifier
        .background(Color.Gray.copy(0.2f), shape = RoundedCornerShape(8.dp))
        .height(40.dp), showFlag: Boolean = true, showCode: Boolean = false,
    textColor: Color = Color.Black, labelColor: Color = Color.Gray,
    fontFamily: FontFamily = FontFamily(Font(R.font.semibold)),
    fontSize: TextUnit = 17.sp,
    cursorColor: Color = Color.Black,
    sheetSearchText:String = "Search Country..."
) {
    val context = LocalContext.current
    var selectedItem by remember {
        mutableStateOf(countryModelList(context.assets).first { it.iso == Locale.getDefault().country })
    }
    var showSheet by remember { mutableStateOf(false) }
    var placeHolder by remember {
        mutableStateOf(selectedItem.mask)
    }
    var list by remember {
        mutableStateOf(findListSpaces(format = selectedItem.mask))
    }

    LaunchedEffect(key1 = selectedItem) {

        onTextValueChanged(TextFieldValue(text = ""))
        placeHolder = selectedItem.mask
        list = findListSpaces(format = selectedItem.mask)
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (showSheet) {
            BottomSheet(sheetSearchText,onChangedItem = { selectedItem = it }) {
                showSheet = false
            }
        }
        //With solid background color
        BasicTextField(
            value = textValue,
            onValueChange = {
                if (textValue.text.length > it.text.length) {
                    onTextValueChanged(it)
                    if (it.text.isEmpty()) {
                        placeHolder = selectedItem.mask
                    }
                } else if (it.text.isDigitsOnly() or (it.text.contains(" "))) {
                    if (it.text.length <= list.last()) {
                        onTextValueChanged(it)
                        placeHolder = placeHolder.replaceRange(
                            it.text.lastIndex..it.text.lastIndex,
                            it.text[it.text.lastIndex].toString()
                        )
                        val textLength: Int = it.text.length
                        list.forEach { i ->
                            if (textLength == i && it.text[it.text.lastIndex] != ' ' && it.text.length < placeHolder.length) {
                                onTextValueChanged(
                                    TextFieldValue(
                                    StringBuilder(it.text).insert(i, " ").toString(),
                                    selection = TextRange(textLength + 1)
                                )
                                )

                            }
                        }
                    }
                }

            },
            singleLine = true,
            cursorBrush = SolidColor(cursorColor),
            textStyle = LocalTextStyle.current.copy(
                color = textColor, fontFamily = fontFamily, fontSize = fontSize
            ),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showFlag) {
                        Text(
                            text = countryFlag(selectedItem.iso), modifier = Modifier
                                .bounceClick { showSheet = !showSheet }
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 2.dp), fontSize = 20.sp
                        )
                    }

                    if (showCode) {
                        Text(text = selectedItem.code,
                            modifier = Modifier
                                .bounceClick(1f) { showSheet = !showSheet }
                                .padding(horizontal = 12.dp),
                            fontFamily = fontFamily,
                            fontSize = fontSize
                        )
                    }


                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Text(
                            text = placeHolder,
                            color = labelColor,
                            fontSize = fontSize,
                            fontFamily = fontFamily,
                            modifier = Modifier
                                .alpha(0.7f)
                        )


                        innerTextField()
                    }

                }
            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { println("onNext clicked") }),

            modifier = modifier

        )

    }
}

@Composable
fun SearchBarSample(text:String,sheetSearchText:String, onClearedText:() -> Unit ,onValueChange: (String) -> Unit){
    //With solid background color
    BasicTextField(value = text, onValueChange = onValueChange,

        singleLine = true,
        cursorBrush = SolidColor(Color.Black),
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black
        ),
        decorationBox = {innerTextField ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()) {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = "",tint = Color.Black.copy(0.3f),
                    modifier = Modifier
                        .padding(end = 8.dp, start = 16.dp, top = 4.dp)
                        .width(24.dp)
                        .fillMaxHeight())
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (text.isEmpty()) Text(text = sheetSearchText, color = Color.Gray,
                        modifier = Modifier.alpha(0.7f))

                    innerTextField()
                }
                if (text.isEmpty().not()){
                    IconButton(onClick = { onClearedText() }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "",tint = Color.Black.copy(0.3f),
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxHeight())
                    }

                }

            }
        },
        keyboardOptions = KeyboardOptions(autoCorrect = false,
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Search), modifier = Modifier
            .background(
                Color.Gray.copy(0.2f),
                shape = CircleShape
            )
            .fillMaxWidth(0.86f)
            .height(40.dp)
    )

}

fun countryFlag(code: String) = code
    .uppercase()
    .split("")
    .filter { it.isNotBlank() }
    .map { it.codePointAt(0) + 0x1F1A5 }
    .joinToString("") { String(Character.toChars(it)) }

fun findListSpaces(format:String = "000 000 000"):MutableList<Int>{
    val s = "$format "
    val c = ' '
    val list = arrayListOf<Int>()

    var index = -1
    while (s.indexOf(c, index + 1).also { index = it } != -1) {
        list.add(index)
    }
    return list
}

fun AssetManager.readAssetsFile(fileName : String): String = open(fileName).bufferedReader().use{it.readText()}

fun countryModelList(assets: AssetManager):List<CountryModel>{
    val listCountry = mutableListOf<CountryModel>()
    val array = JSONArray(assets.readAssetsFile("countries.json"))
    for (i in 0 until array.length()){

        val jsonObj = JSONObject(array[i].toString())
        val model = CountryModel(name = jsonObj["name"].toString(),
            code = jsonObj["code"].toString(),
            iso = jsonObj["iso"].toString(),
            mask = jsonObj["mask"].toString())
        listCountry.add(model)
    }
    return listCountry
}

//Bounce Click
private fun Modifier.bounceClick(
    scaleDown: Float = 0.92f,
    onClick: () -> Unit
) = composed {

    val interactionSource = remember { MutableInteractionSource() }

    val animatable = remember {
        androidx.compose.animation.core.Animatable(1f)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> animatable.animateTo(scaleDown)
                is PressInteraction.Release -> animatable.animateTo(1f)
                is PressInteraction.Cancel -> animatable.animateTo(1f)
            }
        }
    }

    Modifier
        .graphicsLayer {
            val scale = animatable.value
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onClick()
        }
}