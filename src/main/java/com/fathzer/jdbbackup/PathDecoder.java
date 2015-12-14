package com.fathzer.jdbbackup;

public interface PathDecoder {
	String decodePath(String path) throws IllegalNamePattern;
}
