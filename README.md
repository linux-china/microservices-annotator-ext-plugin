Microservices Annotator ext-plugin
===================================

<!-- Plugin description -->

[Microservices Annotator](https://plugins.jetbrains.com/plugin/18361-microservices-annotator) ext plugin with annotations support.

Usage:

* Add Microservices Annotator's annotation dependency

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>microservices-annotator</artifactId>
    <version>0.2.0</version>
</dependency>
```

* Add annotation in the code

```java

@RemoteAccess
public interface UserService {
    String findNickById(Integer id);
}
```

* Microservices annotate icon will be in gutter when APIs called.

```
String nick = userService.findNickById(1);
```

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "microservices-annotator-ext-plugin"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/linux-china/microservices-annotator-ext-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

# References

* Microservices Annotator annotations: https://github.com/linux-china/microservices-annotator
* Microservices Annotator plugin: https://plugins.jetbrains.com/plugin/18361-microservices-annotator