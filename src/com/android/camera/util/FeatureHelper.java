/*
 * Copyright (C) 2019 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class FeatureHelper {
    private static final String PERMISSION_PREVENT_POWER_KEY = "shiftos.permission.PREVENT_POWER_KEY";

    public static boolean isPowerShutterSupported(final Context context) {
        return (context.checkSelfPermission(PERMISSION_PREVENT_POWER_KEY)
                == PackageManager.PERMISSION_GRANTED);
    }
}