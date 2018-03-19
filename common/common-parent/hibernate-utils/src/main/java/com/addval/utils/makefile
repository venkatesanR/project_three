
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\utils

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\AVConstants.class\
$(COMMON_OBJ_DIR)\XGeneral.class\
$(COMMON_OBJ_DIR)\XRuntime.class\
$(COMMON_OBJ_DIR)\CnfgFileMgr.class\
$(COMMON_OBJ_DIR)\LogFileMgr.class\
#$(COMMON_OBJ_DIR)\DBPoolMgr.class\
$(COMMON_OBJ_DIR)\CacheInitializer.class\
$(COMMON_OBJ_DIR)\CacheMgr.class\
$(COMMON_OBJ_DIR)\SelfTest.class\
$(COMMON_OBJ_DIR)\XFeatureNotImplemented.class\
$(COMMON_OBJ_DIR)\ServiceInterface.class\
$(COMMON_OBJ_DIR)\GenericService.class\
$(COMMON_OBJ_DIR)\CommandOption.class\
$(COMMON_OBJ_DIR)\CommandLine.class\
$(COMMON_OBJ_DIR)\Assert.class\
$(COMMON_OBJ_DIR)\ErrorMessageTranslator.class

all: $(OBJ_LIST)

clean:
	del /q $(OBJ_LIST)
