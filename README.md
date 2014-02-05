logback-request
===============

So this is an appender for [Logback](http://logback.qos.ch/) that writes logging events to an in-memory [ThreadLocal](http://docs.oracle.com/javase/7/docs/api/java/lang/ThreadLocal.html) ArrayList.  The use case for this appender is the ability to redirect some logging events to this ThreadLocal appender and then be able to access those logged events and render them in some way.  The original inspiration was for a web app to be able render logged messages at the bottom of the HTTP response.

Basic Configuration
---------------

Add this repository to your pom.xml:

```XML
<repositories>
    <repository>
        <id>wvuong-github-mvn-repo</id>
        <url>https://raw.github.com/wvuong/logback-request/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

Add this dependency to your pom.xml:

```XML
<dependency>
    <groupId>com.willvuong</groupId>
    <artifactId>logback-request</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Add the appender to your **logback.xml** or **logback-test.xml** and configure:

```XML
<configuration scan="true" scanPeriod="30 seconds" debug="true">

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="THREADLOCAL" class="com.willvuong.logbackrequest.ThreadLocalMemoryAppender"/>

    <logger name="com.willvuong" level="TRACE"/>

    <root level="WARN">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="THREADLOCAL"/>
    </root>
</configuration>
```

So this is the basic configuration to get this dependency into your project and configure Logback to log events to it.

If you want programmatic access to the ThreadLocal, do something like this:

```JAVA
// a collection of events
ThreadLocalMemoryAppender.ThreadLocalHolder.getLoggedEvents();
// or as JSON
ThreadLocalMemoryAppender.ThreadLocalHolder.getBufferAsJson(null, null);

// it is a ThreadLocal afterall, so you'll have to figure out when you want to clear the
// appender's log buffer
ThreadLocalMemoryAppender.ThreadLocalHolder.clearLoggedEvents();
```

Servlet Web App Configuration
---------------

Included in this library are two useful Filters for web applications.
* LogbackResponseServletFilter - serializes the logged events per HttpServletRequest into JSON and then
injects it into the HttpServletResponse as JavaScript.  At the end of the request lifecycle, the ThreadLocal
buffer is flushed.
* RequestMDCServletFilter - optional, makes available some handy values into the MDC for logging purposes.

Add these filters to your **web.xml**:

```XML
    <!-- optional filter -->
    <filter>
        <filter-name>requestMdcFilter</filter-name>
        <filter-class>com.willvuong.logbackrequest.RequestMDCServletFilter</filter-class>
    </filter>

    <filter>
        <filter-name>logbackResponseFilter</filter-name>
        <filter-class>com.willvuong.logbackrequest.LogbackResponseServletFilter</filter-class>
    </filter>

    <!-- optional filter mapping -->
    <filter-mapping>
        <filter-name>requestMdcFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <filter-mapping>
        <filter-name>logbackResponseFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

What am I supposed to do with JSON?
---------------

You might be interested in [logback-request-ui](https://github.com/wvuong/logback-request-ui)!