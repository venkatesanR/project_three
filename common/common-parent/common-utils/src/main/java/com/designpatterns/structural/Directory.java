package com.designpatterns.structural;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.jmodule.exceptions.InvalidInputException;

public class Directory implements IDirectory {
	private String directoryName;
	private List<Directory> childDirectorys;
	private List<String> files;

	public String getDirectoryName() {
		return directoryName;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public List<Directory> getChildDirectorys() {
		return childDirectorys;
	}

	public void setChildDirectorys(List<Directory> childDirectorys) {
		this.childDirectorys = childDirectorys;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public void deleteFile(String fileName) throws FileNotFoundException {
		if (files == null || files.isEmpty()) {
			throw new InvalidInputException("No files in given directory");
		}
		int fileIndex = files.indexOf(fileName);
		if (fileIndex == -1)
			throw new FileNotFoundException("No such file Exists");
		files.remove(fileIndex);
	}

	@Override
	public void addFile(String fileName) {
		if (files == null || files.isEmpty()) {
			files = new ArrayList<String>();
		}
		files.add(fileName);
	}

	@Override
	public void printTree() {
		StringBuilder builder = new StringBuilder(directoryName);
		builder.append("\t\t \n");
		builder.append("| \n");
		builder.append("| \n");
		builder.append("+ Files \n");
		builder.append("\t\t \n");
		builder.append("| \n");
		builder.append("| \n");
		if (files != null || !files.isEmpty()) {
			for (String file : files) {
				builder.append('+').append(file);
			}
		}
	}
}
