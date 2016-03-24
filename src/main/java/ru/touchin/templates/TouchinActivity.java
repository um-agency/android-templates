package ru.touchin.templates;

import ru.touchin.roboswag.components.navigation.ViewControllerActivity;
import ru.touchin.roboswag.components.utils.Logic;

/**
 * Created by Gavriil Sitnikov on 11/03/16.
 * TODO: description
 */
public abstract class TouchinActivity<TLogic extends Logic> extends ViewControllerActivity<TLogic> {

    protected TouchinActivity() {
        super();
    }

}
