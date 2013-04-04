package com.daylenyang.tab;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Persistence {

	static void save(Tournament obj, String fileName) throws IOException {
		FileOutputStream outputFile = new FileOutputStream(fileName);
		ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
		
		outputStream.writeObject(obj);
		outputStream.flush();
		outputStream.close();
	}
	
	static Tournament load(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream inputFile = new FileInputStream(fileName);
		ObjectInputStream inputStream = new ObjectInputStream(inputFile);
		
		Tournament obj = (Tournament) inputStream.readObject();
		
		inputStream.close();
		return obj;
	}
	
}
