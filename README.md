# JDXAndroidManyToManyExample
### A Simple Android Project Demonstrating Persistence of Objects (Involved in a Many-to-Many Relationship) Using JDXA ORM

Many-to-many relationship is an important object modeling requirement for many applications.

This project exemplifies how JDXA ORM and associated utilities can be used to easily develop an Android app that exchanges data of inter-related domain model objects, which are involved in many-to-many relationships, with an SQLite database. The object model used by this app has a many-to-many relationship. One user may have many groups and one group may have many users.

Some highlights:  
*	The object model consists of two inter-related POJO classes (User and Group).
*	The many-to-many relationship among User and Group is realized using an intermediate join class (UserGroup).
*	The collection of many objects may be a List, an array, or a Vector.
*	The declarative mapping specification (in the file .../res/raw/manytomany_example.jdx) is simple, intuitive, non-intrusive, and succinct.  
*	API calls for CRUD operations are simple and flexible. They show the following features: 
    -	Deep query,	Shallow query
*	JDXA provides handy utility methods for displaying an object or a list of objects.  


To run this app in your own setup, please do the following:
*	Clone this project on your desktop.
*	Get the JDXA SDK download instructions from [this link](http://softwaretree.com/v1/products/jdxa/download-jdxa.php).
*	You may download just the mini version of the SDK.
*	Add the libraries (JDXAndroid-nn.n.jar and sqldroid.jar) from the SDK to the app/libs directory and build the project.
*	Run the app.  

### About JDXA ORM 
JDXA is a simple yet powerful, non-intrusive, flexible, and lightweight Object-Relational Mapping (ORM) product that simplifies and accelerates the development of Android apps by providing intuitive, object-oriented access to on-device relational (e.g., SQLite) data.  

Adhering to some well thought-out [KISS (Keep It Simple and Straightforward) principles](http://softwaretree.com/v1/KISSPrinciples.html), JDXA boosts developer productivity and reduces maintenance hassles by eliminating endless lines of tedious SQL code.  

Some of the powerful and practical features of JDXA include: 
*	Declarative mapping specification between an object model and a relational model is done textually using a simple grammar (no XML complexity). 
*	Full flexibility in domain object modeling – one-to-one, one-to-many, and many-to-many relationships as well as class-hierarchies supported.
*	POJO (Plain Old Java Objects) friendly non-intrusive programming model, which does not require you to change your Java classes in any way:   

    - No need to subclass your domain model classes from any base class
    - No need to clutter your source code with annotations
    - No source code generation (No need for DAO classes)
    - No pre-processing or post-processing of your code  

*	Support for persistence of JSON objects.
*	A small set of intuitive APIs for object persistence.
*	Automatic generation of relational schema from an object model. 
*	A highly optimized metadata-driven ORM engine that is lightweight, dynamic, and flexible.   

JDXA ORM is a product of Software Tree. To get more information and a free trial version of JDXA SDK, please visit http://www.softwaretree.com.  

JDXA is used with the SQLDroid open source library. SQLDroid is provided under the licensing terms mentioned [here](https://github.com/SQLDroid/SQLDroid/blob/master/LICENSE).



