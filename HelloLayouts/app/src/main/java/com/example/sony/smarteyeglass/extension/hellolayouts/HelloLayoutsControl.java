/*
Copyright (c) 2011, Sony Mobile Communications Inc.
Copyright (c) 2014, Sony Corporation

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications Inc.
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.sony.smarteyeglass.extension.hellolayouts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to render an XML-based layout on the SmartEyeglass display.
 * While the display data is supplied in an Android layout, you can convert
 * it to a bitmap for rendering, if your app must be compatible with a device
 * such as SmartWatch that does not support layouts, or for special customized
 * displays.
 * This sample demonstrates both rendering methods.
 *
 * The resources for this sample include one display data files,
 * and layout.xml, which demonstrate all standard UI components that can
 * be used on SmartEyeglass, except Gallery.
 *
 * The updateLayout() method uses the layout.xml resource to display a screen
 * and three lines of text: LAYOUT in a large font, and smaller text lines
 * "Swipe left to change to bitmap" and "Tap to update". The handlers for these
 * actions call updateBitmap(), which uses the bitmap render method to show the
 * corresponding BITMAP screen.
 */
public final class HelloLayoutsControl extends ControlExtension {

    /** Instance of the Control Utility class. */
    private final SmartEyeglassControlUtils utils;

    /** Uses SmartEyeglass API version*/
    private static final int SMARTEYEGLASS_API_VERSION = 1;

    /** The application context. */
    private final Context context;

    /*  The boolean value that tracks the image */
    private boolean iconImage;

    /**
     * Instantiates a control object, initializing the rendering-method map
     * so that a swipe-left action renders a bitmap, and the swipe-right
     * action renders a layout.
     *
     * @param context            The context.
     * @param hostAppPackageName Package name of SmartEyeglass host application.
     */
    public HelloLayoutsControl(final Context context,
            final String hostAppPackageName) {
        super(context, hostAppPackageName);
        this.context = context;
        utils = new SmartEyeglassControlUtils(hostAppPackageName, null);
        utils.setRequiredApiVersion(SMARTEYEGLASS_API_VERSION);
        utils.activate(context);
        reset();
    }

    // Reset state object and assign initial renderer object
    @Override
    public void onStart() {
        reset();
    }

    // Update the display when app becomes visible, using the
    // current render method.
    @Override
    public void onResume() {
        updateLayout();
        super.onResume();
    }

    // Clean up data structures on termination.
    @Override
    public void onDestroy() {
        Log.d(Constants.LOG_TAG, "onDestroy: HelloLayoutsControl");
        utils.deactivate();
    };

    @Override
    public void onTouch(final ControlTouchEvent event) {
        super.onTouch(event);

        Log.d(Constants.LOG_TAG,"onTouch: HelloLayoutsControl " + " - " + event.getX() + ", " + event.getY());

        if (event.getAction() != Control.Intents.TOUCH_ACTION_RELEASE) {
            return;
        }

        //updates the count and the Imageview
        updateUI();
    }

    /**
     * Renders a layout to the SmartEyeglass display.
     * Displays an ImageView (a button) and a TextView (a caption).
     * For each view you would like to customize, pass a layout reference
     * ID and a data bundle to showLayout().
     *
     * When you use the layout method for displaying TextView
     * elements, they are rendered using an optimized SST font.
     *
     * @see Control.Intents#EXTRA_DATA_XML_LAYOUT
     * @see Registration.LayoutSupport
     */
    private void updateLayout() {
        // retrieve the caption string
        String caption = context.getString(R.string.text_tap_to_update);

        List<Bundle> list = new ArrayList<Bundle>();

        // Prepare a bundle to update the TextView for the button caption.
        Bundle textBundle = new Bundle();
        textBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE,
                R.id.btn_update_this);
        textBundle.putString(Control.Intents.EXTRA_TEXT, caption);
        list.add(textBundle);

        // Prepare a bundle to update the ImageView for the button icon.
        Bundle imageBundle = new Bundle();
        imageBundle.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.image);
        imageBundle.putString(Control.Intents.EXTRA_DATA_URI,
                getUriString(R.drawable.icon_extension48));
        list.add(imageBundle);

        // Display the view elements from layout.xml
        showLayout(R.layout.layout, list.toArray(new Bundle[list.size()]));
    }

    /*
    * This method updates the count and the imageview
    * on the layout after tapping
    * */
    public void updateUI() {

        iconImage = !iconImage;

        // Update the text for this screen
        sendText(R.id.btn_update_this, getCaption());

        // Update the image of an ImageView in the screen
        sendImage(R.id.image, getImageId());
    }


    public int getImageId() {
        return (iconImage) ? R.drawable.icon_extension48
                : R.drawable.actions_view_in_phone;
    }

    /**
     * Extracts a display string for the current screen from resources.
     * @return      The display string.
     */
    private String getCaption() {
        return context.getString(R.string.text_tap_to_update);
    }

    /**
     * Retrieves the URI string corresponding to a resource ID.
     *
     * @param id The resource ID.
     * @return   The URI string.
     */
    private String getUriString(final int id) {
        return ExtensionUtils.getUriString(context, id);
    }

    /**
     * Resets the state.
     */
    public void reset() {
        iconImage = true;
    }

}
