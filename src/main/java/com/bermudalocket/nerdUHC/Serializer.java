package com.bermudalocket.nerdUHC;

import java.io.*;

class Serializer {

	private static final String PATH = "savedstates/";

	static void saveObject(Serializable serializableObject, String fileName) throws IOException {
		fileName = PATH + fileName;
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));

		objectOutputStream.writeObject(serializableObject);
		objectOutputStream.close();
	}

	static Object readObject(String fileName) throws IOException, ClassNotFoundException {
		fileName = PATH + fileName;
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(fileName));

		Object object = objectInputStream.readObject();
		objectInputStream.close();

		return object;
	}

}
