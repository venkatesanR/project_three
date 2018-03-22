package com.designprinciples.structural;

import java.io.FileNotFoundException;

public interface IDirectory {
	public void addFile(String fileName);

	public void deleteFile(String fileName) throws FileNotFoundException;

	public void printTree();
}
