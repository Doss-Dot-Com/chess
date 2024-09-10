<h1>Java code</h1>

1. Code -> Java C
   
```bash
pbpaste > <Enter Code File Here>.java
```
1. Java C -> Java
   
```bash
javac <Filename>.java
```
1. Java -> Execution
   
```bash
java <Filename>.java
```

<h1>Set Project JDK</h1>
- Set it to either `Amazon Corretto` or `Oracle`


```java
public class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public void sleep() {
        System.out.printf("%s is sleeing", name);
    }
}
```

<h1>Basic Comments</h1>

<h2> List </h2>

```java
String[] strings = new String[10];
int[] numbers = new int[5]
```

<h2>For loop</h2>

```java
for (var 1 = 0; i < name.length; i++) {
    System.out.printprintln(names[i]);
}
```