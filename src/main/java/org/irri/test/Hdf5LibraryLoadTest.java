package org.irri.test;

public class Hdf5LibraryLoadTest {

	static {
        System.load("/Library/Java/Extensions/libjhdf5.jnilib");  // This will try to load "libjhdf5.jnilib" (without "lib" and ".jnilib")
    }

    // Declare the native method with the same signature
    public native void testNativeMethod();

    public static void main(String[] args) {
        System.out.println("Starting JNI Test...");

        try {
        	Hdf5LibraryLoadTest test = new Hdf5LibraryLoadTest();
            test.testNativeMethod();  // Call the native method
            System.out.println("Library loaded successfully!");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Error loading the library: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
