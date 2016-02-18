# MaterialEditText

动画效果与`support-design`包中的`TextInputLayout`相同，不过允许你自定义一些属性。

[English](https://github.com/Syehunter/MaterialEditText/blob/master/README.md)

事实上最开始的时候我写这个库只是为了自己使用，但是在coding的过程中我觉得我可以把它做一下扩展，让这个库变得更灵活一点~

`MaterialEditText`允许你自定义属性，比如说在最前面加一个icon，添加一键清除文本按钮，设置下划线颜色、光标颜色等等…

导入`MaterialEditText`：

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
	dependencies {
        compile 'com.github.Syehunter:MaterialEditText:1.0.0'
	}

![Gif](http://7xn4z4.com1.z0.glb.clouddn.com/MaterialEditText.gif)

下面是可以自定义的属性：

	<attr name="inputTextSize" format="dimension"/>
    <attr name="inputTextColor" format="color"/>
    <attr name="inputIcon" format="reference"/>
    <attr name="cleanIcon" format="reference"/>
    <attr name="underlineColor" format="color"/>
    <attr name="cursorColor" format="color"/>
    <attr name="hint" format="string"/>
    <attr name="hintScale" format="float"/>
    <attr name="hintColor" format="color"/>
    <attr name="hintScaleColor" format="color"/>
    <attr name="errorSize" format="dimension"/>
    <attr name="errorColor" format="color"/>
    <attr name="length" format="integer"/>
    <attr name="wordCountEnabled" format="boolean"/>
    <attr name="wordCountColor" format="color"/>
    <attr name="expandDuration" format="integer"/>
    
 插入到`.xml`中：
 
 	<z.sye.space.library.MaterialEditText
        android:id="@+id/password_material"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        clean:cursorColor="#773355"
        clean:hint="Password"
        clean:hintScale="0.8"
        clean:hintScaleColor="#09c300"
        clean:inputIcon="@mipmap/ic_launcher"
        clean:inputTextColor="#FFAA99"
        clean:hintColor="#FF5599"
        clean:inputTextSize="18sp"
        clean:underlineColor="#773355"/>
 
 `MaterialEditText`会根据不同的`textSize`来自动调整高度，所以最好将`layout_height`设置为`wrap_content`。
 
 你也可以在代码中定义这些属性:
 
 	mEditText.hint()
            .inputType()
            //.maxLength() if you just want a word count limit, use this instead of filters
            .filters()
			.animatorDuration()
            .setOnGetFocusListener(mOnGetFocusListener)
            .setOnLostFocusListener(mOnLostFocusListener)
            .setOnErrorListener(mOnErrorListener);
            ...
           
  如果你想对`EditText`进行一些操作，而这个方法并没有在`MaterialEditText`中提供的话，调用`mEditText.real()`方法，会把真正的`EditText`返回~~