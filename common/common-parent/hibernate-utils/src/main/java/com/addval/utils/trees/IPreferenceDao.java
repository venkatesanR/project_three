package com.addval.utils.trees;

import java.util.List;
import java.util.Map;

public interface IPreferenceDao {
	public List<IColumnDTO> mapDataAttributeDim(String daoSQL,Map<String,Object> sqlParams);
}
