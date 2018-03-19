package com.addval.springstruts;

import java.util.List;

import com.addval.ui.UIComponentMetadata;

public interface SaveFilterForm {
	public final String SAVE_FILTER_ACTION = "SaveFilter";
	public final String DELETE_FILTER_ACTION = "DeleteFilter";
	public final String APPLY_FILTER_ACTION = "ApplyFilter";
	public void setFilterName(String filterName);
	public String getFilterName();
	public void setFilter(String jsonString);
	public String getFilter();
	public List<UIComponentMetadata> getUserSearchFilters();
	public void setUserSearchFilters(List<UIComponentMetadata> userSearchFilters);
}
