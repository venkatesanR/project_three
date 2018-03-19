
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com\addval\environment

!INCLUDE $(COMMON_CLIENT_HOME)\source\common.mk

OBJ_LIST=\
$(COMMON_OBJ_DIR)\Environment.class\
$(COMMON_OBJ_DIR)\AppInitServlet.class\
$(COMMON_OBJ_DIR)\JdbcEnvironment.class\
$(COMMON_OBJ_DIR)\OrbEnvironment.class\
$(COMMON_OBJ_DIR)\EJBEnvironment.class\

all: $(OBJ_LIST)

clean:
	del /q $(OBJ_LIST)
