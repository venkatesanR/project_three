package com.addval.trees;

import java.util.List;
import java.util.Map;

import com.addval.datapreference.APreferenceService;
import com.addval.datapreference.PreferenceCacheUtility;

public interface IPreferenceCalcHelper {
	public List<IColumnDTO> mapDataAttributeDim(String daoSQL,Map<String,Object> sqlParams);
	public long computePreference(List<IColumnDTO> columnDTOs,APreferenceService prefSrv);
	public PreferenceCacheUtility getPreferenceCacheUtility();
}
