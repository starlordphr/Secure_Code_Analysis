# CS230-Secure_Code_Analysis
Mini Project for CS230 (Spring 17) UCLA.

# Problem Description:
To implement a plugin for statically checking if a program has followed secure coding techniques.

# Methodology:
Input: Source Code (Java)\n
Output: Secure Coding Warnings
We will use Google's Error-Prone tool for creating our own secure checker. We will be using selected secure coding practices from "CERT Java Secure Coding.pdf" for our project.

# Evaluation:
We will use source codes from some open source projects for secure coding analysis of these projects.

# Important Links:
1. Error-Prone Main Page:
http://errorprone.info/

2. Custom Check using plugin:
https://github.com/google/error-prone/tree/master/examples/plugin/gradle

3. Concrete Example of a Custom Check (The Syntax Tree Stuff :P):
https://github.com/google/error-prone/blob/master/examples/plugin/gradle/sample_plugin/src/main/java/com/google/errorprone/sample/MyCustomCheck.java
