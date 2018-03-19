
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\taglib

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\ListTag.class\
$(COMMON_OBJ_DIR)\EditTag.class\
$(COMMON_OBJ_DIR)\ForEachTag.class\
$(COMMON_OBJ_DIR)\HeadingTag.class\
$(COMMON_OBJ_DIR)\FieldTag.class\
$(COMMON_OBJ_DIR)\FieldTei.class\
$(COMMON_OBJ_DIR)\IfTag.class\
$(COMMON_OBJ_DIR)\MetaDataTag.class\
#$(COMMON_OBJ_DIR)\RecordTag.class\
$(COMMON_OBJ_DIR)\ResultSetTag.class\

all:  $(OBJ_LIST)

cleanupjava:
	del /q *.jv~

clean:
	del /q $(OBJ_LIST)
