
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com/addval/utils


include $(PROJ_HOME)/source/makefile.proj

OBJ_LIST=\
$(CLASS_DIR)/XGeneral.class\
$(CLASS_DIR)/XRuntime.class\
$(CLASS_DIR)/CnfgFileMgr.class\
$(CLASS_DIR)/LogFileMgr.class\
$(CLASS_DIR)/SelfTest.class\
$(CLASS_DIR)/XFeatureNotImplemented.class

all: $(OBJ_LIST)

clean:
	$(RM) $(CLASS_DIR)/*.class
	$(RM) $(PROJ_DOC_DIR)/$(CURR_DIR)/*.html
	$(RM) $(PROJ_SRC_DIR)/$(CURR_DIR)/*.bak; $(RM) $(PROJ_SRC_DIR)/(CURR_DIR)/*~	

