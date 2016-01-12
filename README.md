# MaterialEditText

Same as TextInputLayout but u can define the attrs as u like.

Actually at first I just wirte this for personal use, but with coding I thought maybe I can make it better, and I did.

The MaterialEditText could let you define attrs what you like.Such as add a icon before, add the clear button at the end, set the textsize, textcolor, underline color, cursor color and so on...

Improt library:

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

Here are the attrs supported:

	
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

Use it in xml:

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

Change the color what u want.<br/>
You'd better set the `layout_height` to `wrap_content` and adjust the layout height by different textsizes.<br/>When textSizes change, the layout_height change as well.

You can also set them in codes such as

	mEditText.hint()
            .inputType()
            //.maxLength() if you just want a word count limit, use this instead of filters
            .filters()
			.animatorDuration()
            .setOnGetFocusListener(mOnGetFocusListener)
            .setOnLostFocusListener(mOnLostFocusListener)
            .setOnErrorListener(mOnErrorListener);
            ...

Method `mEditText.real()` can return the real EditText if you need some methods in EditText but didn't support in MaterialEditText.class.

	
