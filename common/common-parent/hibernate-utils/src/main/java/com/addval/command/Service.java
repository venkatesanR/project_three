/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.command;

public interface Service {
	public void execute(Command cmd) throws CommandException;
}