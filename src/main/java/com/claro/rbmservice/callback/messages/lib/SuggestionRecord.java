package com.claro.rbmservice.callback.messages.lib;

public class SuggestionRecord {
    private Object suggestionAction;
	private Integer typeAction;

    public SuggestionRecord(Object suggestionAction, Integer typeAction) {
        this.suggestionAction = suggestionAction;
        this.typeAction = typeAction;
    }

    public Object getSuggestionAction() {
		return suggestionAction;
	}

	public void setSuggestionAction(Object suggestionAction) {
		this.suggestionAction = suggestionAction;
	}

	public Integer getTypeAction() {
		return typeAction;
	}

	public void setTypeAction(Integer typeAction) {
		this.typeAction = typeAction;
	}
	
}
