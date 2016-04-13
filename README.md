# j2oc: Java VM to Objective C Cross-Compiler

Back in 2014 I started working on a cross compiler which was capable of compiling Java classes to Objective C. I got the basic core compiler working, along with a port of some core Java API libraries. The intent was to allow simple Java applications to be ported to Objective C, so they can be run on iOS.

But other tasks got in the way, and now I'm publishing the code here in case someone else finds it of use. I've also included the notes from the original web site which contained the source kit.

From the original web page:

## Statement of Intent

To create a Java to Objective C compiler which can:

* Compile Java code generated from JVM 1.6 to Objective C.
* Correctly handle exceptions constructs thrown within Java.
* Provide a minimum subset of the Java RTL (similar to that provided by GWT)
* Generate correct code that honors the Objective C rules for retain/release/autorelease
* Generate code that can be called from Objective C and which can call into Objective C code

## Goals:

My eventual goal is to provide a mechanism that can be used to recompile a Java module and link it into an Objective C program running on the iPhone or iPad. The purpose is not to create an entire Java run time library for building iPhone and iPad applications in Java, only to allow me to use some Java code within the iPhone or iPad.

## Downloads:

Because of Apple's change of rules regarding cross-compiled code, I've decided to start uploading the project in its current state. Please see my blog posting [here](http://chaosinmotion.com/blog/?p=578) for more information. _The complete sources can be found on this GitHub repository._

As time permits I will describe in detail how the code works, and provide plenty of examples of how to use the code. My hope is that the code or bits of it will be of use to other people.

Meanwhile, if you are interested in a more compile solution you may wish to investigate the [XMLVM project.](http://www.xmlvm.org/overview/)

William Woody  
woody@alumni.caltech.edu
