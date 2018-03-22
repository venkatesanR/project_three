package com.designprinciples.structural;

public abstract class AbstractDirectoryAction {
	// create directory
	// open directory
	// delete directory
	// rename directory
	// list all files
	// delete file
	// delete files
	public abstract void create(String directoryName);

	public abstract void delete(String fileName);

	public abstract void printTree();
}
