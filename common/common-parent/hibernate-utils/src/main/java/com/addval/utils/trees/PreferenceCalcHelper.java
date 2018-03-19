package com.addval.utils.trees;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.addval.datapreference.APreferenceService;
import com.addval.datapreference.AttribValueInfo;
import com.addval.datapreference.PreferenceCacheUtility;
import com.addval.utils.ListUtl;
import com.addval.utils.trees.IColumnDTO;
import com.addval.utils.trees.IPreferenceCalcHelper;
import com.addval.utils.trees.IPreferenceDao;


public class PreferenceCalcHelper implements IPreferenceCalcHelper {
	
private IPreferenceDao dao;	

private PreferenceCacheUtility preferenceCacheUtility;

public List<IColumnDTO> mapDataAttributeDim(String daoSQL,Map<String,Object> sqlParams)
{
	return this.getDao().mapDataAttributeDim(daoSQL,sqlParams);
}
	
public long computePreference(List<IColumnDTO> columnDTOs,APreferenceService prefSrv)
{
     long pref = 0;
     Map<Object,AttribValueInfo> dimAttribValueMap =getDimAttribValueMap(columnDTOs);
     if(prefSrv == null)return pref;
     pref = prefSrv.calculatePreference(dimAttribValueMap, null);
     return pref;
}

private Map<Object,AttribValueInfo> getDimAttribValueMap(List<IColumnDTO> columnDTOs)
{
	Map<Object,AttribValueInfo> dimAttribValueMap =new HashMap<Object,AttribValueInfo>();
	if(!ListUtl.isEmpty(columnDTOs))
	{
		for (IColumnDTO iColumnDTO : columnDTOs) {
			if(iColumnDTO.getValue()!=null)
			{
				dimAttribValueMap.put(iColumnDTO.getDataAttributeDim(), new AttribValueInfo(iColumnDTO.getValue(), iColumnDTO.getDataAttributeCode()));
			}
		}
	}
	return dimAttribValueMap;
}
public IPreferenceDao getDao() {
	return dao;
}

public void setDao(IPreferenceDao dao) {
	this.dao = dao;
}

public PreferenceCacheUtility getPreferenceCacheUtility() {
	return preferenceCacheUtility;
}

public void setPreferenceCacheUtility(
		PreferenceCacheUtility preferenceCacheUtility) {
	this.preferenceCacheUtility = preferenceCacheUtility;
}

}
