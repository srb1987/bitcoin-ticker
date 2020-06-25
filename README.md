# Task Update

- MVVM architecture
- LiveData and Coroutines used
- Unit tests of ViewModel class
- Preferred future tasks - Update UI to be more funky rather than functional


# CMC Markets Android Skeleton Project


## Setup

Clone this repository into your local machine and open in your Android Studio.

- Android Studio 3.5.2+
- gradle-5.4.1 +
- Android grade plugin 3.5.3 +
- Compile SDK 29
- Build tool 29.0.3

* You may need to add a couple of properties to support Androidx for third part libraries in your gradle.properties:
```
android.useAndroidX=true
android.enableJetifier=true
```

### Dependencies
Most of dependencies likely to use are already added but you can freely add dependencies to build.gradle file if you need.

## Project Structure

There are four top level packages:

#### core package
`core` packages includes framework related classes. Daggger modules belongs to this one. So, you can add new Dagger module to `core.di.module` package if you need.

| Sub package | Comments |
|--|--|
| di | Dependency Injection |
| di.module | Dagger modules |
| di.viewmodel | ViewModelFactory & ViewModelKey annotation |

#### data package
`data`package contains data sources and this project has `BitcoinApi` class for ticker api call.
| Sub package | Comments |
|--|--|
| api | BitcoinApi interface |
| model | Api response class(es) |

#### feature package
Presentation layer is located in this package.
| Sub package | Comments | 
|--|--|
| main | MainActivity |
| orderticket | OrderTicketFragment & OrderTicketViewModel |

#### utils package
| file | Comments |
|--|--|
| CurrenyExtensions.kt | Extension function to display number format |
| ViewExtensions.kt | Extension functions for toast and hideKeyboard |


## Implementation

### Design Pattern
Should use MVVM(Model-ViewModel-Model) pattern.  Use `feature.orderticket.OrderTicketViewModel` for your implementation.
You are recommended to use `LiveData` to communicate with `OrderTicketFragment`.


### Data Layer
Update `API_BASE_URL` constant in`core.di.module.NetworkModule` class to the Base URL of the one given in the instruction.
Provide a function to fetch latest data in`data.api.BitcoinApi` class. This class is used by Retrofit and DI is already implemented in `core.di.module.NetworkModule` class.
Kotlin Coroutines are our preferences for API call but you can use RxJava as well.
You might use intermediate layer like Repository, Interactor or UseCase pattern to handle API call and you are recommended to use DI when you create an instance of it.
Consider to separate business data object from ui data object when you do modelling of the API.


### View Layer
All your ui related code will be in `feature` package and you can simply code in `OrderTicketFragment` and `OrderTicketViewModel` class for your implementation.
You can use extension functions in `utils` packages if you need to do price formatting, showing toast message or hide soft keyboard.

### Unit test
We strongly recommend you to write some unit tests. At least test for `OrderTicketViewModel`. For example, you may need to verfiy if your polling logic is working fine. Use clear naming for each test functions by using enclosed backtikcs if you need. 
```kotlin
class MyTestCase {
     @Test fun `ensure everything works`() { /*...*/ }
     
     @Test fun ensureEverythingWorks_onAndroid() { /*...*/ }
}
```


