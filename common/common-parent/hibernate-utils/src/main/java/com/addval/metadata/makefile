
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\metadata

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\RecordStatus.class\
$(COMMON_OBJ_DIR)\ColumnDataType.class\
$(COMMON_OBJ_DIR)\ColumnInfo.class\
$(COMMON_OBJ_DIR)\ColumnMetaData.class\
$(COMMON_OBJ_DIR)\EditorMetaData.class\
$(COMMON_OBJ_DIR)\UIMetaData.class\

all:  $(OBJ_LIST)

cleanupjava:
	del /q *.jv~

clean:
	del /q $(OBJ_LIST)
