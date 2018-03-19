
# order matters!!!
# define current directory relative to ~source
CURR_DIR = com/addval/environment

include $(PROJ_HOME)/source/makefile.proj

OBJ_LIST=\
$(CLASS_DIR)/Environment.class\
$(CLASS_DIR)/AppInitServlet.class


all: $(OBJ_LIST)

clean:
	$(RM) $(CLASS_DIR)/*.class
	$(RM) $(PROJ_DOC_DIR)/$(CURR_DIR)/*.html
	$(RM) $(PROJ_SRC_DIR)/$(CURR_DIR)/*.bak; $(RM) $(PROJ_SRC_DIR)/$(CURR_DIR)/*~


#Dependencies
$(CLASS_DIR)/AppInitServlet.class:		$(CLASS_DIR)/Environment.class