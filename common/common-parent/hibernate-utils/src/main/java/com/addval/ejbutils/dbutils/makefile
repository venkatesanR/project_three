
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\ejbutils\dbutils

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\EJBDateTime.class\
$(COMMON_OBJ_DIR)\EJBColumn.class\
$(COMMON_OBJ_DIR)\EJBConverter.class\
$(COMMON_OBJ_DIR)\EJBIntConverter.class\
$(COMMON_OBJ_DIR)\EJBFloatConverter.class\
$(COMMON_OBJ_DIR)\EJBLongConverter.class\
$(COMMON_OBJ_DIR)\EJBDoubleConverter.class\
$(COMMON_OBJ_DIR)\EJBStringConverter.class\
$(COMMON_OBJ_DIR)\EJBCharConverter.class\
$(COMMON_OBJ_DIR)\EJBDateConverter.class\
$(COMMON_OBJ_DIR)\EJBTimeConverter.class\
$(COMMON_OBJ_DIR)\EJBDateTimeConverter.class\
$(COMMON_OBJ_DIR)\EJBDowConverter.class\
$(COMMON_OBJ_DIR)\EJBShortConverter.class\
$(COMMON_OBJ_DIR)\EJBKeyConverter.class\
$(COMMON_OBJ_DIR)\EJBConverterFactory.class\
$(COMMON_OBJ_DIR)\EJBRecord.class\
$(COMMON_OBJ_DIR)\EJBCriteriaColumn.class\
$(COMMON_OBJ_DIR)\EJBCriteria.class\
$(COMMON_OBJ_DIR)\EJBResultSetMetaData.class\
$(COMMON_OBJ_DIR)\EJBResultSet.class\
$(COMMON_OBJ_DIR)\EJBStatement.class\


all: $(OBJ_LIST)

cleanupjava:
	del /q *.jv~

clean:
	del /q $(COMMON_OBJ_DIR)\*.class

# dependencies
$(COMMON_OBJ_DIR)\EJBDowConverter.class		: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBTimeConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBCharConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBDoubleConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBFloatConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBConverterFactory.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBIntConverter.class 	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBStringConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBDateTimeConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class
$(COMMON_OBJ_DIR)\EJBKeyConverter.class	: $(COMMON_OBJ_DIR)\EJBConverter.class