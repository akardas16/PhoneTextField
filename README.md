# PhoneTextField

## Showcases

<p align="center">
 <img src="https://github.com/akardas16/PhoneTextField/assets/28716129/8546fc0c-264b-4914-8b04-1a86dbc30dad"  width="31.7%" />
   <img src="https://github.com/akardas16/PhoneTextField/assets/28716129/b4724d2a-1bba-4ba8-aaed-36b292d94f94" width="180" alt="CI" />
<img src="https://github.com/akardas16/PhoneTextField/assets/28716129/7c6e584f-9f9b-4304-9956-c09aa7c38304" width="35.7%" >
</p>


## Install

* Jitpack

```Kotlin
 allprojects {
   repositories {
        maven { url 'https://jitpack.io' } // if you are using build.gradle.kts use maven(url = {uri("https://jitpack.io")})
    }
 }
```


```Kotlin
implementation 'com.github.akardas16:PhoneTextField:1.5.0'
```

## Basic Usage

* By default PhoneTextField will show device local country. The library will show different format phone masks for different countries.

```Kotlin
 var text by remember { mutableStateOf(TextFieldValue( text = "")) }

 PhoneTextField(textValue = text, onTextValueChanged = {text = it}, showCode = false, showFlag = true)
```
* Use modifier to change PhoneTextField UI
  
```Kotlin
PhoneTextField(textValue = text, onTextValueChanged = {text = it},
            showCode = false, showFlag = true, modifier = Modifier
               .height(52.dp)
                .fillMaxSize(0.6f)
                .border(width = 1.5.dp,Color.Gray, shape = RoundedCornerShape(12.dp)))
```


*  Available parameters

  ```Kotlin
    textValue: TextFieldValue,
    onTextValueChanged:(textValue: TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    showFlag: Boolean = true,
    showCode: Boolean = false
    labelColor: Color = Color.Gray,
    fontFamily: FontFamily = FontFamily(Font(R.font.semibold)), // default custom font
    fontSize: TextUnit = 17.sp,
    cursorColor: Color = Color.Black,
    sheetSearchText:String = "Search Country..."
```

 
