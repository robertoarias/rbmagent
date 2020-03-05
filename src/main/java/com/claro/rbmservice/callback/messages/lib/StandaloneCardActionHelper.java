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

import java.util.ArrayList;
import java.util.List;

import com.claro.rbmservice.callback.messages.lib.SuggestionDialActionHelper;
import com.claro.rbmservice.callback.messages.lib.cards.MediaHeight;
import com.google.api.services.rcsbusinessmessaging.v1.model.CardContent;
import com.google.api.services.rcsbusinessmessaging.v1.model.Suggestion;

/**
 * Utility class for CardContent data.
 */
public class StandaloneCardActionHelper {
    private String title;
    private String description;
    private String imageFileUrl;
    private List<SuggestionRecord> suggestions;

    public StandaloneCardActionHelper(String title,
                                String description,
                                String imageFileUrl,
                                List<SuggestionRecord> suggestions) {
        this.title = title;
        this.description = description;
        this.imageFileUrl = imageFileUrl;
        this.suggestions = suggestions;
    }

    public StandaloneCardActionHelper(String title,
                                String description,
                                String imageFileUrl,
                                SuggestionRecord suggestion) {
        this(title, description, imageFileUrl, new ArrayList<SuggestionRecord>());
   	    suggestions.add(suggestion);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public List<SuggestionRecord> getSuggestions() {
        return suggestions;
    }

    /**
     * Converts this helper object into an RBM card.
     * @return CardContent object.
     */
    public CardContent getCardContentAction() {
        return getCardContentAction(MediaHeight.MEDIUM);
    }

    /**
     * Converts this helper object into an RBM card with actions.
     * @param height The height of the media element.
     * @return CardContent object.
     */
    public CardContent getCardContentAction(MediaHeight height) {
        RbmApiHelper rbmApiHelper = new RbmApiHelper();

        // convert the suggestion helpers into actual suggested actions
        List<Suggestion> suggestedActions = new ArrayList<Suggestion>();
        for(SuggestionRecord suggestion: suggestions) {
			switch (suggestion.getTypeAction())
			{
				case 1: 
					SuggestionDialActionHelper dialActionHelper = (SuggestionDialActionHelper) suggestion.getSuggestionAction();
		        	suggestedActions.add(dialActionHelper.getSuggestedDialAction());					
					break;

				case 2: 
					SuggestionOpenUrlActionHelper openUrlActionHelper = (SuggestionOpenUrlActionHelper) suggestion.getSuggestionAction();
		        	suggestedActions.add(openUrlActionHelper.getSuggestedOpenUrlAction());					
					break;
			}        	
        }

        // create the card content
        CardContent cardContent = rbmApiHelper.createCardContent(
                title,
                description,
                imageFileUrl,
                height,
                suggestedActions
        );

        return cardContent;
    }
    
}
// [END of the standalone card help wrapper class]
