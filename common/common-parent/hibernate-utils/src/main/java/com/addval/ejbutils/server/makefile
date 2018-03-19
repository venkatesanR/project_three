
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\ejbutils\server

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\EJBSQLBuilderUtils.class\
$(COMMON_OBJ_DIR)\EJBEEditorMetaDataBeanPK.class\
$(COMMON_OBJ_DIR)\EJBEEditorMetaDataRemote.class\
$(COMMON_OBJ_DIR)\EJBEEditorMetaDataHome.class\
$(COMMON_OBJ_DIR)\EJBEEditorMetaDataBean.class\
$(COMMON_OBJ_DIR)\EJBSEditorMetaDataBean.class\
$(COMMON_OBJ_DIR)\EJBSEditorMetaDataRemote.class\
$(COMMON_OBJ_DIR)\EJBSEditorMetaDataHome.class\
$(COMMON_OBJ_DIR)\EJBSTableManagerRemote.class\
$(COMMON_OBJ_DIR)\EJBSTableManagerHome.class\
$(COMMON_OBJ_DIR)\EJBSTableManagerBean.class\
#$(COMMON_OBJ_DIR)\EJBDBPool.class

all: $(OBJ_LIST)

clean:
	del /q $(OBJ_LIST)

# dependencies
$(COMMON_OBJ_DIR)\EJBEEditorMetaDataBean.class	: $(COMMON_OBJ_DIR)\EJBEEditorMetaDataBeanPK.class $(COMMON_OBJ_DIR)\EJBEEditorMetaDataHome.class $(COMMON_OBJ_DIR)\EJBEEditorMetaDataRemote.class
$(COMMON_OBJ_DIR)\EJBSTableManagerBean.class	: $(COMMON_OBJ_DIR)\EJBSTableManagerHome.class $(COMMON_OBJ_DIR)\EJBSTableManagerRemote.class
