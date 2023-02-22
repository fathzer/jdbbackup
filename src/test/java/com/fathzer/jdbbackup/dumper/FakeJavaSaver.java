package com.fathzer.jdbbackup.dumper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.fathzer.jdbbackup.Options;

public final class FakeJavaSaver extends DBSaverFromProcess {
	public static final List<String> CONTENT = Arrays.asList("Hello,","This is a fake db dump");
	@Override
	public String getDBType() {
		return "java";
	}

	@Override
	protected List<String> getCommand(Options params) {
		List<String> args = Arrays.asList("java","-cp","./target/classes"+File.pathSeparator+"./target/test-classes",FakeJavaSaver.class.getName());
		System.out.println(args);
		return args;
	}
	
	public static void main(String[] args) {
		CONTENT.forEach(System.out::println);
	}
}