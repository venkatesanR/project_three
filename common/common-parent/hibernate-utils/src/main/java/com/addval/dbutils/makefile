
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\dbutils

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\Converter.class\
$(COMMON_OBJ_DIR)\DataConverter.class\
$(COMMON_OBJ_DIR)\DateConverter.class\
$(COMMON_OBJ_DIR)\CurrencyConverter.class\
$(COMMON_OBJ_DIR)\StringConverter.class\
$(COMMON_OBJ_DIR)\IntConverter.class\
$(COMMON_OBJ_DIR)\DBColumnInfo.class\
$(COMMON_OBJ_DIR)\DBTableInfo.class\
$(COMMON_OBJ_DIR)\DBSchemaInfo.class\
$(COMMON_OBJ_DIR)\DBConnection.class\
$(COMMON_OBJ_DIR)\DBResultSet.class\
$(COMMON_OBJ_DIR)\DBPreparedStatement.class\
$(COMMON_OBJ_DIR)\DBCallableStatement.class\
$(COMMON_OBJ_DIR)\DBStatement.class\
$(COMMON_OBJ_DIR)\DBPoolMgr.class\
$(COMMON_OBJ_DIR)\OracleAlertListener.class\
$(COMMON_OBJ_DIR)\RecID.class\
$(COMMON_OBJ_DIR)\RSIterAction.class\
$(COMMON_OBJ_DIR)\RSPageDesc.class\
$(COMMON_OBJ_DIR)\RSIterator.class\
$(COMMON_OBJ_DIR)\TableManager.class\
$(COMMON_OBJ_DIR)\IDVal.class\
$(COMMON_OBJ_DIR)\SelfTest.class\
$(COMMON_OBJ_DIR)\IteratorCriteria.class\
$(COMMON_OBJ_DIR)\IteratorStatus.class\
$(COMMON_OBJ_DIR)\RStoObjectConverter.class\
$(COMMON_OBJ_DIR)\Selector.class\

all: $(OBJ_LIST)

clean:
	del /q $(COMMON_OBJ_DIR)\*.class


# dependencies
$(COMMON_OBJ_DIR)\RecID.class		:
$(COMMON_OBJ_DIR)\RSIterAction.class	:
$(COMMON_OBJ_DIR)\RSPageDesc.class	:
$(COMMON_OBJ_DIR)\Converter.class	:
$(COMMON_OBJ_DIR)\DBStatement.class     :
$(COMMON_OBJ_DIR)\DateConverter.class	: $(COMMON_OBJ_DIR)\Converter.class
$(COMMON_OBJ_DIR)\CurrencyConverter.class	: $(COMMON_OBJ_DIR)\Converter.class
$(COMMON_OBJ_DIR)\StringConverter.class	: $(COMMON_OBJ_DIR)\Converter.class
$(COMMON_OBJ_DIR)\IntConverter.class	: $(COMMON_OBJ_DIR)\Converter.class
$(COMMON_OBJ_DIR)\DBColumnInfo.class	: $(COMMON_OBJ_DIR)\Converter.class $(COMMON_OBJ_DIR)\StringConverter.class $(COMMON_OBJ_DIR)\DateConverter.class $(COMMON_OBJ_DIR)\IntConverter.class
$(COMMON_OBJ_DIR)\DBTableInfo.class	: $(COMMON_OBJ_DIR)\DBColumnInfo.class
$(COMMON_OBJ_DIR)\DBSchemaInfo.class	: $(COMMON_OBJ_DIR)\DBTableInfo.class
$(COMMON_OBJ_DIR)\DBConnection.class    : $(COMMON_OBJ_DIR)\DBStatement.class
$(COMMON_OBJ_DIR)\DBPoolMgr.class		: $(COMMON_OBJ_DIR)\DBSchemaInfo.class $(COMMON_OBJ_DIR)\DBConnection.class
$(COMMON_OBJ_DIR)\RSIterator.class	: $(COMMON_OBJ_DIR)\RSIterAction.class $(COMMON_OBJ_DIR)\RSPageDesc.class
$(COMMON_OBJ_DIR)\TableManager.class	: $(COMMON_OBJ_DIR)\RecID.class $(COMMON_OBJ_DIR)\DBTableInfo.class $(COMMON_OBJ_DIR)\RSIterator.class
