/*
 *  Copyright (c) 2016 Touch Instinct
 *
 *  This file is part of RoboSwag library.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ru.touchin.templates.validation;


import java.io.Serializable;

/**
 * Created by Ilia Kurtov on 24/01/2017.
 * TODO: fill
 */
public class ValidationState implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Initial state of validation. It indicates that no validation rules applied yet.
     */
    public static final ValidationState INITIAL = new ValidationState();
    /**
     * Valid state.
     */
    public static final ValidationState VALID = new ValidationState();
    /**
     * Error shows when model (e.g. DateTime) is failing on conversion from raw data (e.g. from String) for validation.
     */
    public static final ValidationState ERROR_CONVERSION = new ValidationState();
    /**
     * Error shows when we don't need to show any description of error (e.g. just highlight input field with red color).
     */
    public static final ValidationState ERROR_NO_DESCRIPTION = new ValidationState();

}
