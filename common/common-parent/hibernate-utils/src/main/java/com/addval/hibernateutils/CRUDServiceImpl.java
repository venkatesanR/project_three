/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.hibernateutils;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;

import com.addval.command.Command;
import com.addval.command.CommandException;
import com.addval.command.Service;
import com.addval.environment.Environment;
import com.addval.utils.LogMgr;
import com.addval.utils.StrUtl;

public class CRUDServiceImpl implements Service, Serializable {
	private static transient final Logger _logger = LogMgr.getLogger(CRUDServiceImpl.class);

	private static final long serialVersionUID = 1L;
	private GenericDao _genericDao;
	private Environment environment;

	public CRUDServiceImpl() {
	}

	public GenericDao getGenericDao() {
		return _genericDao;
	}

	public void setGenericDao(GenericDao aDao) {
		_genericDao = aDao;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void execute(Command incmd) throws CommandException {
		CRUDCommand cmd = (CRUDCommand) incmd;
		if (validateInputs(cmd) == false) {
			return;
		}
		if (getGenericDao() == null) {
			throw new CommandException(0, "GenericDao not initialized");
		}
		try {
			if (cmd.getAction().equals(CRUDCommand.ACTION_INSERT)) {
				// If cmd has been loaded with non-null entityName, call create
				// method that takes an entityName input argument;
				// otherwise call the standard create method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					getGenericDao().create(cmd.getValue(), cmd.getEntityName());
				}
				else {
					getGenericDao().create(cmd.getValue());
				}
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_DELETE)) {
				// FIRST
				// since this could be a transient object
				// first we need to load (read) the object, to see if it exists
				// in the db
				//
				Object obj = null;
				// If cmd has been loaded with non-null entityName, call read
				// method that takes an entityName input argument;
				// otherwise call the standard read method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					obj = getGenericDao().read(cmd.getKey(), cmd.getEntityName(), true);
				}
				else {
					obj = getGenericDao().read(cmd.getKey(), cmd.getValue().getClass(), true);
				}

				// SECOND: if the object exists in the db, delete it
				if (obj != null) {
					if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
						getGenericDao().delete(obj, cmd.getEntityName());
					}
					else {
						getGenericDao().delete(obj);
					}
				}
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_UPDATE)) {
				// If cmd has been loaded with non-null entityName, call update
				// method that takes an entityName input argument;
				// otherwise call the standard update method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					getGenericDao().update(cmd.getValue(), cmd.getEntityName());
				}
				else {
					getGenericDao().update(cmd.getValue());
				}
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_MERGE)) {
				// If cmd has been loaded with non-null entityName, call merge
				// method that takes an entityName input argument;
				// otherwise call the standard merge method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					getGenericDao().merge(cmd.getValue(), cmd.getEntityName());
				}
				else {
					getGenericDao().merge(cmd.getValue());
				}
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_READ)) {
				Object obj = null;
				// If cmd has been loaded with non-null entityName, call read
				// method that takes an entityName input argument;
				// otherwise call the standard read method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					obj = getGenericDao().read(cmd.getKey(), cmd.getEntityName(), false);
				}
				else {
					obj = getGenericDao().read(cmd.getKey(), cmd.getValue().getClass(), false);
				}
				// Set the object read into the command
				cmd.setReadOutput(obj);
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_SEARCH)) {
				List l = null;
				// If cmd has been loaded with non-null entityName, call merge
				// method that takes an entityName input argument;
				// otherwise call the standard merge method.
				if (!StrUtl.isEmptyTrimmed(cmd.getEntityName())) {
					l = getGenericDao().findByExample(cmd.getValue(), cmd.getEntityName());
				}
				else {
					l = getGenericDao().findByExample(cmd.getValue());
				}
				// Set the list of objects read into the cmd
				cmd.setSearchOutput(l);
			}

			if (cmd.getAction().equals(CRUDCommand.ACTION_SEARCH_BY_CRITERIA)) {
				if (cmd.getCountOnly()) {
					cmd.getCriteria().setProjection(Projections.rowCount());
					List c = getGenericDao().findByCriteria(cmd.getCriteria(), 0, -1);
					Integer count = new Integer(0);
					if (c.size() > 0) {
						if (c.get(0) instanceof java.lang.Integer) {
							count = (Integer) c.get(0);
						}
						else if (c.get(0) instanceof java.lang.Long) {
							count = new Integer(((Long) c.get(0)).intValue());
						}
					}
					cmd.setSearchResultCount(count.intValue());
				}
				else {
					if (cmd.getAliasToBean() != null) {
						cmd.getCriteria().setResultTransformer(Transformers.aliasToBean(cmd.getAliasToBean()));
					}
					else {
						cmd.getCriteria().setResultTransformer(Criteria.ROOT_ENTITY);
					}
					List l = getGenericDao().findByCriteria(cmd.getCriteria(), -1, -1);
					cmd.setSearchOutput(l);
				}
				cmd.getCriteria().setProjection(null);
			}
		}
		catch (DataAccessException ex) {
			getGenericDao().clear();
			// Remove all objects from the Session cache, and cancel all pending saves, updates and deletes
			if (ex.getRootCause() != null && ex.getRootCause() instanceof SQLException) {
				SQLException sqlEx = (SQLException) ex.getRootCause();
				if (getEnvironment() != null) {
					// translate the SQL error
					String errorMessage = getEnvironment().getDbPoolMgr().getSQLExceptionTranslator().translate(sqlEx);
					if (errorMessage != null) {
						throw new CommandException(errorMessage);
					}
				}
				throw new CommandException(sqlEx.getErrorCode(), sqlEx.getMessage());
			}
			_logger.error("Error in executing command", ex);
			throw new CommandException(0, ex.getMessage());
		}
		catch (Exception ex) {
			// Remove all objects from the Session cache, and cancel all pending
			// saves, updates and deletes
			getGenericDao().clear();
			_logger.error("Error in executing command", ex);
			throw new CommandException(0, ex.getMessage());
		}
	}

	private boolean validateInputs(CRUDCommand cmd) {

		cmd.setSuccessFlag(true);
		cmd.setFailureReason(null);

		if (cmd.getValue() == null) {
			cmd.setSuccessFlag(false);
			cmd.setFailureReason("Object for CRUD should be passed");
		}

		if (cmd.getAction() == null) {
			cmd.setSuccessFlag(false);
			cmd.setFailureReason("Action for CRUD should be set");
		}

		if (cmd.getAction().equals(CRUDCommand.ACTION_READ)) {
			if (cmd.getKey() == null) {
				cmd.setSuccessFlag(false);
				cmd.setFailureReason("Primary Key should be set for read action");
			}
		}

		if (cmd.getAction().equals(CRUDCommand.ACTION_SEARCH_BY_CRITERIA)) {
			if (cmd.getCriteria() == null) {
				cmd.setSuccessFlag(false);
				cmd.setFailureReason("Criteria is required for search by criteria action");
			}
		}

		return cmd.getSuccessFlag();
	}

}