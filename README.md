# Introduction

EfficientString is a small class I developed when working with a large amount of strings in a small side project. Basically the default string class in Java wastes a lot of memory and has a hashcode function that is not suitable in combination with a very large hash table (think tens of millions of entries). Basically insert speed drops because the number of buckets is not large enough. I ran into this even before getting to a million entries.

This project was inspired by the way Lucene efficiently stores Strings as utf-8. My ambition with this project is to gradually add functionality to support large scale processing of String content in all sorts of ways.

Efficient string does a few things:

* It stores utf-8 byte arrays. Depending on the script, this can save quite a bit of space. Even Asian content tends to have some mixed use of scripts that make utf-8 the better choice under some circumstances. Your mileage may vary of course but for latin based languages, utf-8 is pretty much as good as it gets.
* It provides a reasonable hash function based on the CRC and a modulo of 50000 (so you have a maximum of 50K buckets in a HashMap<EfficientString>). This allows for reasonably speedy inserts and lookups in HashMaps without creating too much memory overhead for additional buckets. The hash code is created once, during construction.
* It provides an int index that uniquely identifies the String. This allows you to use int arrays to store references to strings instead of using e.g. lists or sets.
* It uses a custom, memory efficient bimap like data structure to look up efficient strings by their index or vice versa. 
* It provides a fast equals function that uses the int index.
* Naturally, efficient strings are immutable.
* An int int multi map is provided that allows you to build memory efficient multi maps of efficient strings using their integer id.


# Get it from Maven Central

```
<dependency>
    <groupId>com.jillesvangurp</groupId>
    <artifactId>efficientstring</artifactId>
    <version>1.11</version>
</dependency>
```

Note. check for the latest version. I do not always update the readme.

# Building

It's a maven project. So, checking it out and doing a mvn clean install should do the trick.

Should anyone like this licensed differently, please contact me.

If anyone wants to fix stuff just send me a pull request.

Alternatively, you can exercise your rights under the license and simply copy and adapt. The [license](https://github.com/jillesvangurp/efficientstring/blob/master/LICENSE) allows you to do this and I have no problems with this.

# Changelog

* 1.5-1.9
    * Use read/write lock and get rid of synchronized to fix issue where client sees a null value while it is being inserted.
* 1.4-1.6
    * fix race condition with partial update being seen by client code
    * now requires java 1.7
    * use murmur32 instead of CRC
* 1.3
    * Add bytes() method to get to the utf-8 bytes directly; useful for serialization
* 1.2
    * Add clear method
    * Add IntIntMultiMap to store string references efficiently.
    * Improve synchronization and locking
    * Misc bug fixes
* 1.1
    * replace guava bimap with a more memory efficient custom structure.
* 1.0
    * first release