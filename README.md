# Palladium
Palladium is a DI framework which is based on and enhances Guice.

The entry point for Palladium is the InjectorFactory.
To create an injector and initialise the automatic binding, use InjectorFactory.createInjector(String packageName).

## Enhancements

### AutoBinding
##### Reflections Based Auto Binding
One of the core aspects of Palladium is the automatic binding of Interfaces via Reflections. 
Interfaces which have exactly one implementation are automatically collected and bound to their implementation.
This method does not support assisted injection in any form as of now.

##### Scoping
Palladium uses the default guice scoping mechanisms. @Singleton is automatically regarded during injector calls and constructor/field injection.
Furthermore, @EagerSingleton can be used to bind classes for eager initialisation.
For more information, please visit https://github.com/google/guice/wiki/Scopes

##### Exclusions
If the Palladium injector is created for a package, all interfaces with a single implementation are bound. 
Classes that have to be excluded from this process, because they have to be bound in a different modules for example, have to be annotated with an `@ExcludeFromAutoBinding` annotation.
If a class posses an annotation that is used as a basis for binding, the @ExcludeFromAutoBinding can also be used transitively for one layer (i.e. an Annotation which itself is annotated).

##### EnforceTestImplementationBinding
In certain use cases it might be necessary to automatically replace a binding in the test scope. Palladium provides the 
`@EnforceTestImplementationBinding` annotation, in order to automatically bind the specified implementation to the interface and ignores all other implementations.

__*Note:* This should only be used in tests.__

In the following example, the MockedTestService will be used as bound implementation for the Service interface.
```java
public interface Service {
    void doStuff();
}

@Singleton
public class DefaultService implements Service {

    @Override
    public void doStuff() {
        // do something
    }
}

// This is located in the test package
@Singleton
@EnforceTestImplementationBinding
public class MockedTestService extends DefaultService {

    @Override
    public void doStuff() {
        // do something different
    }
}
```

### SubTypesFactory
Palladium also includes a Subtypes-functionality. Interfaces can be marked with the `@SubtypesConstructable` annotation to mark them as usable for the SubTypesFactory classes.
SubTypesFactories require a base interface and a "Key Extractor" which determines which implementation of the interface is returned for which requesting "key".

##### Example
```java
/**
* Factory to map a key type to a target type that will automatically be 
* resolved via the supportedType within the respective implementations
*/
public class CustomFactory extends SubTypesFactory<KeyType, TargetType> {
    
    @Inject
    public CustomFactory() {
        super(TargetType.class, TargetType::getSupportedType);
    }
}
```
```java
@SubTypeConstructable
public class TargetTypeImpl implements TargetType {
    public KeyType getSupportedType() {
        return KeyType.EXAMPLE_1;
    }
}
```

##### Key Extractors
A key extractor is a function that calls on the basic interface and produces a key for the specific class it is called for.
In the above example, the key extractor function would return "KeyType.EXAMPLE_1" as a value for TargetTypeImpl. 
Hence, the factory would return TargetTypeImpl if it's called for KeyType.EXAMPLE_1  

##### SubTypesInheritableFactory
Sometimes, there are types in a hierarchy that don't have to be treated specifically or where a default factory should be implemented.
For such cases, it's possible to use a SubTypesInheritableFactory to represent or simulate a type hierarchy.
In the above Example, if the factory is called for KeyType.EXAMPLE_0, it would throw an exception. 
If we define that KeyType.EXAMPLE_1 is an ancestor of this key, we could still return with a default result.
Each SubTypesInheritableFactory contains the "getAncestors()" method, which is used to define the hierarchy used for the given type of key. 

##### SubTypesClassFactory
Extending the approach mentioned in SubTypesInheritableFactory, it's possible to have "Class" as a key type and use the class hierarchy as a parent structure.
By that, we enable users to specify, for example, implementations for Object as a default and for Numeric.
If the factory would then be called for Double, it would still return successfully (with the implementation of Numeric) since Numeric is a parent of Double.


### Custom Injector
The PalladiumInjector can be used to retrieve instances of all implementations for a given interface.
```java
public class CustomClass {
    private final PalladiumInjector injector;
    
    @Inject
    public CustomClass(PalladiumInjector injector) {
        this.injector = injector;
        
        Set<? extends SomeInterface> instances = injector.getInstances(SomeInterface.class);
    }
}
```