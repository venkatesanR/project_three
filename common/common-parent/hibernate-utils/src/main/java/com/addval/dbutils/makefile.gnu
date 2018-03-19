
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com/addval/dbutils

include $(PROJ_HOME)/source/makefile.proj

OBJ_LIST=\
$(CLASS_DIR)/Converter.class\
$(CLASS_DIR)/DateConverter.class\
$(CLASS_DIR)/StringConverter.class\
$(CLASS_DIR)/IntConverter.class\
$(CLASS_DIR)/DBColumnInfo.class\
$(CLASS_DIR)/DBTableInfo.class\
$(CLASS_DIR)/DBSchemaInfo.class\
$(CLASS_DIR)/DBConnection.class\
$(CLASS_DIR)/DBStatement.class\
$(CLASS_DIR)/DBPool.class\
$(CLASS_DIR)/OracleAlertListener.class\
$(CLASS_DIR)/RecID.class\
$(CLASS_DIR)/RSIterAction.class\
$(CLASS_DIR)/RSPageDesc.class\
$(CLASS_DIR)/RSIterator.class\
$(CLASS_DIR)/TableManager.class\
$(CLASS_DIR)/SelfTest.class\
$(CLASS_DIR)/IDVal.class\

all: $(OBJ_LIST)

clean:
	$(RM) $(CLASS_DIR)/*.class
	$(RM) $(PROJ_DOC_DIR)/$(CURR_DIR)/*.html
	$(RM) $(PROJ_SRC_DIR)/$(CURR_DIR)/*.bak; $(RM) $(PROJ_SRC_DIR)/$(CURR_DIR)/*~


# dependencies
$(CLASS_DIR)/DateConverter.class: 	$(CLASS_DIR)/Converter.class
$(CLASS_DIR)/StringConverter.class: 	$(CLASS_DIR)/Converter.class
$(CLASS_DIR)/IntConverter.class	: 	$(CLASS_DIR)/Converter.class
$(CLASS_DIR)/DBColumnInfo.class	: 	$(CLASS_DIR)/Converter.class $(CLASS_DIR)/StringConverter.class $(CLASS_DIR)/DateConverter.class $(CLASS_DIR)/IntConverter.class
$(CLASS_DIR)/DBTableInfo.class	: 	$(CLASS_DIR)/DBColumnInfo.class
$(CLASS_DIR)/DBSchemaInfo.class	: 	$(CLASS_DIR)/DBTableInfo.class 
$(CLASS_DIR)/DBConnection.class : 	$(CLASS_DIR)/DBStatement.class
$(CLASS_DIR)/DBPool.class: 		$(CLASS_DIR)/DBSchemaInfo.class $(CLASS_DIR)/DBConnection.class
$(CLASS_DIR)/RSIterator.class: 		$(CLASS_DIR)/RSIterAction.class $(CLASS_DIR)/RSPageDesc.class
$(CLASS_DIR)/TableManager.class	:	$(CLASS_DIR)/RecID.class $(CLASS_DIR)/DBTableInfo.class $(CLASS_DIR)/RSIterator.class 