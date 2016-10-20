# segment-java

Displaying good parse error messages was hard, until I created this!

## Usage

It's very simple: you wrap a `String` and add the source file, start line, and start column, like so:

```java
Segment segment = new Segment(text, "somefile.txt", startLine, startCol);
```
`Segment` implements `CharSequence` and has almost the exact same API as `String` so you can use it just like you would
use a `String` in parsing.  And here's why you want to: as you `split` it or make `substring`s, etc., it returns new `Segments` 
that know where they are located in the file:

```java
for (Segment line : segment.split("\r\n|\r|\n") {
    System.out.println(line.startLine);
}
```
prints:
```
0
1
2
...etc
```
It also provides an `underlineInContext` method that's useful for showing where syntax errors are located:
```java
Segment segment = new Segment("there is an error here", "somefile.txt", 0, 0);
System.out.println(segment.split("\\s+")[4].underlineInContext());
```
prints:
```
there is an error here
                  ^^^^
```

This package also provides `SegmentMatcher` since it would be impossible to call `Pattern.matcher(segment)`:
```java
SegmentMatcher matcher = new SegmentMatcher(new Segment("hello world"), "somefile.txt", 0, 0), Pattern.compile("world"));
matcher.find();
Segment match = matcher.group();
System.out.println(match.underlineInContext());
```
prints:
```
hello world
      ^^^^^
```
