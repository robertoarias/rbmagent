/*
 * Copyright (C) 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.claro.rbmservice.callback.messages.lib;

import com.google.api.services.rcsbusinessmessaging.v1.model.DialAction;
import com.google.api.services.rcsbusinessmessaging.v1.model.SuggestedAction;

// [START of the suggestion help wrapper class]

import com.google.api.services.rcsbusinessmessaging.v1.model.SuggestedReply;
import com.google.api.services.rcsbusinessmessaging.v1.model.Suggestion;

/**
 * Utility class for Suggestion postbackData and text.
 */
public class SuggestionDialActionHelper extends SuggestionHelper {
    private DialAction dialAction;

    public SuggestionDialActionHelper(String text, String postbackData, DialAction dialAction) {
        super(text, postbackData);
    	this.dialAction = dialAction;
    }

    public DialAction getDialAction() {
        return dialAction;
    }

    public void setDialAction(DialAction dialAction) {
        this.dialAction = dialAction;
    }

    /**
     * Converts this suggestion helper object into a RBM suggested dial action.
     * @return The Suggestion object as a suggested dial action.
     */
    public Suggestion getSuggestedDialAction() {
        SuggestedAction action = new SuggestedAction();
        action.setText(super.getText());
        action.setPostbackData(super.getPostbackData());
        action.setDialAction(this.dialAction);

        Suggestion suggestion = new Suggestion();
        suggestion.setAction(action);

        return suggestion;
    }
}
// [END of the suggestion help wrapper class]
