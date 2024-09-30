# JNI Code Editor

A simple and efficient code editor that utilizes Java Native Interface (JNI) for enhanced performance and functionality. This project showcases the integration of native code with Java, allowing for seamless execution of complex tasks while maintaining the simplicity of Java.


![image](https://github.com/user-attachments/assets/127cbc4d-5b36-46cb-85b6-0a0d68a351b3)

## Features

- **Syntax Highlighting**: Supports multiple programming languages with customizable syntax highlighting.
- **Code Completion**: Intelligent code suggestions to speed up development.
- **Cross-Platform**: Works on Windows, macOS, and Linux due to the flexibility of JNI.
- **Lightweight**: Fast performance with a small footprint.

## Additional Features

- **Generate C++ Functions**: 
  - In the editor, you can easily generate C++ functions for native methods defined in your Java code. 
  - To do this, simply right-click on the method name in your Java code and select the "Generate C++ Function" option. This will automatically create a C++ implementation for the selected Java method, saving you time and reducing the likelihood of errors when writing native functions manually.
- **Generate DLLs**: 
  - The editor also supports the generation of Dynamic Link Libraries (DLLs) for your C++ code.
  - To create a DLL, right-click on your desired C++ file and select the "Generate DLL" option. This will compile the native code and produce a DLL file that can be used with your Java application, making it easy to integrate native functionality.
- **Run Configurations**: 
  - You can set up run configurations to streamline the execution of your Java application that uses the generated DLL.
  - To create a new run configuration, go to the "Run" menu and select "Add". Here, you can specify the main class and library path for your application.

## Requirements

- Java Development Kit (JDK) 8 or higher
- C/C++ Compiler (e.g., GCC for Linux, Visual Studio for Windows)
- Maven (for building the project)
- JAVA_HOME: This variable should point to your JDK installation directory.
- PATH: Make sure that the bin directory of your JDK is included in your system's PATH.
- GCC: Ensure that the path to the GCC binary is also included in the PATH.
