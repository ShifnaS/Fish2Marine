/*
 * Copyright 2016 Smacon Technologies Pvt Ltd as an unpublished work. All Rights
 * Reserved.
 *
 * The information contained herein is confidential property of Cutesys Technologies
 * Pvt Ltd. The use, copying,transfer or disclosure of such information is prohibited
 * except by express written agreement with Company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * File Name 					: METValidator
 * Since 						: 06/09/17
 * Version Code & Project Name	: v 1.0 Fish2marinelibrary
 * Author Name					: Aiswarya Saju
 */

package com.smacon.f2mlibrary.Edittext.validation;

import android.support.annotation.NonNull;

/**
 * Created by Aiswarya on 06/09/17.
 */
public abstract class METValidator {

  protected String errorMessage;

  public METValidator(@NonNull String errorMessage) {
    this.errorMessage = errorMessage;
  }
  @NonNull
  public String getErrorMessage() {
    return this.errorMessage;
  }
  public abstract boolean isValid(@NonNull CharSequence text, boolean isEmpty);
}